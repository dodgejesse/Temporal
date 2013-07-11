package edu.uw.cs.lil.tiny.tempeval;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.Timex;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalUtilities;
import edu.uw.cs.utils.composites.Pair;

public class TemporalEvaluationThread extends Thread {
	final int cvFold;
	final int MAX_MENTION_LENGTH = 3;
	final JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model;
	final TemporalDataset trainData, testData;
	final AbstractCKYParser<LogicalExpression> parser;

	public TemporalEvaluationThread(TemporalDataset trainData,
			TemporalDataset testData,
			AbstractCKYParser<LogicalExpression> parser,
			ILexicon<LogicalExpression> fixed,
			LexicalFeatureSet<Sentence, LogicalExpression> lexPhi,
			int cvFold){

		this.cvFold = cvFold;
		this.trainData = trainData;
		this.testData = testData;
		this.parser = parser;
		model = new JointModel.Builder<Sentence, String[], LogicalExpression, LogicalExpression>()
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		model.addFixedLexicalEntries(fixed.toCollection());
	}

	// To create a list without those parses that still have lambdas.
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

	private List<Pair<Integer, Integer>> getPredictedMentions(List<String> tokens, JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> jointDataItemModel) {
		List<Pair<Integer, Integer>> predictedMentions = new LinkedList<Pair<Integer, Integer>>();
		for (int i = 0 ; i < tokens.size(); i++) {	
			// Work backwards to get the longest possible mention
			for (int span = Math.min(MAX_MENTION_LENGTH, tokens.size() - i)  ; span > 0 ; span--) {
				Sentence mentionCandidate = new Sentence(tokens.subList(i, i + span));
				IParserOutput<LogicalExpression> parserOutput = parser.parse(mentionCandidate, jointDataItemModel);
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

	public void run(){
		int numPredictedMentions = 0;
		int numGoldMentions = 0;
		int numCorrectMentions = 0;

		int sentenceCount = 0;
		for (TemporalSentence ts : testData) {
			sentenceCount++;
			if (sentenceCount % 100 == 0)
				System.out.printf("Evaluating %d/%d sentences...\n", sentenceCount, testData.size());
			numGoldMentions += ts.getTimexes().size();
			JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> jointDataItemModel = 
					new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, ts);
			List<Pair<Integer, Integer>> predictedMentions = getPredictedMentions(ts.getTokens(), jointDataItemModel);
			numPredictedMentions += predictedMentions.size();
			List<Pair<Integer, Integer>> correctMentions = getCorrectMentions(ts.getTimexes(), predictedMentions);
			numCorrectMentions += correctMentions.size();
			if (correctMentions.size() < ts.getTimexes().size()) {
				System.out.printf ("False negatives from '%s':\n", ts.prettyString());
				for(Timex t : getFalseNegatives(correctMentions, ts.getTimexes()))
					System.out.printf("[%s]", t.getText());
				System.out.println();
			}
			if (correctMentions.size() < predictedMentions.size()) {
				System.out.printf ("False positives from '%s':\n", ts.prettyString());
				for(Pair<Integer, Integer> p : getFalsePositives(correctMentions, predictedMentions))
					System.out.printf(" %s", ts.prettyString(p.first(), p.second()));
				System.out.println();
			}
		}
		System.out.println("\nMention detection stats:");
		System.out.printf("Recall: %f\n", TemporalUtilities.getRecall(numCorrectMentions, numGoldMentions, numPredictedMentions));
		System.out.printf("Precision: %f\n", TemporalUtilities.getPrecision(numCorrectMentions, numGoldMentions, numPredictedMentions));
		System.out.printf("F1: %f\n", TemporalUtilities.getF1(numCorrectMentions, numGoldMentions, numPredictedMentions));
	}
}