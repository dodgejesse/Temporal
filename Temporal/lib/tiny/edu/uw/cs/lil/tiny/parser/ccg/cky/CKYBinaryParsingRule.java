package edu.uw.cs.lil.tiny.parser.ccg.cky;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.AbstractCellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.cky.chart.Cell;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IBinaryParseRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;

public class CKYBinaryParsingRule<Y> {
	private final IBinaryParseRule<Y>	ccgParseRule;
	
	public CKYBinaryParsingRule(IBinaryParseRule<Y> ccgParseRule) {
		this.ccgParseRule = ccgParseRule;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		final CKYBinaryParsingRule other = (CKYBinaryParsingRule) obj;
		if (ccgParseRule == null) {
			if (other.ccgParseRule != null) {
				return false;
			}
		} else if (!ccgParseRule.equals(other.ccgParseRule)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ccgParseRule == null) ? 0 : ccgParseRule.hashCode());
		return result;
	}
	
	/**
	 * Takes two cell, left and right, as input. Assumes these cells are
	 * adjacent. Adds any new cells it can produce to the result list.
	 */
	protected List<Cell<Y>> newCellsFrom(Cell<Y> left, Cell<Y> right,
			AbstractCellFactory<Y> cellFactory, boolean isCompleteSentence) {
		final Collection<ParseRuleResult<Y>> results = ccgParseRule.apply(
				left.getCategroy(), right.getCategroy(), isCompleteSentence);
		final List<Cell<Y>> result = new LinkedList<Cell<Y>>();
		for (final ParseRuleResult<Y> ruleResult : results) {
			result.add(cellFactory.create(ruleResult.getResultCategory(), left,
					right, ruleResult.getRuleName()));
		}
		return result;
	}
}
