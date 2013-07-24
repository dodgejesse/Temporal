package edu.uw.cs.lil.temporal.eval;

import java.util.List;
import edu.uw.cs.lil.temporal.eval.entities.TemporalEntity;
import edu.uw.cs.lil.temporal.eval.predicates.TemporalPredicate;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.AbstractEvaluationServices;

public class TemporalEvaluationServices extends AbstractEvaluationServices<Object> {	
	TemporalEvaluationConstants constants;
	public TemporalEvaluationServices(TemporalEvaluationConstants constants) {
		this.constants = constants;
	}
	
	@Override
	public Object evaluateConstant(LogicalConstant constant) {
		TemporalEntity entity = constants.getEntity(constant);
		if (entity != null)
			return entity;
		else
			return super.evaluateConstant(constant);
	}
	
	@Override
	public Object evaluateLiteral(LogicalExpression expression, Object[] args) {
		TemporalPredicate evaluator = constants.getPredicate(expression);
		if (evaluator != null)
			return evaluator.evaluate(args);
		else
			return null;
	}
	
	@Override
	public boolean isInterpretable(LogicalConstant constant) {
		return super.isInterpretable(constant)
				|| evaluateConstant(constant) != null
				|| constants.getPredicate(constant) != null;
	}

	@Override
	public List<?> getAllDenotations(Variable variable) {
		return null;
	}

	@Override
	public boolean isDenotable(Variable variable) {
		return false;
	}

	@Override
	protected Object currentState() {
		return null;
	}
}
