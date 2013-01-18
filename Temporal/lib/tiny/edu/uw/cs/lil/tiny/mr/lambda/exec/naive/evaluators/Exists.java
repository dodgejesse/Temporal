package edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators;

import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILiteralEvaluator;

public class Exists implements ILiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 1 && args[0] instanceof ILambdaResult) {
			return !((ILambdaResult) args[0]).isEmpty();
		} else {
			return null;
		}
	}
	
}
