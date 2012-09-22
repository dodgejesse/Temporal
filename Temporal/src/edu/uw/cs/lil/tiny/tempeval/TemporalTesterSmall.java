package edu.uw.cs.lil.tiny.tempeval;

import java.util.List;

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
		// Not parsed or too many parses
		int parseProblem = 0;
		for (final ILabeledDataItem<Pair<Sentence, String>, String> item : testData) {
			counter++;
			int c = test(item,model,counter);
			if(c == 0){
				correct += 1;
			}else if (c == 1)
				incorrect += 1;
			else
				parseProblem += 1;
		}
		System.out.println("Total phrases: " + counter);
		System.out.println("Number correctly parsed and executed: " + correct + ", which is " + (double)correct*100/counter+ " percent");
		System.out.println("Number parsed, but without correct output (either parser or executer): " + incorrect + ", which his " + (double)incorrect*100/counter + " percent");
		System.out.println("Number not parsed: " + parseProblem);
	}

	private int test(ILabeledDataItem<Pair<Sentence, String>, String> dataItem,
			Model<Sentence, LogicalExpression> model, int counter) {
		
		Sentence s = dataItem.getSample().first();
		String goldISO = dataItem.getLabel();
		String ref_time = dataItem.getSample().second();
		final IParserOutput<LogicalExpression> modelParserOutput = parser.parse(s,
				model.createDataItemModel(s));
		
		
		final List<IParseResult<LogicalExpression>> bestModelParses = modelParserOutput
				.getBestParses();
		if (!ONLYPRINTINCORRECT)
			System.out.println("Phrase: " + s.toString());
		if (bestModelParses.size() == 1) {
			// Case we have a single parse
			final IParseResult<LogicalExpression> parse = bestModelParses.get(0);
			LogicalExpression label = parse.getY();
			label = wrapNextAroundLogic(label);
			TemporalISO tmp = TemporalVisitor.of(label, ref_time);
			boolean c = tmp.toString().equals(goldISO);
			if ((ONLYPRINTINCORRECT && !c) || !ONLYPRINTINCORRECT)
				printing(label, goldISO, tmp, s.toString(), c, ref_time);
			return c ? 0 : 1;
		} else if (bestModelParses.size() > 1) {
			if (!ONLYPRINTINCORRECT){
				System.out.println("Too many parses! Will implement something here when we have learning.");
				System.out.println();
			}
			return 2;
		} else {
			if (!ONLYPRINTINCORRECT) {
				System.out.println("No parses! Will implement something to throw out words and try again.");
				System.out.println();
			}
			return 2;
		}
	}
	
	private void printing(LogicalExpression label, String goldISO, TemporalISO output, String phrase, boolean c, String ref_time){
		System.out.println("Phrase:   " + phrase);
		System.out.println("Logic:    " + label);
		System.out.println("ref_time: " + ref_time);
		System.out.println("Gold:     " + goldISO);
		System.out.println("Guess:    " + output);
		System.out.println("Correct?  " + c);
		System.out.println();
	}
	
	// A hack to wrap the logical expression within a "next", in place of context. 
	private LogicalExpression wrapNextAroundLogic(LogicalExpression l){
		if (l.getType().getName().toString().equals("r") || l.toString().contains("this:<s,<r,s>>") || l.toString().contains("previous:<s,<r,s>>"))
			return l;
		LogicalExpression nextFunction = categoryServices.parseSemantics("(lambda $0:s (next:<s,<r,s>> $0 ref_time:r))");
		return categoryServices.doSemanticApplication(nextFunction, l);
	}

	public static TemporalTesterSmall build(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		return new TemporalTesterSmall(test, parser);
	}
}