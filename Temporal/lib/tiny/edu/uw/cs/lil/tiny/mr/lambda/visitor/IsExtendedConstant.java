package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Visitor to check if a logical expression is an extended constant. A logical
 * expression is an extended constant if:
 * <ul>
 * <li>1. It's a logical constant {@link LogicalConstant}.</li>
 * <li>2. It's a literal with the predicate being an extended constant and each
 * argument being an extended constant.</li>
 * <li>3. Otherwise it's not an extended constant.</li>
 * </ul>
 * 
 * @author Yoav Artzi
 */
public class IsExtendedConstant implements ILogicalExpressionVisitor {
	private boolean	isExtendedConstant	= true;
	
	private IsExtendedConstant() {
		// Usage only through static 'of' method.
	}
	
	public static boolean of(LogicalExpression exp) {
		final IsExtendedConstant visitor = new IsExtendedConstant();
		visitor.visit(exp);
		return visitor.result();
	}
	
	public boolean result() {
		return isExtendedConstant;
	}
	
	@Override
	public void visit(Lambda lambda) {
		isExtendedConstant = false;
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
		// Nothing to do since we start with the 'true' assumption
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		isExtendedConstant = false;
	}
}
