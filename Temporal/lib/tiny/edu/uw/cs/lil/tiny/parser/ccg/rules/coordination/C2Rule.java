package edu.uw.cs.lil.tiny.parser.ccg.rules.coordination;

import java.util.Collection;
import java.util.Collections;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.parser.ccg.rules.IBinaryParseRule;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;
import edu.uw.cs.utils.collections.ListUtils;

class C2Rule<Y> implements IBinaryParseRule<Y> {
	
	private static final String				RULE_NAME	= "c2";
	
	private final ICoordinationServices<Y>	services;
	
	public C2Rule(ICoordinationServices<Y> services) {
		this.services = services;
	}
	
	@Override
	public Collection<ParseRuleResult<Y>> apply(Category<Y> left,
			Category<Y> right, boolean isCompleteSentence) {
		if (left.getSyntax().equals(Syntax.C)
				&& SyntaxCoordinationServices.isCoordinationOfType(
						right.getSyntax(), null)) {
			final Y semantics = services.expandCoordination(right.getSem());
			if (semantics != null) {
				return ListUtils.createSingletonList(new ParseRuleResult<Y>(
						RULE_NAME, Category.create(
								new ComplexSyntax(right.getSyntax(),
										SyntaxCoordinationServices
												.getCoordinationType(right
														.getSyntax()),
										Slash.BACKWARD), semantics)));
			}
		}
		return Collections.emptyList();
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
		final C2Rule other = (C2Rule) obj;
		if (services == null) {
			if (other.services != null) {
				return false;
			}
		} else if (!services.equals(other.services)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((RULE_NAME == null) ? 0 : RULE_NAME.hashCode());
		result = prime * result
				+ ((services == null) ? 0 : services.hashCode());
		return result;
	}
	
	@Override
	public boolean isOverLoadable() {
		return true;
	}
	
}
