package edu.uw.cs.lil.tiny.parser.ccg.cky.genlex;

import java.util.Collections;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYBinaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.AbstractCellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IBinaryParseRule;

public class MarkAwareCKYBinaryParsingRule extends
		CKYBinaryParsingRule<LogicalExpression> {
	
	private final int	maxMarkedLexicalEntries;
	
	public MarkAwareCKYBinaryParsingRule(
			IBinaryParseRule<LogicalExpression> ccgParseRule,
			int maxMarkedLexicalEntries) {
		super(ccgParseRule);
		this.maxMarkedLexicalEntries = maxMarkedLexicalEntries;
	}
	
	@Override
	protected List<Cell<LogicalExpression>> newCellsFrom(
			Cell<LogicalExpression> left, Cell<LogicalExpression> right,
			AbstractCellFactory<LogicalExpression> cellFactory,
			boolean isCompleteSentence) {
		// If both cells contains a GENLEX lexical entry, don't apply the rule,
		// just return
		if (left instanceof IMarkedEntriesCounter
				&& right instanceof IMarkedEntriesCounter
				&& ((IMarkedEntriesCounter) left).getNumMarkedLexicalEntries()
						+ ((IMarkedEntriesCounter) right)
								.getNumMarkedLexicalEntries() > maxMarkedLexicalEntries) {
			return Collections.emptyList();
		}
		
		return super.newCellsFrom(left, right, cellFactory, isCompleteSentence);
	}
}
