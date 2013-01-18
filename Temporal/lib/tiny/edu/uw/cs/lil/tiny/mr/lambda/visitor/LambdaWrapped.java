package edu.uw.cs.lil.tiny.mr.lambda.visitor;

import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

/**
 * Wraps an expression with needed Lambda operators. The order of arguments is
 * determined by the wrapped expression, so that the typing signature remains
 * the same.
 * 
 * @author Yoav Artzi
 */
public class LambdaWrapped implements ILogicalExpressionVisitor {
	private LogicalExpression	tempReturn	= null;
	
	private LambdaWrapped() {
		// Use through static 'of' method
	}
	
	public static LogicalExpression of(LogicalExpression exp) {
		final LambdaWrapped visitor = new LambdaWrapped();
		visitor.visit(exp);
		return visitor.tempReturn;
	}
	
	/**
	 * Assumes the type of exp is complex.
	 * 
	 * @param exp
	 * @return
	 */
	private static Lambda wrap(LogicalExpression exp) {
		final Variable newVariable = new Variable(LogicLanguageServices
				.getTypeRepository().generalizeType(exp.getType().getDomain()));
		final List<LogicalExpression> args = new ArrayList<LogicalExpression>(1);
		args.add(newVariable);
		LogicalExpression newLiteral = new Literal(exp, args,
				LogicLanguageServices.getTypeComparator(),
				LogicLanguageServices.getTypeRepository());
		if (newLiteral.getType().isComplex()) {
			newLiteral = wrap(newLiteral);
		} else {
			newLiteral = Simplify.of(newLiteral);
		}
		return new Lambda(newVariable, newLiteral,
				LogicLanguageServices.getTypeRepository());
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getBody().accept(this);
		if (tempReturn != lambda.getBody()) {
			tempReturn = new Lambda(lambda.getArgument(), tempReturn,
					LogicLanguageServices.getTypeRepository());
		} else {
			tempReturn = lambda;
		}
	}
	
	@Override
	public void visit(Literal literal) {
		if (literal.getType().isComplex()) {
			tempReturn = wrap(literal);
		} else {
			tempReturn = literal;
		}
		
		// Don't visit the arguments or predicate (this is a shallow process)
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (logicalConstant.getType().isComplex()) {
			tempReturn = wrap(logicalConstant);
		} else {
			tempReturn = logicalConstant;
		}
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		if (variable.getType().isComplex()) {
			tempReturn = wrap(variable);
		} else {
			tempReturn = variable;
		}
	}
}
