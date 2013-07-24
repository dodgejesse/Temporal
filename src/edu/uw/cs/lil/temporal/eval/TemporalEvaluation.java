package edu.uw.cs.lil.temporal.eval;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.Evaluation;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.LambdaWrapped;

public class TemporalEvaluation extends Evaluation {
	private final LogicalExpression	expression;
	protected TemporalEvaluation(LogicalExpression expression,	TemporalEvaluationServices services) {
		super(services);
		this.expression = expression;
	}
	
	public static Object of(LogicalExpression exp, TemporalEvaluationServices services) {
		final LogicalExpression wrapped = LambdaWrapped.of(exp);
		final TemporalEvaluation visitor = new TemporalEvaluation(wrapped, services);
		visitor.visit(wrapped);
		return visitor.result;
	}
}
