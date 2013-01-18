package edu.uw.cs.lil.tiny.parser.ccg.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.categories.Category;

public class BinaryRulesSet<Y> implements IBinaryParseRule<Y> {
	
	private final List<IBinaryParseRule<Y>>	rules;
	
	public BinaryRulesSet(List<IBinaryParseRule<Y>> rules) {
		this.rules = rules;
	}
	
	@Override
	public Collection<ParseRuleResult<Y>> apply(Category<Y> left,
			Category<Y> right, boolean completeSentence) {
		final List<ParseRuleResult<Y>> results = new LinkedList<ParseRuleResult<Y>>();
		
		for (final IBinaryParseRule<Y> rule : rules) {
			results.addAll(rule.apply(left, right, completeSentence));
		}
		
		return results;
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
		final BinaryRulesSet other = (BinaryRulesSet) obj;
		if (rules == null) {
			if (other.rules != null) {
				return false;
			}
		} else if (!rules.equals(other.rules)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
		return result;
	}
	
	@Override
	public boolean isOverLoadable() {
		for (final IBinaryParseRule<Y> rule : rules) {
			if (!rule.isOverLoadable()) {
				return false;
			}
		}
		return true;
	}
	
}
