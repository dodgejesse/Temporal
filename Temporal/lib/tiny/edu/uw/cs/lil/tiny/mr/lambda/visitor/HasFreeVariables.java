package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Checks if a given logical expression has any free variables.
 * 
 * @author Yoav Artzi
 */
public class HasFreeVariables implements ILogicalExpressionVisitor {
	private final Set<Variable>	bindedVariable	= new HashSet<Variable>();
	private boolean				result			= false;
	
	public static boolean of(LogicalExpression exp) {
		final HasFreeVariables visitor = new HasFreeVariables();
		visitor.visit(exp);
		
		return visitor.result;
	}
	
	@Override
	public void visit(Lambda lambda) {
		bindedVariable.add(lambda.getArgument());
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		final Iterator<? extends LogicalExpression> iterator = literal
				.getArguments().iterator();
		while (!result && iterator.hasNext()) {
			iterator.next().accept(this);
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
		result |= !bindedVariable.contains(variable);
	}
	
}
