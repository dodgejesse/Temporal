package edu.uw.cs.lil.tiny.tempeval;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservation;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservationDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.Timex;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDetectionTester {
	final private int MAX_MENTION_LENGTH = 5;
	private TemporalStatistics stats;
	private TemporalDataset testData;
	private JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> dummyDataItemModel;
	private AbstractCKYParser<LogicalExpression> parser;
	private TemporalObservationDataset correctObservations;
	
	public TemporalDetectionTester(TemporalDataset testData, AbstractCKYParser<LogicalExpression> parser, ILexicon<LogicalExpression> fixed) {
		this.testData = testData;
		this.parser = parser;
		
		JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model = new JointModel.Builder<Sentence, String[], LogicalExpression, LogicalExpression>()
				.setLexicon(new Lexicon<LogicalExpression>()).build();
		model.addFixedLexicalEntries(fixed.toCollection());

		// dataItem doesn't actually matter, since there is no scoring involved
		dummyDataItemModel = new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, new TemporalObservation());		
	
		stats = new TemporalStatistics();
		correctObservations = new TemporalObservationDataset();
	}
	
	public TemporalStatistics test() {
		int sentenceCount = 0;
		for (TemporalSentence ts : testData) {
			sentenceCount++;
			if (sentenceCount % 100 == 0)
				System.out.printf("Evaluating %d/%d sentences...\n", sentenceCount, testData.size());
			stats.addGold(ts.getTimexes().size());
			List<Pair<Integer, Integer>> predictedMentions = getPredictedMentions(ts.getTokens(), dummyDataItemModel);
			stats.addPredicted(predictedMentions.size());
			List<Timex> correctMentions = getCorrectMentions(ts.getTimexes(), predictedMentions);
			stats.addCorrect(correctMentions.size());
			if (correctMentions.size() < ts.getTimexes().size()) {
				System.out.printf ("False negative from '%s':\n", ts.prettyString());
				for(Timex t : getFalseNegatives(correctMentions, ts.getTimexes()))
					System.out.printf("[%s]", t.getText());
				System.out.println();
			}
			if (correctMentions.size() < predictedMentions.size()) {
				System.out.printf ("False positive from '%s':\n", ts.prettyString());
				for(Pair<Integer, Integer> p : getFalsePositives(correctMentions, predictedMentions))
					System.out.printf("[%s]", ts.prettyString(p.first(), p.second()));
				System.out.println();
			}
			if (!correctMentions.isEmpty())
				correctObservations.addObservations(ts.getObservations(correctMentions));
		}
		return stats;
	}
	
	private List<IParseResult<LogicalExpression>> pruneLogicWithLambdas(List<IParseResult<LogicalExpression>> allModelParses){
		List<IParseResult<LogicalExpression>> newAllModelParses = new ArrayList<IParseResult<LogicalExpression>>();
		for (IParseResult<LogicalExpression> p : allModelParses){
			if (!p.getY().getType().isComplex())
				newAllModelParses.add(p);
		}
		return newAllModelParses;
	}

	private List<Timex> getCorrectMentions(List<Timex> timexes, List<Pair<Integer, Integer>> predictedMentions) {
		List<Timex> correctMentions = new LinkedList<Timex>();
		for (Timex t : timexes) {
			for (Pair<Integer, Integer> p : predictedMentions) {
				if (TemporalStatistics.hasOverlap(p.first(), p.second(), t.getStartToken(), t.getEndToken())) {
					correctMentions.add(t);
					break;
				}
			}
		}
		return correctMentions;
	}

	private List<Timex> getFalseNegatives(List<Timex> correctMentions, List<Timex> goldMentions) {
		List<Timex> falseNegatives = new LinkedList<Timex>(goldMentions);
		for (Timex gm : goldMentions) {
			for(Timex cm : correctMentions) {
				if (TemporalStatistics.hasOverlap(cm.getStartToken(), cm.getEndToken(), gm.getStartToken(), gm.getEndToken()))
					falseNegatives.remove(gm);
			}
		}
		return falseNegatives;
	}

	private List<Pair<Integer, Integer>> getFalsePositives(List<Timex> correctMentions, List<Pair<Integer, Integer>> predictedMentions) {
		List<Pair<Integer, Integer>> falsePositives = new LinkedList<Pair<Integer, Integer>>(predictedMentions);
		for (Pair<Integer, Integer> p : predictedMentions) {
			for(Timex cm : correctMentions) {
				if (TemporalStatistics.hasOverlap(cm.getStartToken(), cm.getEndToken(), p.first(), p.second()))
					falsePositives.remove(p);
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
	
	public TemporalObservationDataset getCorrectObservations() {
		return correctObservations;
	}
	
	public TemporalStatistics getStatistics() {
		return stats;
	}
}
