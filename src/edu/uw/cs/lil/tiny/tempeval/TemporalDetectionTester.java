package edu.uw.cs.lil.tiny.tempeval;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservation;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservationDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;

public class TemporalDetectionTester {
	final private int MAX_MENTION_LENGTH = 5;
	private TemporalStatistics stats;
	private TemporalDataset testData;
	private JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> dummyDataItemModel;
	private TemporalJointParser jointParser;
	private TemporalObservationDataset correctObservations;
	
	public TemporalDetectionTester(TemporalDataset testData, TemporalJointParser jointParser, ILexicon<LogicalExpression> fixed) {
		this.testData = testData;
		this.jointParser = jointParser;
		
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
			stats.addGold(ts.getMentions().size());
			List<TemporalMention> predictedMentions = getPredictedMentions(ts.getTokens(), dummyDataItemModel);
			stats.addPredicted(predictedMentions.size());
			List<TemporalMention> correctMentions = getCorrectMentions(ts.getMentions(), predictedMentions);
			stats.addCorrect(correctMentions.size());
			if (correctMentions.size() < ts.getMentions().size()) {
				System.out.printf ("False negative from '%s':\n", ts.prettyString());
				for(TemporalMention fn: getFalseNegatives(correctMentions, ts.getMentions()))
					System.out.printf("[%s]", fn);
				System.out.println();
			}
			if (correctMentions.size() < predictedMentions.size()) {
				System.out.printf ("False positive from '%s':\n", ts.prettyString());
				for(TemporalMention fp : getFalsePositives(correctMentions, predictedMentions))
					System.out.printf("[%s]", fp);
				System.out.println();
			}
			if (!correctMentions.isEmpty())
				correctObservations.addObservations(ts.getObservations(correctMentions));
		}
		return stats;
	}
	
	private List<TemporalMention> getCorrectMentions(List<TemporalMention> goldMentions, List<TemporalMention> predictedMentions) {
		List<TemporalMention> correctMentions = new LinkedList<TemporalMention>();
		for (TemporalMention gm : goldMentions) {
			for (TemporalMention pm : predictedMentions) {
				if (gm.matches(pm)) {
					// Fill in gold information from gold mention for evaluation
					pm.mergeWith(gm);
					correctMentions.add(pm);
					break;
				}
			}
		}
		return correctMentions;
	}

	private List<TemporalMention> getFalseNegatives(List<TemporalMention> correctMentions, List<TemporalMention> goldMentions) {
		List<TemporalMention> falseNegatives = new LinkedList<TemporalMention>(goldMentions);
		for (TemporalMention gm : goldMentions) {
			for(TemporalMention cm : correctMentions) {
				if (gm.matches(cm))
					falseNegatives.remove(gm);
			}
		}
		return falseNegatives;
	}

	private List<TemporalMention> getFalsePositives(List<TemporalMention> correctMentions, List<TemporalMention> predictedMentions) {
		List<TemporalMention> falsePositives = new LinkedList<TemporalMention>(predictedMentions);
		for (TemporalMention pm : predictedMentions) {
			for(TemporalMention cm : correctMentions) {
				if (pm.matches(cm))
					falsePositives.remove(pm);
			}
		}
		return falsePositives;
	}

	private List<TemporalMention> getPredictedMentions(List<String> tokens, JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> dummyDataItemModel) {
		List<TemporalMention> predictedMentions = new LinkedList<TemporalMention>();
		for (int i = 0 ; i < tokens.size(); i++) {	
			// Work backwards to get the longest possible mention
			for (int span = Math.min(MAX_MENTION_LENGTH, tokens.size() - i)  ; span > 0 ; span--) {
				Sentence mentionCandidate = new Sentence(tokens.subList(i, i + span));
				IParserOutput<LogicalExpression> parserOutput = jointParser.parse(mentionCandidate, dummyDataItemModel);
				final List<IParseResult<LogicalExpression>> bestParses = parserOutput.getBestParses();
				if (bestParses.size() > 0) {
					predictedMentions.add(new TemporalMention(i, i + span, mentionCandidate.toString()));				
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
