package edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary;

import java.util.Collection;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;

/**
 * An abstract rule for logical application. Forward application rule:
 * <ul>
 * <li>X/Y Y => X</li>
 * </ul>
 */
public class ForwardApplication<Y> extends AbstractApplication<Y> {
	private static final String	RULE_NAME	= "fapply";
	
	public ForwardApplication(ICategoryServices<Y> categoryServices) {
		super(RULE_NAME, categoryServices);
	}
	
	@Override
	public Collection<ParseRuleResult<Y>> apply(Category<Y> left,
			Category<Y> right, boolean isCompleteSentence) {
		return doApplication(left, right, false);
	}
	
}
