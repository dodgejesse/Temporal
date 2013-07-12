package edu.uw.cs.lil.tiny.tempeval;

import java.util.LinkedHashSet;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.*;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalObservationDataset;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalResult;
import edu.uw.cs.lil.tiny.tempeval.util.Debug;
import edu.uw.cs.lil.tiny.tempeval.util.Debug.Type;
import edu.uw.cs.lil.tiny.tempeval.util.GovernerVerbPOSExtractor;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalStatistics;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public class TemporalTester {
	private final boolean PRINT_CORRECT = false;
	private final boolean PRINT_INCORRECT = false;
	private final boolean PRINT_NOPARSES = false;
	private final String PHRASE = "a year earlier";
	private final TemporalObservationDataset test;
	private final TemporalJointParser jointParser;
	private TemporalStatistics stats;


	private TemporalTester(TemporalObservationDataset test,	TemporalJointParser jointParser) {
		this.test = test;
		this.jointParser = jointParser;
	}

	public void test(JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model, TemporalStatistics stats) {
		this.stats = stats;
		test(test, model);
	}

	private void test(IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>> testData,
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model) {
		for (final ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult> item : testData) {
			stats.incrementTotalObservations();
			test(item, model);
		}
	}

	private void test(ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult> dataItem,
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model) {
		LogicalExpression guessLabel = null;
		String guessType = "", guessVal = "", correctLogicalForms = "";
		LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries = null;
		IHashVector averageMaxFeatureVector = null;
		IHashVector theta = model.getTheta();

		// To extract all of the information from the mention.
		// The key to know how to unpack this is in TemporalSentence.java.
		Sentence phrase = dataItem.getSample().first();
		String sentence = dataItem.getSample().second()[0];
		String ref_time = dataItem.getSample().second()[1];
		String depParse = dataItem.getSample().second()[2];
		String goldType = dataItem.getLabel().type;
		String goldVal = dataItem.getLabel().val;
		Pair<String, String>  govVerbPOSWithMod = GovernerVerbPOSExtractor.getGovVerbTag(dataItem.getSample().second());
		String govVerbPOS = govVerbPOSWithMod.second();
		String mod = govVerbPOSWithMod.first();
		// TODO clean this up! I don't need the governor verb here.

		IJointDataItemModel<LogicalExpression, LogicalExpression> jointDataItemModel = 
				new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, dataItem);
		final IJointOutput<LogicalExpression, TemporalResult> parserOutput = jointParser.parse(dataItem, jointDataItemModel);
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
			correctLogicalForms = anyCorrectLogic(goldType, goldVal, parserOutput.getAllJointParses(), theta);

			// TODO: Check every parse to make sure a correct one is included.
			//} else if (bestModelParses.size() > 1) {
			//	output = 3;
		} else { // zero parses
			stats.incrementNoParses();
			isCorrect = false;
		}
		Debug.println(Type.ATTRIBUTE, formatResult(guessLabel, goldType, goldVal, guessType, guessVal, phrase.toString(), ref_time, isCorrect, hasParse,
				depParse, govVerbPOS, sentence, mod, correctLogicalForms,lexicalEntries,averageMaxFeatureVector, theta));
	}

	private String anyCorrectLogic(String goldType, String goldVal, final List<? extends IJointParse<LogicalExpression, TemporalResult>> bestParses, IHashVector theta){
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

	private String formatResult(LogicalExpression label, String goldType, String goldVal,
			String guessType, String guessVal, String phrase, String ref_time,
			boolean isCorrect, boolean hasParse, String depParse, String govVerbPOS, String sentence, String mod, 
			String correctLogicalForms, LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries, IHashVector averageMaxFeatureVector, IHashVector theta) {
		String s =  "\n";
		if (hasParse) {
			if(PRINT_CORRECT && isCorrect || PRINT_INCORRECT && !isCorrect || phrase.contains(PHRASE)) {
				s += "Phrase:            " + phrase + "\n";
				s += "Lexical Entries:   " + lexicalEntries + "\n";
				s += "Sentence:          " + sentence + "\n";
				s += "Logic:             " + label + "\n";

				s += "Average max feats: " + theta.printValues(averageMaxFeatureVector) + "\n";
				s += "ref_time:          " + ref_time + "\n";
				s += "Gold type:         " + goldType + "\n";
				s += "gold val:          " + goldVal + "\n";
				s += "Guess type:        " + guessType + "\n";
				s += "Guess val:         " + guessVal + "\n";
				s += "Correct?           " + isCorrect + "\n";
				s += "Correct logics:    " + correctLogicalForms + "\n";
				s += "Governor verb POS: " + govVerbPOS + "\n";
				s += "Mod:               " + mod.equals("MD") + "\n";
			}
		}
		else if (PRINT_NOPARSES) {
			s += "Phrase:            " + phrase + "\n";
			s += "Sentence:          " + sentence + "\n";
			s += "ref_time:          " + ref_time + "\n";
			s += "Gold type:         " + goldType + "\n";
			s += "gold val:          " + goldVal + "\n";
			s += "No parses! Will implement something"
					+ " to throw out words and try again.\n";
		}
		return s;
	}

	public static TemporalTester build(TemporalObservationDataset test,	TemporalJointParser parser) {
		return new TemporalTester(test, parser);
	}
}