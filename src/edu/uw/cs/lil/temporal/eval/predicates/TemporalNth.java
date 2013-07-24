package edu.uw.cs.lil.temporal.eval.predicates;

import edu.uw.cs.lil.temporal.eval.entities.TemporalSequence;


public class TemporalNth extends TemporalPredicate {
	@Override
	public Object evaluate(Object[] args) {
		/*
		 * args[0]: n (Integer)
		 * args[1]: unit (TemporalSequence)
		 * args[2]: scope (TemporalSequence)
		 * 
		 * e.g. Fifth day of the week (<n>th <unit> of the <scope>)
		 */
		int n = (int)args[0];
		TemporalSequence unit = (TemporalSequence) args[1];
		TemporalSequence scope = (TemporalSequence) args[2];
		unit.specifyDeepestActiveNode(n, scope);
		return unit;
	}
}
