package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Get a set of all the vacuous variables: those that are in the argument for a
 * function but never in the body.
 * 
 * @author Luke Zettlemoyer
 */
public class GetAllVacuousVariables implements ILogicalExpressionVisitor {
	private final Set<Variable>	vacVariables	= new HashSet<Variable>();
	
	private GetAllVacuousVariables() {
		// Usage only through static 'of' method.
	}
	
	public static Set<Variable> of(LogicalExpression exp) {
		final GetAllVacuousVariables visitor = new GetAllVacuousVariables();
		visitor.visit(exp);
		return visitor.getVacuousVariables();
	}
	
	public Set<Variable> getVacuousVariables() {
		return vacVariables;
	}
	
	@Override
	public void visit(Lambda lambda) {
		vacVariables.add(lambda.getArgument());
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
		// Nothing to do
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		vacVariables.remove(variable);
	}
}
