package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Given a logical expression will return a set of all logical constants.
 * 
 * @author Yoav Artzi
 */
public class GetConstantsSet implements ILogicalExpressionVisitor {
	private final Set<LogicalConstant>	constants	= new HashSet<LogicalConstant>();
	
	private GetConstantsSet() {
		// Usage only through static 'of' method
	}
	
	public static Set<LogicalConstant> of(LogicalExpression exp) {
		final GetConstantsSet visitor = new GetConstantsSet();
		visitor.visit(exp);
		return visitor.getConstants();
	}
	
	public Set<LogicalConstant> getConstants() {
		return constants;
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
		constants.add(logicalConstant);
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
