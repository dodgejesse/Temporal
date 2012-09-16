package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
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
		for (final ILabeledDataItem<Pair<Sentence, String>, String> item : testData) {
			test(item, model);
		}
	}

	// This feels wrong. It doesn't match what's going on in Tester.class, by Yoav.
	private void test(ILabeledDataItem<Pair<Sentence, String>, String> dataItem,
			Model<Sentence, LogicalExpression> model) {
		Sentence s = dataItem.getSample().first();
		final IParserOutput<LogicalExpression> modelParserOutput = parser.parse(s,
				model.createDataItemModel(s));
		System.out.println(dataItem.getSample().first());
		System.out.println(dataItem.getSample().second());
		System.out.println(dataItem.getLabel());
	}

	public static TemporalTesterSmall build(
			IDataCollection<? extends ILabeledDataItem<Pair<Sentence, String>, String>> test,
			AbstractCKYParser<LogicalExpression> parser) {
		return new TemporalTesterSmall(test, parser);
	}
}