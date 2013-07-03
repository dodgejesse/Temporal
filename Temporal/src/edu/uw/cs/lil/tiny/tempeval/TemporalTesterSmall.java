package edu.uw.cs.lil.tiny.tempeval;

import java.io.PrintStream;
import java.util.*;

import edu.uw.cs.lil.learn.simple.joint.JointSimplePerceptron;
import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.IJointParse;
import edu.uw.cs.lil.tiny.parser.joint.model.*;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public class TemporalTesterSmall {
	private final boolean ONLYPRINTINCORRECT = false;
	private final boolean ONLYPRINTTOOMANYPARSES = false;
	private final boolean ONLYPRINTNOPARSES = true;
	private final boolean ONLYPRINTONEPHRASE = false;
	private final String PHRASE = "hours";
	private final IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>> test;
	//private final AbstractCKYParser<LogicalExpression> baseParser;
	private final TemporalJointParser jointParser;
	private final LogicalExpressionCategoryServices categoryServices;
	private PrintStream out;
	OutputData outputData;


	private TemporalTesterSmall(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>> test,
					TemporalJointParser jointParser) {
		this.test = test;
		this.jointParser = jointParser;
		
		categoryServices = new LogicalExpressionCategoryServices();
	}

	public void test(JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model, 
			PrintStream out, OutputData outData) {
		this.out = out;
		outputData = outData;
		test(test, model);
	}

	private void test(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>> testData,
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model) {
		// The number of phrases in total.
		int counter = 0;
		// Correctly parsed and executed, both type and val.
		int correct = 0;
		// Parsed and executed, but either type or val are wrong.
		int incorrect = 0;
		// Number with the correct type.
		int correctType = 0;
		// Number with the correct value.
		int correctVal = 0;
		// Too many parses.
		int tooManyParses = 0;
		// Not parsed.
		int notParsed = 0;
		for (final ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult> item : testData) {
			counter++;
			int c = test(item, model, counter);
			// 0 == correct type and val
			// 1 == correct type, not val
			// 2 == incorrect type, correct val
			// -1 == incorrect type and val
			if (c == -1)
				incorrect++;
			else if (c == 0)
				correct++;
			else if (c == 1)
				correctType++;
			else if (c == 2)
				correctVal++;
			else if (c == 3)
				tooManyParses++;
			else// if (c == 4)
				notParsed++;
		}
		int[] tmpOutput = {counter, correct, correctVal, correctType, incorrect, tooManyParses, notParsed};
		outputData.setCounters(tmpOutput);
		out.println(outputData.toString());
	}

	private int test(ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult> dataItem,
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model, int counter) {
		int output;
		LogicalExpression guessLabel = null;
		String guessType = "", guessVal = "", correctLogicalForms = "";
		LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries = null;
		IHashVector averageMaxFeatureVector = null;
		IHashVector theta = model.getTheta();
		//out.println(thetaFromModel);
		

		
		// To extract all of the information from the mention.
		// The key to know how to unpack this is in TemporalSentence.java.
		Sentence phrase = dataItem.getSample().first();
		String docID = dataItem.getSample().second()[0];
		String sentence = dataItem.getSample().second()[1];
		String ref_time = dataItem.getSample().second()[2];
		String prevDocID = dataItem.getSample().second()[3];
		String goldType = dataItem.getLabel().type;
		String goldVal = dataItem.getLabel().val;
		String charNum = dataItem.getSample().second()[4];
		String depParse = dataItem.getSample().second()[5];
		Pair<String, String>  govVerbPOSWithMod = GovernerVerbPOSExtractor.getGovVerbTag(dataItem.getSample().second());
		String govVerbPOS = govVerbPOSWithMod.second();
		String mod = govVerbPOSWithMod.first();
		// TODO clean this up! I don't need the govenor verb here.
		
		//final IParserOutput<LogicalExpression> modelParserOutput = parser.parse(phrase, model.createDataItemModel(phrase));

		IJointDataItemModel<LogicalExpression, LogicalExpression> jointDataItemModel = 
				new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, dataItem);
		final IJointOutput<LogicalExpression, TemporalResult> parserOutput = jointParser.parse(dataItem, jointDataItemModel);
		final List<? extends IJointParse<LogicalExpression, TemporalResult>> bestParses = parserOutput.getBestJointParses();
		// there is more than one 
		if (bestParses.size() > 0) {
			
			IJointParse<LogicalExpression, TemporalResult> topParse = bestParses.get(0);
			
			
			averageMaxFeatureVector = topParse.getAverageMaxFeatureVector();
			
			
			guessType = topParse.getResult().second().type;
			guessVal = topParse.getResult().second().val;
			guessLabel = topParse.getResult().second().e;
			lexicalEntries = topParse.getResult().second().lexicalEntries;
			
			
			
//			System.out.println("\n\n\n\n");
//			System.out.println("IN TEMPORAL TESTER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			System.out.println("The number of parses: " + bestParses.size());
//			System.out.println("The number of parses from my parser: " + bestParses.size());
//			System.out.println("\n\n\n\n");
			
			output = oneParse(goldType, goldVal, guessType, guessVal);
			correctLogicalForms = anyCorrectLogic(goldType, goldVal, parserOutput.getAllJointParses(), theta);
			
			
		
		// TODO: Check every parse to make sure a correct one is included.
		//} else if (bestModelParses.size() > 1) {
		//	output = 3;
		} else { // zero parses
			output = 4;
		}
		printing(guessLabel, goldType, goldVal, guessType, guessVal, phrase.toString(), ref_time, output, charNum, 
				depParse, govVerbPOS, sentence, mod, correctLogicalForms,lexicalEntries,averageMaxFeatureVector, theta);
		return output;
	}
	
	private String anyCorrectLogic(String goldType, String goldVal, final List<? extends IJointParse<LogicalExpression, TemporalResult>> bestParses, IHashVector theta){
		String correctLogicalForms = "";
		for (IJointParse<LogicalExpression, TemporalResult> l : bestParses){
			String guessType = l.getResult().second().type;
			String guessVal = l.getResult().second().val;
			LogicalExpression guessLabel = l.getResult().second().e;
			if (oneParse(goldType, goldVal, guessType, guessVal) == 0)
				correctLogicalForms += "[" + guessLabel.toString() + "=>" + "(" + guessType + "," + guessVal + ")" + "]" + theta.printValues(l.getAverageMaxFeatureVector()) + "\n\t ";
		}
		return correctLogicalForms;
	}
	
	// To create a list without those parses that still have lambdas.
	private List<IParseResult<LogicalExpression>> pruneLogicWithLambdas(List<IParseResult<LogicalExpression>> bestModelParses){
		List<IParseResult<LogicalExpression>> newBestModelParses = new ArrayList<IParseResult<LogicalExpression>>();
		for (IParseResult<LogicalExpression> p : bestModelParses){
			if (!p.getY().getType().isComplex())
				newBestModelParses.add(p);
		}
		return newBestModelParses;
	}
	
	// Case we have a single parse
	// return a Pair<label, int>, where the int indicates if the label is correct.
	// 0 == correct type and val
	// 1 == correct type, not val
	// 2 == incorrect type, correct val
	// -1 == incorrect type and val
	private int oneParse(String goldType, String goldVal, String guessType, String guessVal){
		//LogicalExpression[] labels = getArrayOfLabels(parse, sameDocID);
		
		
		if (goldType.equals(guessType) && goldVal.equals(guessVal))
			return 0;
		else if (goldType.equals(guessType) && !goldVal.equals(guessVal))
			return 1;
		else if (!goldType.equals(guessType) && goldVal.equals(guessVal))
			return 2;
		else 
			return -1;
	}
	
//	// returns an int n.
//	private int findCorrectLabel(LogicalExpression[] labels, String ref_time, String type, String val){
//		int returnNum = -1;
//		// To test that the logic executes, and has the correct type and value.
//		for (int i = 0; i < labels.length; i++){
//			TemporalISO tmp = TemporalVisitor.of(labels[i], ref_time, previous);
//			if (tmp.getType().equals(type) && tmp.getVal().equals(val))
//				returnNum = i;
//		}
//		// if both type and value are correct for at least one of the labels, return.
//		if (returnNum > 0)
//			return returnNum;
//		// To test that the logic to an ISO that has the correct value, but not the correct type.
//		for (int i = 0; i < labels.length; i++){
//			TemporalISO tmp = TemporalVisitor.of(labels[i],  ref_time, previous);
//			if (!tmp.getType().equals(type) && tmp.getVal().equals(val))
//				returnNum = i + labels.length;
//		}
//		// if value is correct, but type is not, return.
//		if (returnNum > 0)
//			return returnNum;
//		// To test that the logic to an ISO that has the correct type, but not the correct value.		
//		for (int i = 0; i < labels.length; i++){
//			TemporalISO tmp = TemporalVisitor.of(labels[i],  ref_time, previous);
//			if (tmp.getType().equals(type) && !tmp.getVal().equals(val))
//				returnNum = i + labels.length * 2;
//		}
//		// if type is correct, but value is not, return. 
//		// Also returns -1 if nothing matches. 
//		return returnNum;
//	}

	// args: output is the output of the system
	private void printing(LogicalExpression label, String goldType, String goldVal,
			String guessType, String guessVal, String phrase, String ref_time,
			int correct, String charNum, String depParse, String govVerbPOS, String sentence, String mod, 
			String correctLogicalForms, LinkedHashSet<LexicalEntry<LogicalExpression>> lexicalEntries, IHashVector averageMaxFeatureVector, IHashVector theta) {
		
		//boolean c = (correct == 0);
		if (!ONLYPRINTONEPHRASE
				|| (ONLYPRINTONEPHRASE && phrase.contains(PHRASE))) {
			if (correct >= -1 && correct <= 2) {
				if ((ONLYPRINTINCORRECT && (correct == -1)) 
						|| !ONLYPRINTINCORRECT && !ONLYPRINTTOOMANYPARSES && !ONLYPRINTNOPARSES) {
					out.println();
					out.println("Phrase:            " + phrase);
					out.println("Lexical Entries:   " + lexicalEntries);
					out.println("Sentence:          " + sentence);
					out.println("Logic:             " + label);
					
					out.println("Average max feats: " + theta.printValues(averageMaxFeatureVector));
					out.println("ref_time:          " + ref_time);
					out.println("Gold type:         " + goldType);
					out.println("gold val:          " + goldVal);
					out.println("Guess type:        " + guessType);
					out.println("Guess val:         " + guessVal);
					out.println("Correct type?      " + (correct == 0 || correct == 1));
					out.println("Correct val?       " + (correct == 0 || correct == 2));
					out.println("Correct logics:    " + correctLogicalForms);
					//out.println("Character Number: " + charNum);
					out.println("Governor verb POS: " + govVerbPOS);
					out.println("Mod:               " + mod.equals("MD"));
//					out.println("Sentence: " + sentence);
//					out.println("Dependncy Parse: ");
//					out.println(depParse);
				}
			} else if ((correct == 4 && !ONLYPRINTINCORRECT && !ONLYPRINTTOOMANYPARSES) || correct == 4 && ONLYPRINTNOPARSES) {
				out.println();
				out.println("Phrase:            " + phrase);
				out.println("Sentence:          " + sentence);
				out.println("ref_time:          " + ref_time);
				out.println("Gold type:         " + goldType);
				out.println("gold val:          " + goldVal);
				out.println("No parses! Will implement something"
						+ " to throw out words and try again.");
//				out.println("Character Number: " + charNum);
//				out.println("Governor verb POS: " + govVerbPOS);
//				out.println("Mod: " + mod.equals("MD"));
//				out.println("Dependncy Parse: ");
//				out.println(depParse);
			}
		}
	}

	// A hack to wrap the logical expression within a "next", in place of
	// context.

	private LogicalExpression[] getArrayOfLabels(LogicalExpression l, boolean sameDocID) {
		int numOfFunctions = 4;
		LogicalExpression[] newLogicArray = new LogicalExpression[numOfFunctions + 1];
		LogicalExpression[] functionsS = new LogicalExpression[numOfFunctions];
		LogicalExpression[] functionsD = new LogicalExpression[numOfFunctions];
		// Making the Predicates to apply to the logical expressions for SEQUENCES
		functionsS[0] = categoryServices
				.parseSemantics("(lambda $0:s (previous:<s,<r,s>> $0 ref_time:r))");
		functionsS[1] = categoryServices
				.parseSemantics("(lambda $0:s (this:<s,<r,s>> $0 ref_time:r))");
		functionsS[2] = categoryServices
				.parseSemantics("(lambda $0:s (next:<s,<r,s>> $0 ref_time:r))");
		if (sameDocID)
			functionsS[3] = categoryServices
				.parseSemantics("(lambda $0:s (temporal_ref:<s,s> $0))");
		else
			functionsS[3] = l;			

		// Making the Predicates to apply to the logical expressions for DURATIONS
		functionsD[0] = categoryServices
				.parseSemantics("(lambda $0:d (previous:<d,<r,s>> $0 ref_time:r))");
		functionsD[1] = categoryServices
				.parseSemantics("(lambda $0:d (this:<d,<r,s>> $0 ref_time:r))");
		functionsD[2] = categoryServices
				.parseSemantics("(lambda $0:d (next:<d,<r,s>> $0 ref_time:r))");
		if (sameDocID)
			functionsD[3] = categoryServices
				.parseSemantics("(lambda $0:d (temproal_ref:<d,s> $0))");
		else
			functionsD[3] = l;			
		
		
		// Looping over the predicates, applying them each to the given logical expression
		for (int i = 0; i < functionsS.length; i++){
			newLogicArray[i+1] = categoryServices
					.doSemanticApplication(functionsS[i], l);

			if (newLogicArray[i+1] == null){
				newLogicArray[i+1] = categoryServices
					.doSemanticApplication(functionsD[i], l);
			}
			
			if (newLogicArray[i+1] == null) 
				newLogicArray[i+1] = l;
		}
		newLogicArray[0] = l;

		return newLogicArray;
	}

	public static TemporalTesterSmall build(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>> test,
					TemporalJointParser parser) {
		return new TemporalTesterSmall(test, parser);
	}
}