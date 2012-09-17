package edu.uw.cs.lil.tiny.tempeval;

import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParseResult;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.utils.composites.Pair;

public class TemporalTesterSmall {
	final IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test;
	final AbstractCKYParser<LogicalExpression> parser;

	private TemporalTesterSmall(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		this.test = test;
		this.parser = parser;
	}

	public void test(Model<Sentence, LogicalExpression> model) {
		test(test, model);
	}

	private void test(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> testData,
			Model<Sentence, LogicalExpression> model) {
		int counter = 0;
		for (final ILabeledDataItem<Pair<Sentence, String>, String> item : testData) {
			counter++;
			test(item, model, counter);
		}
	}

	// This feels wrong. It doesn't match what's going on in Tester.class, by Yoav.
	private void test(ILabeledDataItem<Pair<Sentence, String>, String> dataItem,
			Model<Sentence, LogicalExpression> model, int counter) {
		Sentence s = dataItem.getSample().first();
		String goldISO = dataItem.getLabel();
		String ref_time = dataItem.getSample().second();
		final IParserOutput<LogicalExpression> modelParserOutput = parser.parse(s,
				model.createDataItemModel(s));
		
		final List<IParseResult<LogicalExpression>> bestModelParses = modelParserOutput
				.getBestParses();
		if (bestModelParses.size() == 1) {
			// Case we have a single parse
			final IParseResult<LogicalExpression> parse = bestModelParses.get(0);
			final LogicalExpression label = parse.getY();
			TemporalISO tmp = TemporalVisitor.of(label, ref_time);
			System.out.println("For phrase number " + counter);
			System.out.println("Phrase: " + s.toString());
			System.out.println("Gold:   " + goldISO);
			System.out.println("Guess:  " + tmp);
			System.out.println("Correct? " + tmp.toString().equals(goldISO));

		} else if (bestModelParses.size() > 1) {
			System.out.println("Too many parses! Will implement something here when we have learning.");
		} else {
			System.out.println("No parses! Will implement something to throw out words and try again.");
		}
		/*
		
		System.out.println("Testing the phrase:");
		System.out.println(dataItem.getSample().first());
		System.out.println(dataItem.getSample().second());
		System.out.println("The output from the parser:");
		System.out.println(dataItem.getLabel());
		
		 */
	}

	public static TemporalTesterSmall build(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		return new TemporalTesterSmall(test, parser);
	}
}