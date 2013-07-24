package edu.uw.cs.lil.temporal.predicates;

import edu.uw.cs.lil.temporal.types.TemporalDate;
import edu.uw.cs.lil.temporal.types.TemporalDuration;
import edu.uw.cs.lil.temporal.types.TemporalISO;
import edu.uw.cs.lil.temporal.types.TemporalNumber;

public class TemporalNth extends TemporalPredicate{

	
	// Idea: Try to find general approach for finding 
	@Override
	public TemporalISO perform() {
		if (first == null || second == null)
			return null;
		 
		int existingN = getSpecifiedDuration(first);
		// If the first argument already specified a non-zero number, e.g. first two months, use that instead
		if (existingN > 0)
			second = new TemporalNumber(existingN, true);
		
		if (!(first instanceof TemporalDuration)){
			throw new IllegalArgumentException("The first ISO stored in TemporalNth is not a TemporalDuration! (It really should  be.) ");
		}
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second ISO stored in TemporalNth is not a number!");
		
		return nthOfEach();
	}
	
	
	
	public TemporalISO nthOfEach() {
		if (!(first instanceof TemporalDuration))
			throw new IllegalArgumentException("The first ISO stored in TemporalNth is not a TemporalDuration! (It really should  be.) ");
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second ISO stored in TemporalNth is not a number!");
		TemporalNumber secondNum = (TemporalNumber)second;
		if (first.isSet("quarter")) //quarter of the year
			return new TemporalDate("quarter", secondNum.getNum());
		else if (first.isSet("month")) //month of the year
			return new TemporalDate("month", secondNum.getNum());
		else if (first.isSet("day")) //day of the month
			return new TemporalDate("day", secondNum.getNum());
		else if (first.isSet("weekday")) //weekday of the week
			return new TemporalDate("weekday", secondNum.getNum());
		else if (first.isSet("hour")) //hour of the day
			return new TemporalDate("hour", secondNum.getNum());
		else 
			return null;
		//throw new IllegalArgumentException("Constants passed to NthOfEach are not implemented yet! " + "Check TemporalNthOfEach for list of implemented constants.");
	}
	
	
	
	private int getSpecifiedDuration(TemporalISO iso){
		int maxN = 0;
		for (String s : iso.getKeys()){
			if (TemporalDate.getValueFromDate(iso, s) > maxN)
				maxN = TemporalDate.getValueFromDate(iso, s);
		}
		return maxN;
	}
}