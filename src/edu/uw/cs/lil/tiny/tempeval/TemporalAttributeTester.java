package edu.uw.cs.lil.tiny.tempeval;

import java.util.LinkedHashSet;
import java.util.List;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.*;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMentionDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalResult;
import edu.uw.cs.lil.tiny.tempeval.util.Debug;
import edu.uw.cs.lil.tiny.tempeval.util.Debug.Type;
import edu.uw.cs.lil.tiny.tempeval.util.GovernerVerbPOSExtractor;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public class TemporalAttributeTester {
	private static final String DEBUG_PHRASE = "asdfsada year earlier";
	private final TemporalMentionDataset testData;
	private final TemporalJointParser jointParser;
	private TemporalStatistics stats;
	private JointModel<Sentence, TemporalMention, LogicalExpression, LogicalExpression> model;

	public TemporalAttributeTester(TemporalMentionDataset testData, TemporalJointParser jointParser, JointModel<Sentence, TemporalMention, LogicalExpression, LogicalExpression> model, TemporalStatistics stats) {
		this.testData = testData;
		this.jointParser = jointParser;
		this.model = model;
		this.stats = stats;
	}

	public void test() {
		Debug.printf(Type.PROGRESS, "Predicting attributes for %d observations\n", testData.size());
		for (TemporalMention mention : testData)
			testMention(mention);
	}

	private void testMention(TemporalMention mention) {
		stats.incrementTotalObservations();
		LogicalExpression guessLabel = null;
		String guessType = "", guessVal = "", correctLogicalForms = "", incorrectLogicalForms = "";
		LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries = null;
		IHashVector averageMaxFeatureVector = null;
		IHashVector theta = model.getTheta();

		// To extract all of the information from the mention.
		// The key to know how to unpack this is in TemporalSentence.java.
		Sentence phrase = mention.getPhrase();
		String sentence = mention.getSentence().prettyString();
		String referenceTime = mention.getSentence().getReferenceTime();
		String depParse = mention.getSentence().getDependencyParse();
		String docID = mention.getSentence().getDocID();
		String goldType = mention.getType();
		String goldVal = mention.getValue();
		Pair<String, String>  govVerbPOSWithMod = GovernerVerbPOSExtractor.getGovVerbTag(mention);
		String govVerbPOS = govVerbPOSWithMod.second();
		String mod = govVerbPOSWithMod.first();

		IJointDataItemModel<LogicalExpression, LogicalExpression> jointDataItemModel = new JointDataItemModel<Sentence, TemporalMention, LogicalExpression, LogicalExpression>(model, mention);
		final IJointOutput<LogicalExpression, TemporalResult> parserOutput = jointParser.parse(mention, jointDataItemModel);
		final List<? extends IJointParse<LogicalExpression, TemporalResult>> bestParses = parserOutput.getBestJointParses();
		boolean isCorrect;
		boolean hasParse = bestParses.size() > 0;
		// there is more than one 
		if (hasParse) {
			IJointParse<LogicalExpression, TemporalResult> topParse = bestParses.get(0);

			averageMaxFeatureVector = topParse.getAverageMaxFeatureVector();

			guessType = topParse.getResult().second().type;
			guessVal = topParse.getResult().second().val;
			guessLabel = topParse.getResult().second().e;
			lexicalEntries = topParse.getResult().second().lexicalEntries;

			isCorrect = evaluateTopParse(goldType, goldVal, guessType, guessVal);
			incorrectLogicalForms = getIncorrectLogicalForms(goldType, goldVal, parserOutput.getAllJointParses(), theta);
			correctLogicalForms = getCorrectLogicalForms(goldType, goldVal, parserOutput.getAllJointParses(), theta);
			if (!isCorrect && correctLogicalForms.length() > 0) {
				stats.incrementIncorrectParseSelection();
			}
			// TODO: Check every parse to make sure a correct one is included.
			//} else if (bestModelParses.size() > 1) {
			//	output = 3;
		} else { // zero parses
			stats.incrementNoParses();
			isCorrect = false;
		}

		Debug.print(Type.ATTRIBUTE, formatResult(docID, guessLabel, goldType, goldVal, guessType, guessVal, phrase.toString(), referenceTime, isCorrect, hasParse,
				depParse, govVerbPOS, sentence, mod, correctLogicalForms, incorrectLogicalForms, lexicalEntries,averageMaxFeatureVector, theta));
		if (!isCorrect) {
			Debug.print(Type.INCORRECT_ATTRIBUTE, formatResult(docID, guessLabel, goldType, goldVal, guessType, guessVal, phrase.toString(), referenceTime, isCorrect, hasParse,
					depParse, govVerbPOS, sentence, mod, correctLogicalForms, incorrectLogicalForms, lexicalEntries,averageMaxFeatureVector, theta));
			if (correctLogicalForms.length() > 0)
				Debug.print(Type.PARSE_SELECTION, formatResult(docID, guessLabel, goldType, goldVal, guessType, guessVal, phrase.toString(), referenceTime, isCorrect, hasParse,
						depParse, govVerbPOS, sentence, mod, correctLogicalForms, incorrectLogicalForms, lexicalEntries,averageMaxFeatureVector, theta));
		}
		if (phrase.toString().contains(DEBUG_PHRASE))
			Debug.print(Type.DEBUG_ATTRIBUTE, formatResult(docID, guessLabel, goldType, goldVal, guessType, guessVal, phrase.toString(), referenceTime, isCorrect, hasParse,
					depParse, govVerbPOS, sentence, mod, correctLogicalForms, incorrectLogicalForms, lexicalEntries,averageMaxFeatureVector, theta));
	}

	private String getCorrectLogicalForms(String goldType, String goldVal, final List<? extends IJointParse<LogicalExpression, TemporalResult>> bestParses, IHashVector theta){
		String correctLogicalForms = "";
		for (IJointParse<LogicalExpression, TemporalResult> l : bestParses){
			String guessType = l.getResult().second().type;
			String guessVal = l.getResult().second().val;
			LogicalExpression guessLabel = l.getResult().second().e;
			if (goldType.equals(guessType) && goldVal.equals(guessVal))
				correctLogicalForms += "\n\t[" + guessLabel.toString() + "=>" + "(" + guessType + "," + guessVal + ")" + "]" + theta.printValues(l.getAverageMaxFeatureVector());
		}
		return correctLogicalForms;
	}

	private String getIncorrectLogicalForms(String goldType, String goldVal, final List<? extends IJointParse<LogicalExpression, TemporalResult>> bestParses, IHashVector theta){
		String correctLogicalForms = "";
		for (IJointParse<LogicalExpression, TemporalResult> l : bestParses){
			String guessType = l.getResult().second().type;
			String guessVal = l.getResult().second().val;
			LogicalExpression guessLabel = l.getResult().second().e;
			if (!(goldType.equals(guessType) && goldVal.equals(guessVal)))
				correctLogicalForms += "\n\t[" + guessLabel.toString() + "=>" + "(" + guessType + "," + guessVal + ")" + "]" + theta.printValues(l.getAverageMaxFeatureVector());
		}
		return correctLogicalForms;
	}

	private boolean evaluateTopParse(String goldType, String goldVal, String guessType, String guessVal){
		if (goldType.equals(guessType) && goldVal.equals(guessVal)) {
			stats.incrementCorrectObservations();
			return true;
		}
		else if (goldType.equals(guessType) && !goldVal.equals(guessVal))
			stats.incrementCorrectTypes();
		else if (!goldType.equals(guessType) && goldVal.equals(guessVal))
			stats.incrementCorrectValues();
		else 
			stats.incrementIncorrectObservations();
		return false;
	}

	private String formatResult(String docID, LogicalExpression label, String goldType, String goldVal,
			String guessType, String guessVal, String phrase, String ref_time,
			boolean isCorrect, boolean hasParse, String depParse, String govVerbPOS, String sentence, String mod, 
			String correctLogicalForms, String incorrectLogicalForms, LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries, IHashVector averageMaxFeatureVector, IHashVector theta) {
		String s = "";
		s += "Phrase:            " + phrase + "\n";
		s += "Sentence:          " + sentence + "\n";
		s += "ref_time:          " + ref_time + "\n";
		s += "Doc ID:            " + docID + "\n";
		s += "Gold type:         " + goldType + "\n";
		s += "Gold val:          " + goldVal + "\n";
		if(hasParse) {
			s += "Guess type:        " + guessType + "\n";
			s += "Guess val:         " + guessVal + "\n";
			s += "Lexical Entries:   " + lexicalEntries + "\n";
			s += "Logic:             " + label + "\n";
			s += "Average max feats: " + theta.printValues(averageMaxFeatureVector) + "\n";
			s += "Correct?           " + isCorrect + "\n";
			s += "Correct logics:    " + correctLogicalForms + "\n";
			s += "Incorrect logics:  " + incorrectLogicalForms + "\n";
			s += "Governor verb POS: " + govVerbPOS + "\n";
			s += "Mod:               " + mod.equals("MD") + "\n";
		}
		else {
			s += "NO PARSES!";
		}
		//s += "Dep. parse:      \n" + depParse + "\n";
		s += "\n\n";
		return s;
	}
}