package edu.uw.cs.lil.tiny.mr.lambda.exec.naive;

import java.util.Iterator;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.LambdaWrapped;

public class IsEvaluable implements ILogicalExpressionVisitor {
	
	private boolean						result	= true;
	private final IEvaluationServices	services;
	
	private IsEvaluable(IEvaluationServices services) {
		this.services = services;
	}
	
	public static boolean of(LogicalExpression exp, IEvaluationServices services) {
		final IsEvaluable visitor = new IsEvaluable(services);
		visitor.visit(LambdaWrapped.of(exp));
		return visitor.result;
	}
	
	@Override
	public void visit(Lambda lambda) {
		if (services.isDenotable(lambda.getArgument())) {
			lambda.getBody().accept(this);
		} else {
			result = false;
		}
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		final Iterator<LogicalExpression> iterator = literal.getArguments()
				.iterator();
		while (result && iterator.hasNext()) {
			iterator.next().accept(this);
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		result = services.isInterpretable(logicalConstant);
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
