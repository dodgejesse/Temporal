package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Return all sub expressions of a given expression. The list of sub expressions
 * includes the expression itself.
 * 
 * @author Yoav Artzi
 */
public class AllSubExpressions implements ILogicalExpressionVisitor {
	private final List<LogicalExpression>	subExpressions	= new LinkedList<LogicalExpression>();
	
	private AllSubExpressions() {
		// Usage only through static 'of' method
	}
	
	public static List<LogicalExpression> of(LogicalExpression exp) {
		final AllSubExpressions visitor = new AllSubExpressions();
		visitor.visit(exp);
		return visitor.getSubExpressions();
	}
	
	public List<LogicalExpression> getSubExpressions() {
		return subExpressions;
	}
	
	@Override
	public void visit(Lambda lambda) {
		subExpressions.add(lambda);
		lambda.getBody().accept(this);
	}
	
	@Override
	public void visit(Literal literal) {
		subExpressions.add(literal);
		literal.getPredicate().accept(this);
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		subExpressions.add(logicalConstant);
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		// Nothing to do here
	}
	
}
