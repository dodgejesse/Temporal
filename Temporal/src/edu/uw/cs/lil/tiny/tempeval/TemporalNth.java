package edu.uw.cs.lil.tiny.tempeval;

public class TemporalNth extends TemporalPredicate{

	
	// Idea: Try to find general approach for finding 
	@Override
	public TemporalISO perform() {
		if (first instanceof TemporalNumber){
			return new TemporalDate("quarter", 2);
		}
		if (!first.isConvexSet())
			throw new IllegalArgumentException("The first ISO stored in TemporalNth is not a convex set! (It really should  be.) ");
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second ISO stored in TemporalNth is not a number!");
		TemporalNthOfEach nthOfEach = new TemporalNthOfEach();
		nthOfEach.storeISO(first);
		nthOfEach.storeISO(second);
		nthOfEach.storeISO(getThird());
		return nthOfEach.perform();
	}
	
	private TemporalISO getThird(){
		if (first.isSet("quarter"))
			return new TemporalDate("year");
		else if (first.isSet("month"))
			return new TemporalDate("year");
		else if (first.isSet("day"))
			return new TemporalDate("month");
		else if (first.isSet("weekday"))
			return new TemporalDate("week");
		throw new IllegalArgumentException("Problem with the first stored ISO in TemporalNth!");
	}
}