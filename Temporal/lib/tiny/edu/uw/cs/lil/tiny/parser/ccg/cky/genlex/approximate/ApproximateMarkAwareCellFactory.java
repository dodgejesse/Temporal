package edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.approximate;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.CellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.utils.filter.IFilter;

public class ApproximateMarkAwareCellFactory extends
		CellFactory<LogicalExpression> {
	
	public ApproximateMarkAwareCellFactory(
			IDataItemModel<LogicalExpression> model, int sentenceSize,
			IFilter<Category<LogicalExpression>> completeParseFilter) {
		super(model, sentenceSize, completeParseFilter);
	}
	
	@Override
	protected Cell<LogicalExpression> doCreate(
			Category<LogicalExpression> category,
			Cell<LogicalExpression> child, boolean completeSpan,
			boolean fullParse, String ruleName) {
		return new ApproximateMarkedCell(
				category,
				ruleName,
				child,
				completeSpan,
				fullParse,
				child instanceof ApproximateMarkedCell ? ((ApproximateMarkedCell) child)
						.getNumMarkedLexicalEntries() : 0);
	}
	
	@Override
	protected Cell<LogicalExpression> doCreate(
			Category<LogicalExpression> category,
			Cell<LogicalExpression> leftChild,
			Cell<LogicalExpression> rightChild, boolean completeSpan,
			boolean fullParse, String ruleName) {
		final int leftGeneratedLexicalEntries = leftChild instanceof ApproximateMarkedCell ? ((ApproximateMarkedCell) leftChild)
				.getNumMarkedLexicalEntries() : 0;
		final int rightGeneratedLexicalEntries = rightChild instanceof ApproximateMarkedCell ? ((ApproximateMarkedCell) rightChild)
				.getNumMarkedLexicalEntries() : 0;
		
		return new ApproximateMarkedCell(category, ruleName, leftChild,
				rightChild, completeSpan, fullParse,
				leftGeneratedLexicalEntries + rightGeneratedLexicalEntries);
	}
	
	@Override
	protected Cell<LogicalExpression> doCreate(
			LexicalEntry<LogicalExpression> lexicalEntry, int begin, int end,
			boolean completeSpan, boolean fullParse) {
		return new ApproximateMarkedCell(lexicalEntry, begin, end,
				completeSpan, fullParse);
	}
	
}
