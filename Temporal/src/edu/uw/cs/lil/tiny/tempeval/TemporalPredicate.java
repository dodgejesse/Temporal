package edu.uw.cs.lil.tiny.tempeval;

import org.joda.time.LocalDate;

// The only predicates that take three arguments are:
// NthOfEach
//
// Only these should have the third predicate.
public abstract class TemporalPredicate {
	TemporalISO first;
	TemporalISO second;
	TemporalISO third;

	public abstract TemporalISO perform();

	public void storeISO(TemporalISO other) {
		if (this.first == null)
			this.first = other;
		else if (this.second == null)
			this.second = other;
		else if (this.third == null) {
			if (!(this instanceof TemporalNthOfEach))
				throw new IllegalArgumentException(
						"Trying to add a third argument to a predicate that shouldn't need one.");
			this.third = other;
		} else
			throw new IllegalArgumentException(
					"We have a problem in storeISO(TemporalISO d), in TemporalPredicate! "
							+ "There are already three ISOs stored, and we're trying to add another!");
	}
}