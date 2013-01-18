package edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * ADJ => N/N
 * 
 * @author Yoav Artzi
 */
public class AdjectiveTypeShifting extends
		AbstractTypeShiftingFunctionForThreading {
	private static final Syntax	N_FS_N_SYNTAX	= new ComplexSyntax(Syntax.N,
														Syntax.N, Slash.FORWARD);
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AdjectiveTypeShifting;
	}
	
	@Override
	public int hashCode() {
		return AdjectiveTypeShifting.class.getName().hashCode();
	}
	
	@Override
	public Category<LogicalExpression> typeRaise(
			Category<LogicalExpression> category) {
		if (category.getSyntax().equals(Syntax.ADJ)) {
			final LogicalExpression raisedSemantics = typeRaiseSemantics(category
					.getSem());
			if (raisedSemantics != null) {
				return Category.create(N_FS_N_SYNTAX, raisedSemantics);
			}
		}
		return null;
	}
	
}
