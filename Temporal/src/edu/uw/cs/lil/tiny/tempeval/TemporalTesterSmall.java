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
	final boolean ONLYPRINTINCORRECT = true;
	final boolean ONLYPRINTONEPHRASE = false;
	final String PHRASE = "fourth";
	final IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test;
	final AbstractCKYParser<LogicalExpression> parser;
	final LogicalExpressionCategoryServices categoryServices;

	private TemporalTesterSmall(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		this.test = test;
		this.parser = parser;

		categoryServices = new LogicalExpressionCategoryServices(false);
	}

	public void test(Model<Sentence, LogicalExpression> model) {
		test(test, model);
	}

	private void test(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> testData,
			Model<Sentence, LogicalExpression> model) {
		int counter = 0;
		// Correctly parsed and executed
		int correct = 0;
		// Parsed, but not executed
		int incorrect = 0;
		// Too many parses
		int tooManyParses = 0;
		// Not parsed
		int notParsed = 0;
		for (final ILabeledDataItem<Pair<Sentence, String>, String> item : testData) {
			counter++;
			int c = test(item, model, counter);
			if (c == 0) {
				correct += 1;
			} else if (c == 1)
				incorrect += 1;
			else if (c == 2)
				tooManyParses += 1;
			else
				notParsed += 1;
		}
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

	private int test(ILabeledDataItem<Pair<Sentence, String>, String> dataItem,
			Model<Sentence, LogicalExpression> model, int counter) {
		int output;
		boolean c = false;
		LogicalExpression label = null;
		TemporalISO tmp = null;
		Sentence s = dataItem.getSample().first();
		String goldISO = dataItem.getLabel();
		String ref_time = dataItem.getSample().second();
		final IParserOutput<LogicalExpression> modelParserOutput = parser
				.parse(s, model.createDataItemModel(s));

		final List<IParseResult<LogicalExpression>> bestModelParses = modelParserOutput
				.getBestParses();
		if (bestModelParses.size() == 1) {
			// Case we have a single parse
			final IParseResult<LogicalExpression> parse = bestModelParses
					.get(0);
			label = parse.getY();
			LogicalExpression[] labels = getArrayOfLabels(label);

			int n = findCorrectLabel(labels, ref_time, goldISO);
			if (n>=0){
				label = labels[n];
				c = true;
			} else 
				c = false;
			tmp = TemporalVisitor.of(label, ref_time);
			// if ((ONLYPRINTINCORRECT && !c) || !ONLYPRINTINCORRECT)
			// printing(label, goldISO, tmp, s.toString(), c, ref_time);
			output = c ? 0 : 1;
		} else if (bestModelParses.size() > 1) {

			output = 2;
		} else {
			output = 3;
		}
		printing(label, goldISO, tmp, s.toString(), c, ref_time, output, s);
		return output;
	}
	
	private int findCorrectLabel(LogicalExpression[] labels, String ref_time, String goldISO){
		for (int i = 0; i < labels.length; i++){
			TemporalISO tmp;
			
			// I'm keeping these try / catch statements here because I'll probably need them again in the future.
			//try {
				tmp = TemporalVisitor.of(labels[i], ref_time);
			//} catch (Exception e){
			//	System.out.println(e);
			//	continue;
			//}
			//try {
				if (tmp.toString().equals(goldISO))
					return i;
			//} catch (Exception e){
			//	System.out.println(e);
			//}
		}
		return -1;
	}

	private void printing(LogicalExpression label, String goldISO,
			TemporalISO output, String phrase, boolean c, String ref_time,
			int correct, Sentence s) {
		if (!ONLYPRINTONEPHRASE
				|| (ONLYPRINTONEPHRASE && s.toString().contains(PHRASE))) {
			if (correct == 0 || correct == 1) {
				if ((ONLYPRINTINCORRECT && !c) || !ONLYPRINTINCORRECT) {
					//System.out.println("Phrase:   " + phrase);
					System.out.println("Phrase:   " + s.toString());
					System.out.println("Logic:    " + label);
					System.out.println("ref_time: " + ref_time);
					System.out.println("Gold:     " + goldISO);
					System.out.println("Guess:    " + output);
					System.out.println("Correct?  " + c);
					System.out.println();
				}
			} else if (correct == 2 && !ONLYPRINTINCORRECT) {
				System.out.println("Phrase:   " + s.toString());
				System.out.println("Too many parses! Will implement"
						+ " something here when we have learning.");
				System.out.println();
			} else if (correct == 3 && !ONLYPRINTINCORRECT) {
				System.out.println("Phrase:   " + s.toString());
				System.out.println("No parses! Will implement something"
						+ " to throw out words and try again.");
				System.out.println();
			}
		}
	}

	// A hack to wrap the logical expression within a "next", in place of
	// context.

	private LogicalExpression[] getArrayOfLabels(LogicalExpression l) {
		int numOfFunctions = 2;
		LogicalExpression[] newLogicArray = new LogicalExpression[numOfFunctions + 1];
		LogicalExpression[] functions = new LogicalExpression[numOfFunctions];
		// Making the Predicates to apply to the logical expressions
		functions[0] = categoryServices
				.parseSemantics("(lambda $0:s (next:<s,<r,s>> $0 ref_time:r))");
		functions[1] = categoryServices
				.parseSemantics("(lambda $0:s (this:<s,<r,s>> $0 ref_time:r))");
		
		// Looping over the predicates, applying them each to the given logical expression
		for (int i = 0; i < functions.length; i++){
			newLogicArray[i] = categoryServices
					.doSemanticApplication(functions[i], l);
			if (newLogicArray[i] == null) 
				newLogicArray[i] = l;
		}
		newLogicArray[newLogicArray.length - 1] = l;

		return newLogicArray;
	}

	public static TemporalTesterSmall build(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		return new TemporalTesterSmall(test, parser);
	}
}