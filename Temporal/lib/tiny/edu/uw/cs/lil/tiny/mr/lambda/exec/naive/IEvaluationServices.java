package edu.uw.cs.lil.tiny.mr.lambda.exec.naive;

import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;

public interface IEvaluationServices {
	
	void cacheResult(LogicalExpression exp, Object result);
	
	void denotationChanged(Variable variable);
	
	Object evaluateConstant(LogicalConstant logicalConstant);
	
	Object evaluateLiteral(LogicalExpression predicate, Object[] args);
	
	List<?> getAllDenotations(Variable variable);
	
	Object getFromCache(LogicalExpression exp);
	
	boolean isCached(LogicalExpression exp);
	
	boolean isDenotable(Variable variable);
	
	/**
	 * Returns 'true' iff there's a mapping between the constant to the the
	 * domain of the appropriate type.
	 * 
	 * @param constant
	 * @return
	 */
	boolean isInterpretable(LogicalConstant constant);
	
}
