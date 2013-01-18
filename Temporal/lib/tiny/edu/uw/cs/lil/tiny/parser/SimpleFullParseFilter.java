package edu.uw.cs.lil.tiny.parser;

import java.util.Set;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.IsWellTyped;
import edu.uw.cs.utils.filter.IFilter;

public class SimpleFullParseFilter<Y> implements IFilter<Category<Y>> {
	
	private final Set<Syntax>	fullSentenceSyntaxes;
	
	public SimpleFullParseFilter(Set<Syntax> fullSentenceSyntaxes) {
		this.fullSentenceSyntaxes = fullSentenceSyntaxes;
	}
	
	@Override
	public boolean isValid(Category<Y> category) {
		return category.getSem() != null
				&& fullSentenceSyntaxes.contains(category.getSyntax())
				&& IsWellTyped.of((LogicalExpression) category.getSem());
	}
	
}
