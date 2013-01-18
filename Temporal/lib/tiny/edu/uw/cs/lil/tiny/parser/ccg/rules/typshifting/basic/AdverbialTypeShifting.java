package edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * AP => S\S
 * 
 * @author Yoav Artzi
 */
public class AdverbialTypeShifting extends
		AbstractTypeShiftingFunctionForThreading {
	private static final Syntax	S_BS_S_SYNTAX	= new ComplexSyntax(Syntax.S,
														Syntax.S,
														Slash.BACKWARD);
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AdverbialTypeShifting;
	}
	
	@Override
	public int hashCode() {
		return AdverbialTypeShifting.class.getName().hashCode();
	}
	
	@Override
	public Category<LogicalExpression> typeRaise(
			Category<LogicalExpression> category) {
		if (category.getSyntax().equals(Syntax.AP)) {
			final LogicalExpression raisedSemantics = typeRaiseSemantics(category
					.getSem());
			if (raisedSemantics != null) {
				return Category.create(S_BS_S_SYNTAX, raisedSemantics);
			}
		}
		return null;
	}
	
}
