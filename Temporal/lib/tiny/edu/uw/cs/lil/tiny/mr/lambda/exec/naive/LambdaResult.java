package edu.uw.cs.lil.tiny.mr.lambda.exec.naive;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LambdaResult implements ILambdaResult {
	
	private final int			numKeys;
	final private Set<Tuple>	tuples	= new HashSet<Tuple>();
	
	public LambdaResult(int numKeys) {
		this.numKeys = numKeys;
	}
	
	public boolean addTuple(Tuple tuple) {
		if (tuple.numKeys() != numKeys) {
			throw new IllegalArgumentException("Invalid number of keys "
					+ tuple.numKeys() + ", expected " + numKeys);
		}
		return tuples.add(tuple);
	}
	
	public int getNumKeys() {
		return numKeys;
	}
	
	@Override
	public boolean isEmpty() {
		return tuples.isEmpty();
	}
	
	@Override
	public Iterator<Tuple> iterator() {
		return Collections.unmodifiableSet(tuples).iterator();
	}
	
	@Override
	public int size() {
		return tuples.size();
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final Iterator<Tuple> iterator = tuples.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString());
			if (iterator.hasNext()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
