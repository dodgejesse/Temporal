package edu.uw.cs.lil.temporal.eval.predicates;

import edu.uw.cs.lil.temporal.eval.entities.TemporalSequence;


public class TemporalIntersect extends TemporalPredicate {
	@Override
	public Object evaluate(Object[] args) {
		/*
		 * args[0]: s1 (TemporalSequence)
		 * args[1]: s2 (TemporalSequence)
		 * 
		 * e.g. 4th of July (<s1> of <s2>)
		 */
		TemporalSequence s1 = (TemporalSequence) args[1];
		TemporalSequence s2 = (TemporalSequence) args[2];

		TemporalSequence result;
		
		// Assume one path is a subset of another for now
		if (s1.getActiveDepth() > s2.getActiveDepth())
			result = s1.intersect(s2);
		else
			result = s2.intersect(s1);
		return result;
	}
}
