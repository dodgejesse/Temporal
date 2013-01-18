package edu.uw.cs.lil.tiny.parser.ccg.rules;

import edu.uw.cs.lil.tiny.ccg.categories.Category;

/**
 * Result of applying a parse rule.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class ParseRuleResult<Y> {
	private final Category<Y>	resultCategory;
	private final String		ruleName;
	
	public ParseRuleResult(String ruleName, Category<Y> resultCategory) {
		super();
		this.ruleName = ruleName;
		this.resultCategory = resultCategory;
	}
	
	public Category<Y> getResultCategory() {
		return resultCategory;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
}
