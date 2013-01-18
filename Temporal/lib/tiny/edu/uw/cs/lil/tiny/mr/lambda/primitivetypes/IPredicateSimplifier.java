package edu.uw.cs.lil.tiny.mr.lambda.primitivetypes;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;

/**
 * Predicate specific simplifier.
 * 
 * @author Yoav Artzi
 */
public interface IPredicateSimplifier {
	public LogicalExpression simplify(LogicalExpression exp);
}
