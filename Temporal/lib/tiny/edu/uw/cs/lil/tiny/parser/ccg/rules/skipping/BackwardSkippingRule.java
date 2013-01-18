package edu.uw.cs.lil.tiny.parser.ccg.rules.skipping;

import java.util.Collection;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;

/**
 * This is used to skip words tagged with an empty category:
 * <p>
 * <li>
 * <ul>
 * EMP X => X
 * </ul>
 * </li>
 * </p>
 * 
 * @author Yoav Artzi
 */
public class BackwardSkippingRule<Y> extends AbstractSkippingRule<Y> {
	private static final String	RULE_NAME	= "bskip";
	
	public BackwardSkippingRule(ICategoryServices<Y> categoryServices) {
		super(RULE_NAME, categoryServices);
	}
	
	@Override
	public Collection<ParseRuleResult<Y>> apply(Category<Y> left,
			Category<Y> right, boolean isCompleteSentence) {
		return attemptSkipping(left, right, true);
	}
	
}
