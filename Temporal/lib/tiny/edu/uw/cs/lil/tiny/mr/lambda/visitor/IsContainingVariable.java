package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Returns 'true' if the given logical expression contains the given variable.
 * Variable comparison is done using '=='.
 * 
 * @author Yoav Artzi
 */
public class IsContainingVariable implements ILogicalExpressionVisitor {
	
	private boolean			containing	= false;
	private final Variable	var;
	
	private IsContainingVariable(Variable var) {
		this.var = var;
	}
	
	public static boolean of(LogicalExpression exp, Variable var) {
		final IsContainingVariable visitor = new IsContainingVariable(var);
		visitor.visit(exp);
		return visitor.containing;
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getArgument().accept(this);
		if (!containing) {
			lambda.getBody().accept(this);
		}
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		if (!containing) {
			for (final LogicalExpression arg : literal.getArguments()) {
				arg.accept(this);
				if (containing) {
					return;
				}
			}
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		// Nothing to do
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		containing |= variable == var;
	}
}
