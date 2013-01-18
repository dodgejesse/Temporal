package edu.uw.cs.lil.tiny.mr.lambda.exec.naive;

public interface ILambdaResult extends Iterable<Tuple> {
	boolean isEmpty();
	
	int size();
}
