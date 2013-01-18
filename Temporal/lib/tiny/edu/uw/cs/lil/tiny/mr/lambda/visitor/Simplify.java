package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Simplify a given logical expression.
 * 
 * @author Yoav Artzi
 */
public class Simplify extends AbstrcatSimplify {
	
	private Simplify() {
	}
	
	public static LogicalExpression of(LogicalExpression exp) {
		final Simplify visitor = new Simplify();
		visitor.visit(exp);
		return visitor.tempReturn;
	}
	
	@Override
	public void visit(Variable variable) {
		tempReturn = variable;
	}
}
