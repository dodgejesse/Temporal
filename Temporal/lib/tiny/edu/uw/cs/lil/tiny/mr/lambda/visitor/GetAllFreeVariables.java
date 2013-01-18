package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Get a set of all the free variables in a logical expression.
 * 
 * @author Yoav Artzi
 */
public class GetAllFreeVariables implements ILogicalExpressionVisitor {
	private final Set<Variable>	boundVariables	= new HashSet<Variable>();
	private final Set<Variable>	freeVariables	= new HashSet<Variable>();
	
	private GetAllFreeVariables() {
		// Usage only through static 'of' method
	}
	
	public static Set<Variable> of(LogicalExpression exp) {
		final GetAllFreeVariables visitor = new GetAllFreeVariables();
		visitor.visit(exp);
		return visitor.getFreeVariables();
	}
	
	public Set<Variable> getFreeVariables() {
		return freeVariables;
	}
	
	@Override
	public void visit(Lambda lambda) {
		boundVariables.add(lambda.getArgument());
		lambda.getBody().accept(this);
		boundVariables.remove(lambda.getArgument());
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
		// Nothing to do
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		if (!boundVariables.contains(variable)) {
			freeVariables.add(variable);
		}
	}
}
