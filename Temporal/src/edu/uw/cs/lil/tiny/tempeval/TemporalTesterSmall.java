package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.utils.composites.Pair;

public class TemporalTesterSmall {
	final boolean ONLYPRINTINCORRECT = false;
	final boolean ONLYPRINTTOOMANYPARSES = false;
	final boolean ONLYPRINTNOPARSES = false;
	final boolean ONLYPRINTONEPHRASE = false;
	final String PHRASE = "";
	final IDataCollection<? extends ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>>> test;
	final AbstractCKYParser<LogicalExpression> parser;
	final LogicalExpressionCategoryServices categoryServices;

	private TemporalTesterSmall(
			IDataCollection<? extends ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		this.test = test;
		this.parser = parser;

		categoryServices = new LogicalExpressionCategoryServices(false);
	}

	public void test(Model<Sentence, LogicalExpression> model) {
		test(test, model);
	}

	private void test(
			IDataCollection<? extends ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>>> testData,
			Model<Sentence, LogicalExpression> model) {
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
		for (final ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>> item : testData) {
			counter++;
			int c = test(item, model, counter);
			if (c == 0)
				correct++;
			else if (c == 1)
				incorrect++;
			else if (c == 2)
				correctType++;
			else if (c == 3)
				correctVal++;
			else if (c == 4)
				tooManyParses++;
			else
				notParsed++;
		}
		System.out.println();
		System.out.println("Total phrases: " + counter);
		System.out
				.println("Number correctly parsed and executed: " + correct
						+ ", which is " + (double) correct * 100 / counter
						+ " percent");
		System.out
				.println("Number parsed, but without correct output (either parser or executer): "
						+ incorrect
						+ ", which his "
						+ (double) incorrect
						* 100
						/ counter + " percent");
		System.out.println("Number with too many parses: " + tooManyParses);
		System.out.println("Number with no parses: " + notParsed);
	}

	private int test(ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>> dataItem,
			Model<Sentence, LogicalExpression> model, int counter) {
		int output;
		boolean c = false;
		LogicalExpression label = null;
		TemporalISO temporalISO = null;
		
		// To extract all of the information from the mention.
		// The key to know how to unpack this is in TemporalSentence.java.
		String docID = dataItem.getSample().first()[0];
		String sentence = dataItem.getSample().first()[1];
		Sentence phrase = dataItem.getSample().second();
		String ref_time = dataItem.getSample().first()[2];
		String type = dataItem.getLabel().first();
		String val = dataItem.getLabel().second();
		
		final IParserOutput<LogicalExpression> modelParserOutput = parser
				.parse(phrase, model.createDataItemModel(phrase));

		final List<IParseResult<LogicalExpression>> bestModelParses = modelParserOutput
				.getBestParses();
		if (bestModelParses.size() == 1) {
			Pair<LogicalExpression, Integer> correctParse = oneParse(bestModelParses, label, temporalISO, ref_time, type, val);
			temporalISO = TemporalVisitor.of(label, ref_time);
		} else if (bestModelParses.size() > 1) {

			output = 2;
		} else { // zero parses
			output = 3;
		}
		printing(label, type, val, temporalISO, phrase.toString(), c, ref_time, output);
		return output;
	}
	
	// Case we have a single parse
	// return a Pair<label, int>, where the int indicates if the label is correct.
	private Pair<LogicalExpression, Integer> oneParse(List<IParseResult<LogicalExpression>> bestModelParses,
			LogicalExpression label, TemporalISO temporalISO, String ref_time, String type, String val){
		int output;
		final IParseResult<LogicalExpression> parse = bestModelParses
				.get(0);
		label = parse.getY();
		LogicalExpression[] labels = getArrayOfLabels(label);

		int n = findCorrectLabel(labels, ref_time, type, val);
		// if the logic (label) executes with the correct type and value.
		if (n > 0 && n < labels.length)
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
				throw new IllegalArgumentException("Something is wrong with the logic in oneParse, within TepmoralTesterSmall");
			return null;
		
	}
	
	// returns an int n.
	private int findCorrectLabel(LogicalExpression[] labels, String ref_time, String type, String val){
		int returnNum = -1;
		// To test that the logic executes, and has the correct type and value.
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp = TemporalVisitor.of(labels[i], ref_time);
			if (tmp.getType().equals(type) && tmp.getVal().equals(val))
				returnNum = i;
		}
		// if both type and value are correct for at least one of the labels, return.
		if (returnNum > 0)
			return returnNum;
		// To test that the logic to an ISO that has the correct value, but not the correct type.
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp = TemporalVisitor.of(labels[i],  ref_time);
			if (!tmp.getType().equals(type) && tmp.getVal().equals(val))
				returnNum = i + labels.length;
		}
		// if value is correct, but type is not, return.
		if (returnNum > 0)
			return returnNum;
		// To test that the logic to an ISO that has the correct type, but not the correct value.		
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp = TemporalVisitor.of(labels[i],  ref_time);
			if (tmp.getType().equals(type) && !tmp.getVal().equals(val))
				returnNum = i + labels.length * 2;
		}
		// if type is correct, but value is not, return. 
		// Also returns -1 if nothing matches. 
		return returnNum;
		
		
	}

	// args: output is the output of the system
	private void printing(LogicalExpression label, String type, String val,
			TemporalISO output, String phrase, boolean c, String ref_time,
			int correct) {
		//boolean c = n>=0;
		if (!ONLYPRINTONEPHRASE
				|| (ONLYPRINTONEPHRASE && phrase.contains(PHRASE))) {
			if (correct == 0 || correct == 1) {
				if ((ONLYPRINTINCORRECT && !c) || !ONLYPRINTINCORRECT && !ONLYPRINTTOOMANYPARSES && !ONLYPRINTNOPARSES) {
					System.out.println();
					System.out.println("Phrase:     " + phrase);
					System.out.println("Logic:      " + label);
					System.out.println("ref_time:   " + ref_time);
					System.out.println("Gold type:  " + type);
					System.out.println("gold val:   " + val);
					System.out.println("Guess:      " + output);
					System.out.println("Correct?    " + c);
				}
			} else if (correct == 2 && !ONLYPRINTINCORRECT &&!ONLYPRINTNOPARSES) {
				System.out.println();
				System.out.println("Phrase:   " + phrase);
				System.out.println("ref_time: " + ref_time);
				System.out.println("Gold type:  " + type);
				System.out.println("gold val:   " + val);
				System.out.println("Too many parses! Will implement"
						+ " something here when we have learning.");
			} else if ((correct == 3 && !ONLYPRINTINCORRECT && !ONLYPRINTTOOMANYPARSES) || correct == 3 && ONLYPRINTNOPARSES) {
				System.out.println();
				System.out.println("Phrase:   " + phrase);
				System.out.println("ref_time: " + ref_time);
				System.out.println("Gold type:  " + type);
				System.out.println("gold val:   " + val);
				System.out.println("No parses! Will implement something"
						+ " to throw out words and try again.");
			}
		}
	}

	// A hack to wrap the logical expression within a "next", in place of
	// context.

	private LogicalExpression[] getArrayOfLabels(LogicalExpression l) {
		int numOfFunctions = 3;
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

		// Making the Predicates to apply to the logical expressions for DURATIONS
		functionsD[0] = categoryServices
				.parseSemantics("(lambda $0:d (previous:<d,<r,s>> $0 ref_time:r))");
		functionsD[1] = categoryServices
				.parseSemantics("(lambda $0:d (this:<d,<r,s>> $0 ref_time:r))");
		functionsD[2] = categoryServices
				.parseSemantics("(lambda $0:d (next:<d,<r,s>> $0 ref_time:r))");		

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
			IDataCollection<? extends ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		return new TemporalTesterSmall(test, parser);
	}
}