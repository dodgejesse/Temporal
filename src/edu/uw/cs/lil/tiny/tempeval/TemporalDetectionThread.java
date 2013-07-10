package edu.uw.cs.lil.tiny.tempeval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
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
import edu.uw.cs.lil.tiny.tempeval.featureSets.TemporalContextFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featureSets.TemporalDayOfWeekFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featureSets.TemporalReferenceFeatureSet;
import edu.uw.cs.lil.tiny.tempeval.featureSets.TemporalTypeFeatureSet;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDetectionThread extends Thread {
	final int cvFold;
	final int MAX_MENTION_LENGTH = 5;
	final JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model;
	final TemporalSentenceDataset train, test;
	final AbstractCKYParser<LogicalExpression> parser;

	public TemporalDetectionThread(TemporalSentenceDataset train,
			TemporalSentenceDataset test,
			AbstractCKYParser<LogicalExpression> parser,
			ILexicon<LogicalExpression> fixed,
			LexicalFeatureSet<Sentence, LogicalExpression> lexPhi,
			int cvFold){

		this.cvFold = cvFold;
		this.train = train;
		this.test = test;
		this.parser = parser;
		model = new JointModel.Builder<Sentence, String[], LogicalExpression, LogicalExpression>()
				//.addParseFeatureSet(
				//		new LogicalExpressionCoordinationFeatureSet<Sentence>(true, true, true))
				//.addParseFeatureSet(
				//		new LogicalExpressionTypeFeatureSet<Sentence>())
				.addJointFeatureSet(new TemporalContextFeatureSet())
				.addJointFeatureSet(new TemporalReferenceFeatureSet())
				.addJointFeatureSet(new TemporalTypeFeatureSet())
				.addJointFeatureSet(new TemporalDayOfWeekFeatureSet())
				.addLexicalFeatureSet(lexPhi)//.addLexicalFeatureSet(lexemeFeats)
				//.addLexicalFeatureSet(templateFeats)
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		// Initialize lexical features. This is not "natural" for every lexical
		// feature set, only for this one, so it's done here and not on all
		// lexical feature sets.
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

	private boolean hasOverlap (int index1, String s1, int index2, String s2) {
		// Whether the first and second string at respective indexes have overlapping regions
		return (index1 + s1.length() - 1) >= index2 && index1 <= (index2 + s2.length() - 1);
	}

	private boolean hasCorrectMention(List<Pair<Integer, String>> predictedMentions, int goldCharNum, String goldMention) {
		for (Pair<Integer, String> p : predictedMentions) {
			if(hasOverlap(p.first(), p.second(), goldCharNum, goldMention.toString())) {
				return true;
			}
		}
		return false;
	}

	private List<Pair<Integer, String>> getPredictedMentions(String fullSentence, JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> jointDataItemModel) {
		List<String> tokens = new Sentence(fullSentence).getTokens();
		tokens = new ArrayList<String>(tokens);
		List<Pair<Integer, String>> predictedMentions = new LinkedList<Pair<Integer, String>>();
		for (int i = 0 ; i < tokens.size(); i++) {	
			String t = tokens.get(i);
			if (t.endsWith("'s"))
				tokens.set(i, t.substring(0, t.length() - 2));
			// Work backwards to get the longest possible mention
			for (int span = Math.min(MAX_MENTION_LENGTH, tokens.size() - i)  ; span > 0 ; span--) {
				Sentence mentionCandidate = new Sentence(tokens.subList(i, i + span));
				IParserOutput<LogicalExpression> parserOutput = parser.parse(mentionCandidate, jointDataItemModel);
				final List<IParseResult<LogicalExpression>> isoParses = pruneLogicWithLambdas(parserOutput.getAllParses());
				if (isoParses.size() > 0) {
					int predictedCharNum = fullSentence.toString().indexOf(mentionCandidate.toString());
					predictedMentions.add(Pair.of(predictedCharNum, mentionCandidate.toString()));					
					// skip used tokens
					i += span - 1; // - 1 to account for i++ in for-loop
					break;
				}
			}
		}	
		return predictedMentions;
	}

	public void run(){
		Map<String, List<Pair<Integer, String>>> predictedMentionsMap = new HashMap<String, List<Pair<Integer, String>>>();
		int numPredictedMentions = 0;
		int numGoldMentions = 0;
		int numCorrectMentions = 0;

		for (final ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult> dataItem : train) {
			// Retrieve relevant fields
			String fullSentence = dataItem.getSample().second()[1].replace("-", " ").replace(",","").toLowerCase();
			if (fullSentence.charAt(fullSentence.length() - 1) == '.')
				fullSentence = fullSentence.substring(0, fullSentence.length() - 1);
			String goldMention = dataItem.getSample().first().toString();
			int goldCharNum = Integer.parseInt(dataItem.getSample().second()[4]);
			JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> jointDataItemModel = 
					new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, dataItem);

			numGoldMentions++;

			// Predict the mentions if they weren't already predicted
			if(!predictedMentionsMap.containsKey(fullSentence)) {
				List<Pair<Integer, String>> predictedMentions = getPredictedMentions(fullSentence, jointDataItemModel);
				predictedMentionsMap.put(fullSentence, predictedMentions);
				numPredictedMentions += predictedMentions.size();
			}

			if (hasCorrectMention(predictedMentionsMap.get(fullSentence), goldCharNum, goldMention.toString())) {
				//System.out.println("Correct mention detected");
				numCorrectMentions++;
			}
			else {
				System.out.println("No correct mention detected");
				// Per-sentence info
				System.out.printf ("Sentence: '%s'\n", fullSentence);
				System.out.printf ("Gold mention [#%d]: '%s'\n", goldCharNum, goldMention);
				for (Pair<Integer, String> p : predictedMentionsMap.get(fullSentence))
					System.out.printf ("Predicted mention [#%d]: '%s'\n", p.first(), p.second());
				System.out.println();
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