package edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * PP => N\N
 * 
 * @author Yoav Artzi
 */
public class PrepositionTypeShifting extends
		AbstractTypeShiftingFunctionForThreading {
	private static final Syntax	N_BS_N_SYNTAX	= new ComplexSyntax(Syntax.N,
														Syntax.N,
														Slash.BACKWARD);
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PrepositionTypeShifting;
	}
	
	@Override
	public int hashCode() {
		return PrepositionTypeShifting.class.getName().hashCode();
	}
	
	@Override
	public Category<LogicalExpression> typeRaise(
			Category<LogicalExpression> category) {
		if (category.getSyntax().equals(Syntax.PP)) {
			final LogicalExpression raisedSemantics = typeRaiseSemantics(category
					.getSem());
			if (raisedSemantics != null) {
				return Category.create(N_BS_N_SYNTAX, raisedSemantics);
			}
		}
		return null;
	}
	
}
