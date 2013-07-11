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
import edu.uw.cs.lil.tiny.tempeval.structures.NewTemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.Timex;
import edu.uw.cs.utils.composites.Pair;

public class TemporalEvaluationThread extends Thread {
	final int cvFold;
	final int MAX_MENTION_LENGTH = 5;
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

	private boolean hasOverlap (int x1, int x2, int y1, int y2) {
		// Whether the first and second string at respective indexes have overlapping regions
		return x2 >= y1 && x1 <= y2;
	}

	private List<Pair<Integer, Integer>> getCorrectMentions(List<Timex> timexes, List<Pair<Integer, Integer>> predictedMentions) {
		List<Pair<Integer, Integer>> correctMentions = new LinkedList<Pair<Integer, Integer>>();
		for (Timex t : timexes) {
			for(Pair<Integer, Integer> p: predictedMentions) {
				if (hasOverlap(p.first(), p.second(), t.getStartToken(), t.getEndToken()))
					correctMentions.add(p);
			}
		}
		return correctMentions;
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

		for (NewTemporalSentence ts : trainData) {
			numGoldMentions += ts.getNumMentions();
			JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> jointDataItemModel = 
					new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, ts);
			List<Pair<Integer, Integer>> predictedMentions = getPredictedMentions(ts.getTokens(), jointDataItemModel);
			numPredictedMentions += predictedMentions.size();
			List<Pair<Integer, Integer>> correctMentions = getCorrectMentions(ts.getTimexes(), predictedMentions);
			numCorrectMentions += correctMentions.size();
			if (correctMentions.size() < ts.getNumMentions()) {
				System.out.printf ("False negatives: '%s'\n", ts);
				System.out.printf("\tGold mentions:\n");
				for(Timex t : ts.getTimexes())
					System.out.printf("\t%s\n", t.getText());
			}
			if (correctMentions.size() < predictedMentions.size()) {
				System.out.printf ("False positives: '%s'\n", ts);
				System.out.printf("\tPredicted mentions:\n");
				for(Pair<Integer, Integer> p: predictedMentions)
					System.out.printf("\t%s\n", ts.prettyString(p.first(), p.second()));
			}
		}
		System.out.println("\nMention detection stats:");
		System.out.printf("Recall: %f\n", getRecall(numCorrectMentions, numGoldMentions, numPredictedMentions));
		System.out.printf("Precision: %f\n", getPrecision(numCorrectMentions, numGoldMentions, numPredictedMentions));
		System.out.printf("F1: %f\n", getF1(numCorrectMentions, numGoldMentions, numPredictedMentions));
	}

	private double getRecall(int correct, int gold, int predicted) {
		return ((double) correct)/gold;
	}

	private double getPrecision(int correct, int gold, int predicted) {
		return ((double) correct)/predicted;
	}

	private double getF1(int correct, int gold, int predicted) {
		double r = getRecall(correct, gold, predicted);
		double p = getPrecision(correct, gold, predicted);
		return 2*r*p/(r+p);
	}
}