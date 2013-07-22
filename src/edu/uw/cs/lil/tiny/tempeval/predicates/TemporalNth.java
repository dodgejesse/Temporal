package edu.uw.cs.lil.tiny.tempeval.predicates;

import edu.uw.cs.lil.tiny.tempeval.types.TemporalDate;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDuration;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalNumber;

public class TemporalNth extends TemporalPredicate{

	
	// Idea: Try to find general approach for finding 
	@Override
	public TemporalISO perform() {
		if (first == null || second == null)
			return null;
			
		if (first instanceof TemporalNumber){
			return new TemporalDate("quarter", ((TemporalNumber) first).getNum());
		}
		 
		int existingN = getSpecifiedDuration(first);
		// If the first argument already specified a non-zero number, e.g. first two months, use that instead
		if (existingN > 0)
			second = new TemporalNumber(existingN, true);
		
		
		if (!(first instanceof TemporalDuration)){
			throw new IllegalArgumentException("The first ISO stored in TemporalNth is not a TemporalDuration! (It really should  be.) ");
		}
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second ISO stored in TemporalNth is not a number!");
		//TemporalNthOfEach nthOfEach = new TemporalNthOfEach();
		//nthOfEach.storeISO(first);
		//nthOfEach.storeISO(second);
		return nthOfEach();
		//return nthOfEach.perform();
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
			return new TemporalDate("weekday", secondNum.getNum()); //hour of the weekday
		else if (first.isSet("hour"))
			return first;
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