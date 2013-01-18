package edu.uw.cs.lil.tiny.parser.ccg.cky.single;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.AbstractCellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IUnaryParseRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;

public class CKYUnaryParsingRule<Y> {
	private final boolean				applyOnlyToCompleteSentences;
	private final IUnaryParseRule<Y>	ccgParseRule;
	
	public CKYUnaryParsingRule(IUnaryParseRule<Y> ccgParseRule) {
		this(ccgParseRule, false);
	}
	
	public CKYUnaryParsingRule(IUnaryParseRule<Y> ccgParseRule,
			boolean applyOnlyToCompleteSentences) {
		this.ccgParseRule = ccgParseRule;
		this.applyOnlyToCompleteSentences = applyOnlyToCompleteSentences;
	}
	
	/**
	 * Takes two cell, left and right, as input. Assumes these cells are
	 * adjacent. Adds any new cells it can produce to the result list.
	 */
	public List<Cell<Y>> newCellsFrom(Cell<Y> cell,
			AbstractCellFactory<Y> cellFactory) {
		final List<Cell<Y>> result = new LinkedList<Cell<Y>>();
		if (!applyOnlyToCompleteSentences || cell.isCompleteSpan()) {
			final Collection<ParseRuleResult<Y>> results = ccgParseRule
					.apply(cell.getCategroy());
			for (final ParseRuleResult<Y> ruleResult : results) {
				result.add(cellFactory.create(ruleResult.getResultCategory(),
						cell, ruleResult.getRuleName()));
			}
		}
		return result;
	}
}
