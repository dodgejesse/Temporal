package edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary;

import java.util.Collection;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.parser.ccg.rules.ParseRuleResult;

/**
 * A rule for logical composition. Backward composition rule:
 * <ul>
 * <li>Y\Z X\Y => X\Z</li>
 * </ul>
 * 
 * @author Yoav Artzi
 */
public class BackwardComposition<Y> extends AbstractComposition<Y> {
	private static String	RULE_NAME	= "bcomp";
	
	public BackwardComposition(ICategoryServices<Y> categoryServices) {
		super(RULE_NAME, categoryServices);
	}
	
	public BackwardComposition(ICategoryServices<Y> categoryServices,
			boolean useEisnerNormalForm) {
		super(RULE_NAME, categoryServices, useEisnerNormalForm);
	}
	
	@Override
	public Collection<ParseRuleResult<Y>> apply(Category<Y> left,
			Category<Y> right, boolean isCompleteSentence) {
		return doComposition(right, left, true);
	}
	
}
