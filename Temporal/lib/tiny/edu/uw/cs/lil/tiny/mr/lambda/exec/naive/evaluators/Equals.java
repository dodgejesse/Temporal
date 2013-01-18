package edu.uw.cs.lil.tiny.mr.lambda.exec.naive.evaluators;

import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILiteralEvaluator;

public class Equals implements ILiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] != null && args[1] != null) {
			return args[0].equals(args[1]);
		} else {
			return null;
		}
	}
	
}
