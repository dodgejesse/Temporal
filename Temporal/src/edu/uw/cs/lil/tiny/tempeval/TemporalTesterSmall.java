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
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.parser.joint.IJointOutput;
import edu.uw.cs.lil.tiny.parser.joint.model.*;
import edu.uw.cs.utils.composites.Pair;

public class TemporalTesterSmall {
	private final boolean ONLYPRINTINCORRECT = false;
	private final boolean ONLYPRINTTOOMANYPARSES = false;
	private final boolean ONLYPRINTNOPARSES = false;
	private final boolean ONLYPRINTONEPHRASE = false;
	private final String PHRASE = "a year earlier";
	private final IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>>> test;
	//private final AbstractCKYParser<LogicalExpression> baseParser;
	private final TemporalJointParser jointParser;
	private final LogicalExpressionCategoryServices categoryServices;
	private TemporalISO previous;
	private PrintStream out;
	OutputData outputData;


	private TemporalTesterSmall(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>>> test,
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
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>>> testData,
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
		for (final ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>> item : testData) {
			counter++;
			int c = test(item, model, counter);
			if (c == -1)
				incorrect++;
			else if (c == 0)
				correct++;
			else if (c == 1)
				correctVal++;
			else if (c == 2)
				correctType++;
			else if (c == 3)
				tooManyParses++;
			else// if (c == 4)
				notParsed++;
		}
		int[] tmpOutput = {counter, correct, correctVal, correctType, incorrect, tooManyParses, notParsed};
		outputData.setCounters(tmpOutput);
		out.println(outputData.toString());
	}

	private int test(ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>> dataItem,
			JointModel<Sentence, String[], LogicalExpression, LogicalExpression> model, int counter) {
		int output;
		LogicalExpression label = null;
		TemporalISO temporalISO = null;
				
		// To extract all of the information from the mention.
		// The key to know how to unpack this is in TemporalSentence.java.
		Sentence phrase = dataItem.getSample().first();
		String docID = dataItem.getSample().second()[0];
		String sentence = dataItem.getSample().second()[1];
		String ref_time = dataItem.getSample().second()[2];
		String prevDocID = dataItem.getSample().second()[3];
		String type = dataItem.getLabel().first();
		String val = dataItem.getLabel().second();
		String charNum = dataItem.getSample().second()[4];
		String depParse = dataItem.getSample().second()[5];
		boolean sameDocID = (previous == null || prevDocID.equals(docID));
		Pair<String, String>  govVerbPOSWithMod = GovernerVerbPOSExtractor.getGovVerbTag(dataItem.getSample().second());
		String govVerbPOS = govVerbPOSWithMod.second();
		// TODO clean this up! I don't need the govenor verb here.
		
		//final IParserOutput<LogicalExpression> modelParserOutput = parser.parse(phrase, model.createDataItemModel(phrase));
		
		//to make the dataItem for the jointParser:
		JointDataItemWrapper<Sentence, String[]> jointDataItem = new JointDataItemWrapper<Sentence, String[]>(
				phrase, new TemporalDataItem(dataItem.getSample()));
		
	
		IJointDataItemModel<LogicalExpression, LogicalExpression> jointDataItemModel = 
				new JointDataItemModel<Sentence, String[], LogicalExpression, LogicalExpression>(model, dataItem);
		
		IParserOutput<LogicalExpression> jointOutput = jointParser.parse(jointDataItem, jointDataItemModel);

		List<IParseResult<LogicalExpression>> parses = jointOutput.getBestParses();
				
		// there is more than one 
		if (parses.size() > 0) {
			IParseResult<LogicalExpression> parse = parses.get(0);
			label = parse.getY();
			output = oneParse(label, sameDocID, ref_time, type, val);
			temporalISO = TemporalVisitor.of(label, ref_time, previous);			
		
		// TODO: Check every parse to make sure a correct one is included.
		//} else if (bestModelParses.size() > 1) {
		//	output = 3;
		} else { // zero parses
			output = 4;
		}
		printing(label, type, val, temporalISO, phrase.toString(), ref_time, output, charNum, depParse, govVerbPOS, sentence);
		this.previous = temporalISO;
		return output;
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
	private int oneParse(LogicalExpression label, boolean sameDocID, String ref_time, String type, String val){
		//LogicalExpression[] labels = getArrayOfLabels(parse, sameDocID);
		
		TemporalISO tmp = TemporalVisitor.of(label,  ref_time,  previous);
		if (tmp.getType().equals(type) && tmp.getVal().equals(val))
			return 0;
		else if (tmp.getType().equals(type) && !tmp.getVal().equals(val))
			return 1;
		else if (!tmp.getType().equals(type) &&tmp.getVal().equals(val))
			return 2;
		else 
			return -1;
		
		/*
		int n = findCorrectLabel(label, ref_time, type, val);
		if (n >= 0 && n < labels.length)
			return Pair.of(labels[n],0);
		// if the label executes with the correct type, but not value.
		else if (n >= labels.length && n < labels.length * 2)
			return Pair.of(labels[n-labels.length],1);
		// if the label executes with the incorrect type, but correct value.
		else if (n >= labels.length*2 && n < labels.length *3)
			return Pair.of(labels[n-labels.length*2],2);
		// if n == -1
		else
			if (n != -1)
				throw new IllegalArgumentException("Something is wrong with the logic in oneParse, within TepmoralTesterSmall. n = " + n);
			return Pair.of(parse,  -1);*/
	}
	
	// returns an int n.
	private int findCorrectLabel(LogicalExpression[] labels, String ref_time, String type, String val){
		int returnNum = -1;
		// To test that the logic executes, and has the correct type and value.
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp = TemporalVisitor.of(labels[i], ref_time, previous);
			if (tmp.getType().equals(type) && tmp.getVal().equals(val))
				returnNum = i;
		}
		// if both type and value are correct for at least one of the labels, return.
		if (returnNum > 0)
			return returnNum;
		// To test that the logic to an ISO that has the correct value, but not the correct type.
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp = TemporalVisitor.of(labels[i],  ref_time, previous);
			if (!tmp.getType().equals(type) && tmp.getVal().equals(val))
				returnNum = i + labels.length;
		}
		// if value is correct, but type is not, return.
		if (returnNum > 0)
			return returnNum;
		// To test that the logic to an ISO that has the correct type, but not the correct value.		
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp = TemporalVisitor.of(labels[i],  ref_time, previous);
			if (tmp.getType().equals(type) && !tmp.getVal().equals(val))
				returnNum = i + labels.length * 2;
		}
		// if type is correct, but value is not, return. 
		// Also returns -1 if nothing matches. 
		return returnNum;
	}

	// args: output is the output of the system
	private void printing(LogicalExpression label, String type, String val,
			TemporalISO output, String phrase, String ref_time,
			int correct, String charNum, String depParse, String govVerbPOS, String sentence) {
		//boolean c = (correct == 0);
		if (!ONLYPRINTONEPHRASE
				|| (ONLYPRINTONEPHRASE && phrase.contains(PHRASE))) {
			if (correct >= -1 && correct <= 2) {
				if ((ONLYPRINTINCORRECT && (correct == -1)) || !ONLYPRINTINCORRECT && !ONLYPRINTTOOMANYPARSES && !ONLYPRINTNOPARSES) {
					out.println();
					out.println("Phrase:        " + phrase);
					out.println("Logic:         " + label);
					out.println("ref_time:      " + ref_time);
					out.println("Gold type:     " + type);
					out.println("gold val:      " + val);
					out.println("Guess type:    " + output.getType());
					out.println("Guess val:     " + output.getVal());
					out.println("Correct type?  " + (correct == 0 || correct == 2));
					out.println("Correct val?   " + (correct == 0 || correct == 1));
					//out.println("Character Number: " + charNum);
					//out.println("Govener Vebr POS: " + govVerbPOS);
					//out.println("Sentence: " + sentence);
					//out.println("Dependncy Parse: ");
					//out.println(depParse);
				}
			} else if (correct == 3 && !ONLYPRINTINCORRECT && !ONLYPRINTNOPARSES) {
				out.println();
				out.println("Phrase:        " + phrase);
				out.println("ref_time:      " + ref_time);
				out.println("Gold type:     " + type);
				out.println("gold val:      " + val);
				out.println("Too many parses! Will implement"
						+ " something here when we have learning.");
//				out.println("Character Number: " + charNum);
//				out.println("Govener Vebr POS: " + govVerbPOS);
//				out.println("Dependncy Parse: ");
//				out.println(depParse);
			} else if ((correct == 4 && !ONLYPRINTINCORRECT && !ONLYPRINTTOOMANYPARSES) || correct == 4 && ONLYPRINTNOPARSES) {
				out.println();
				out.println("Phrase:        " + phrase);
				out.println("ref_time:      " + ref_time);
				out.println("Gold type:     " + type);
				out.println("gold val:      " + val);
				out.println("No parses! Will implement something"
						+ " to throw out words and try again.");
//				out.println("Character Number: " + charNum);
//				out.println("Govener Vebr POS: " + govVerbPOS);
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
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>>> test,
					TemporalJointParser parser) {
		return new TemporalTesterSmall(test, parser);
	}
}