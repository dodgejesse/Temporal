package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Given a logical expression will return a multi-set of all logical constants.
 * 
 * @author Yoav Artzi
 */
public class GetConstantsMultiSet implements ILogicalExpressionVisitor {
	private final Multiset<LogicalConstant>	constants	= HashMultiset.create();
	
	private GetConstantsMultiSet() {
		// Usage only through static 'of' method
	}
	
	public static Multiset<LogicalConstant> of(LogicalExpression exp) {
		final GetConstantsMultiSet visitor = new GetConstantsMultiSet();
		visitor.visit(exp);
		return visitor.constants;
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
