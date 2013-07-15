package edu.uw.cs.lil.tiny.tempeval;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservationDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalSentence;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.util.Debug;
import edu.uw.cs.lil.tiny.tempeval.util.Debug.Type;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;

public class TemporalDetectionTester {
	final private int MAX_MENTION_LENGTH = 5;
	private TemporalDataset testData;
	private TemporalJointParser jointParser;
	private TemporalObservationDataset correctObservations;
	private JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model;
	public TemporalDetectionTester(TemporalDataset testData, TemporalJointParser jointParser, JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model) {
		this.testData = testData;
		this.jointParser = jointParser;
		this.model = model;
		correctObservations = new TemporalObservationDataset();
	}
	
	public TemporalStatistics test(TemporalStatistics stats) {
		int sentenceCount = 0;
		for (TemporalSentence ts : testData) {
			sentenceCount++;
			if (sentenceCount % 100 == 0)
				Debug.printf(Type.PROGRESS, "Detecting mentions from %d/%d sentences...\n", sentenceCount, testData.size());
			stats.addGold(ts.getMentions().size());
			List<TemporalMention> predictedMentions = getPredictedMentions(ts);
			stats.addPredicted(predictedMentions.size());
			List<TemporalMention> correctMentions = getCorrectMentions(ts.getMentions(), predictedMentions);
			stats.addCorrect(correctMentions.size());
			if (correctMentions.size() < ts.getMentions().size()) {
				Debug.printf(Type.DETECTION, "False negative from '%s':\n", ts.prettyString());
				for(TemporalMention fn: getFalseNegatives(correctMentions, ts.getMentions()))
					Debug.printf(Type.DETECTION, "[%s]", fn);
				Debug.print(Type.DETECTION, "\n\n");
			}
			if (correctMentions.size() < predictedMentions.size()) {
				Debug.printf(Type.DETECTION, "False positive from '%s':\n", ts.prettyString());
				for(TemporalMention fp : getFalsePositives(correctMentions, predictedMentions))
					Debug.printf(Type.DETECTION, "[%s]", fp);
				Debug.print(Type.DETECTION, "\n\n");
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

	private List<TemporalMention> getPredictedMentions(TemporalSentence ts) {
		List<TemporalMention> predictedMentions = new LinkedList<TemporalMention>();
		for (int i = 0 ; i < ts.getTokens().size(); i++) {	
			// Work backwards to get the longest possible mention
			for (int span = Math.min(MAX_MENTION_LENGTH, ts.getTokens().size() - i)  ; span > 0 ; span--) {
				Sentence mentionCandidate = new Sentence(ts.getTokens().subList(i, i + span));
				JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression> dataItemModel = new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, ts.getPossibleObservation(i, i + span));		
				IParserOutput<LogicalExpression> parserOutput = jointParser.parse(mentionCandidate, dataItemModel);
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
}
