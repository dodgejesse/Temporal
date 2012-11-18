package edu.uw.cs.lil.tiny.tempeval;


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
	
	// Finds the season of a given TemporalDate. 1 => spring, 2 => summer, etc.
	// Assumes t is fully specified, i.e. with a year.
	public int getSeason(TemporalISO t){
		int tMonth = TemporalDate.getValueFromDate(t, "month");
		int tDay = TemporalDate.getValueFromDate(t, "day");
		if (tMonth < 1)
			throw new IllegalArgumentException("Problem with finding the season of "+ t + "\n It has a month value of < 1!");
		if (tMonth < 3)
			return 4;
		else if (tMonth == 3){
			if (tDay <= 19)
				return 4;
			else
				return 1;
		} else if (tMonth > 3 && tMonth < 6)
			return 1;
		else if (tMonth == 6){
			if (tDay <= 20)
				return 1;
			else 
				return 2;
		} else if (tMonth > 6 && tMonth < 10)
			return 2;
		else if (tMonth == 10){
			if (tDay <= 21)
				return 2;
			else
				return 3;
		} else if (tMonth > 10 && tMonth < 12)
			return 3;
		else if (tMonth == 12){
			if (tDay <= 20)
				return 3;
			else
				return 4;
		}else
			throw new IllegalArgumentException("Problem with finding the season! We have a month > 12. Date: " + t);
	}
}