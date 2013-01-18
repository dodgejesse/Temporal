package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Count the number of constants (not unique constants) in a logical expression.
 * 
 * @author Yoav Artzi
 */
public class CountLogicalConstants implements ILogicalExpressionVisitor {
	int	count	= 0;
	
	private CountLogicalConstants() {
		// Usage only through static 'of' method
	}
	
	public static int of(LogicalExpression exp) {
		final CountLogicalConstants visitor = new CountLogicalConstants();
		visitor.visit(exp);
		return visitor.getCount();
	}
	
	public int getCount() {
		return count;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getArgument().accept(this);
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (!LogicLanguageServices.isCoordinationPredicate(logicalConstant)) {
			++count;
		}
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do
	}
}
