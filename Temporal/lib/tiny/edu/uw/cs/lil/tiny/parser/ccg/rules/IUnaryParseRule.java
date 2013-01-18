package edu.uw.cs.lil.tiny.parser.ccg.rules;

import java.util.Collection;

import edu.uw.cs.lil.tiny.ccg.categories.Category;

public interface IUnaryParseRule<Y> {
	
	/*
	 * Takes a single category and applies the unary rule to it.
	 */
	Collection<ParseRuleResult<Y>> apply(Category<Y> category);
	
}
