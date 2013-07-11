package edu.uw.cs.lil.tiny.tempeval;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.learn.simple.joint.JointSimplePerceptron;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalContextFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalDayOfWeekFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featuresets.TemporalReferenceFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservation;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservationDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalResult;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.Timex;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalUtilities;
import edu.uw.cs.utils.composites.Pair;

public class TemporalEvaluationThread extends Thread {
	final private int cvFold;
	final private int MAX_MENTION_LENGTH = 5;
	final private TemporalDataset trainData, testData;
	final private AbstractCKYParser<LogicalExpression> parser;
	final private LexicalFeatureSet<Sentence, LogicalExpression> lexPhi;
	final private int perceptronIterations;
	final private ILexicon<LogicalExpression> fixed;
	public TemporalEvaluationThread(TemporalDataset trainData,
			TemporalDataset testData,
			AbstractCKYParser<LogicalExpression> parser,
			ILexicon<LogicalExpression> fixed,
			LexicalFeatureSet<Sentence, LogicalExpression> lexPhi,
			int perceptronIterations,
			int cvFold){

		this.cvFold = cvFold;
		this.trainData = trainData;
		this.testData = testData;
		this.parser = parser;
		this.lexPhi = lexPhi;
		this.fixed = fixed;
		this.perceptronIterations = perceptronIterations;
	}

	private List<IParseResult<LogicalExpression>> pruneLogicWithLambdas(List<IParseResult<LogicalExpression>> allModelParses){
		List<IParseResult<LogicalExpression>> newAllModelParses = new ArrayList<IParseResult<LogicalExpression>>();
		for (IParseResult<LogicalExpression> p : allModelParses){
			if (!p.getY().getType().isComplex())
				newAllModelParses.add(p);
		}
		return newAllModelParses;
	}

	private List<Pair<Integer, Integer>> getCorrectMentions(List<Timex> timexes, List<Pair<Integer, Integer>> predictedMentions) {
		List<Pair<Integer, Integer>> correctMentions = new LinkedList<Pair<Integer, Integer>>();
		for (Timex t : timexes) {
			for (Pair<Integer, Integer> p : predictedMentions) {
				if (TemporalUtilities.hasOverlap(p.first(), p.second(), t.getStartToken(), t.getEndToken())) {
					correctMentions.add(p);
					break;
				}
			}
		}
		return correctMentions;
	}

	private List<Timex> getFalseNegatives(List<Pair<Integer, Integer>> correctMentions, List<Timex> timexes) {
		List<Timex> falseNegatives = new LinkedList<Timex>(timexes);
		for (Timex t : timexes) {
			for(Pair<Integer, Integer> p : correctMentions) {
				if (TemporalUtilities.hasOverlap(p.first(), p.second(), t.getStartToken(), t.getEndToken()))
					falseNegatives.remove(t);
			}
		}
		return falseNegatives;
	}

	private List<Pair<Integer, Integer>> getFalsePositives(List<Pair<Integer, Integer>> correctMentions, List<Pair<Integer, Integer>> predictedMentions) {
		List<Pair<Integer, Integer>> falsePositives = new LinkedList<Pair<Integer, Integer>>(predictedMentions);
		for (Pair<Integer, Integer> pp : predictedMentions) {
			for(Pair<Integer, Integer> cp : correctMentions) {
				if (pp.equals(cp))
					falsePositives.remove(cp);
			}
		}
		return falsePositives;
	}

	private List<Pair<Integer, Integer>> getPredictedMentions(List<String> tokens, JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> dummyDataItemModel) {
		List<Pair<Integer, Integer>> predictedMentions = new LinkedList<Pair<Integer, Integer>>();
		for (int i = 0 ; i < tokens.size(); i++) {	
			// Work backwards to get the longest possible mention
			for (int span = Math.min(MAX_MENTION_LENGTH, tokens.size() - i)  ; span > 0 ; span--) {
				Sentence mentionCandidate = new Sentence(tokens.subList(i, i + span));
				IParserOutput<LogicalExpression> parserOutput = parser.parse(mentionCandidate, dummyDataItemModel);
				final List<IParseResult<LogicalExpression>> isoParses = pruneLogicWithLambdas(parserOutput.getAllParses());
				if (isoParses.size() > 0) {
					predictedMentions.add(Pair.of(i, i + span));				
					// skip used tokens
					i += span - 1; // - 1 to account for i++ in for-loop
					break;
				}
			}
		}	
		return predictedMentions;
	}

	private JointModel<Sentence, String[], LogicalExpression, LogicalExpression> learnModel(TemporalDataset dataset) {
		TemporalObservationDataset observations = dataset.getObservations();
		final TemporalJointParser jParser = new TemporalJointParser(parser);
		JointSimplePerceptron<Sentence, String[], LogicalExpression, LogicalExpression, TemporalResult> learner = new JointSimplePerceptron<Sentence, String[], LogicalExpression, LogicalExpression, TemporalResult>(
				perceptronIterations, observations, jParser);
		JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model = new JointModel.Builder<Sentence, String[], LogicalExpression, LogicalExpression>()
				.addJointFeatureSet(new TemporalContextFeatureSet())
				.addJointFeatureSet(new TemporalReferenceFeatureSet())
				.addJointFeatureSet(new TemporalDayOfWeekFeatureSet())
				.addLexicalFeatureSet(lexPhi)
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		model.addFixedLexicalEntries(fixed.toCollection());
		learner.train(model);
		return model;
	}


	public void run(){
		JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model = learnModel(trainData);

		int numPredictedMentions = 0;
		int numGoldMentions = 0;
		int numCorrectMentions = 0;

		int sentenceCount = 0;
		JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> dummyDataItemModel = 
				new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, new TemporalObservation());		
		
		for (TemporalSentence ts : testData) {
			sentenceCount++;
			if (sentenceCount % 100 == 0)
				System.out.printf("Evaluating %d/%d sentences...\n", sentenceCount, testData.size());
			numGoldMentions += ts.getTimexes().size();
			List<Pair<Integer, Integer>> predictedMentions = getPredictedMentions(ts.getTokens(), dummyDataItemModel);
			numPredictedMentions += predictedMentions.size();
			List<Pair<Integer, Integer>> correctMentions = getCorrectMentions(ts.getTimexes(), predictedMentions);
			numCorrectMentions += correctMentions.size();
			if (correctMentions.size() < ts.getTimexes().size()) {
				System.out.printf ("False negative from '%s':\n", ts.prettyString());
				for(Timex t : getFalseNegatives(correctMentions, ts.getTimexes()))
					System.out.printf("[%s]", t.getText());
				System.out.println();
			}
			if (correctMentions.size() < predictedMentions.size()) {
				System.out.printf ("False positive from '%s':\n", ts.prettyString());
				for(Pair<Integer, Integer> p : getFalsePositives(correctMentions, predictedMentions))
					System.out.printf(" %s", ts.prettyString(p.first(), p.second()));
				System.out.println();
			}
		}
		System.out.println("\nMention detection stats:");
		System.out.printf("Recall: %.1f\n", 100*TemporalUtilities.getRecall(numCorrectMentions, numGoldMentions, numPredictedMentions));
		System.out.printf("Precision: %.1f\n", 100*TemporalUtilities.getPrecision(numCorrectMentions, numGoldMentions, numPredictedMentions));
		System.out.printf("F1: %.1f\n", 100*TemporalUtilities.getF1(numCorrectMentions, numGoldMentions, numPredictedMentions));
	}
}