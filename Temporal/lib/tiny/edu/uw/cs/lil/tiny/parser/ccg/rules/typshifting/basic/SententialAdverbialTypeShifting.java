package edu.uw.cs.lil.tiny.parser.ccg.rules.typshifting.basic;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Slash;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Type shifting: S -> S/AP
 * 
 * @author Yoav Artzi
 */
public class SententialAdverbialTypeShifting extends
		AbstractTypeShiftingFunctionForThreading {
	private static final Syntax	S_FS_AP_SYNTAX	= new ComplexSyntax(Syntax.S,
														Syntax.AP,
														Slash.FORWARD);
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SententialAdverbialTypeShifting;
	}
	
	@Override
	public int hashCode() {
		return SententialAdverbialTypeShifting.class.getName().hashCode();
	}
	
	@Override
	public Category<LogicalExpression> typeRaise(
			Category<LogicalExpression> category) {
		if (category.getSyntax().equals(Syntax.S)) {
			final LogicalExpression raisedSemantics = typeRaiseSemantics(category
					.getSem());
			if (raisedSemantics != null) {
				return Category.create(S_FS_AP_SYNTAX, raisedSemantics);
			}
		}
		return null;
	}
	
}
