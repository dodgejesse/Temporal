package edu.uw.cs.lil.tiny.parser.ccg.rules;

import java.util.Collection;

import edu.uw.cs.lil.tiny.ccg.categories.Category;

/**
 * Binary CCG parse rule.
 * 
 * @author Luke Zettlemoyer
 * @author Tom Kwiatkowski
 * @author Yoav Artzi
 */
public interface IBinaryParseRule<Y> {
	
	/**
	 * Takes two categories, left and right, as input. Assumes these categories
	 * are adjacent.
	 * 
	 * @param isCompleteSentence
	 *            Boolean indicating if the span covered by the two categories
	 *            provided is the complete sentence.
	 */
	Collection<ParseRuleResult<Y>> apply(Category<Y> left, Category<Y> right,
			boolean isCompleteSentence);
	
	boolean equals(Object obj);
	
	int hashCode();
	
	/**
	 * Indicates if this rule can be used to overload a type shifting function.
	 * 
	 * @return
	 */
	boolean isOverLoadable();
}
