package edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators;

import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILiteralEvaluator;

public class Not implements ILiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 1 && args[0] instanceof Boolean) {
			return !Boolean.TRUE.equals(args[0]);
		} else {
			return null;
		}
	}
	
}
