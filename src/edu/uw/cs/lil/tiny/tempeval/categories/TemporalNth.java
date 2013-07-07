package edu.uw.cs.lil.tiny.tempeval.categories;

import edu.uw.cs.lil.tiny.tempeval.types.TemporalDate;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDuration;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalNumber;

public class TemporalNth extends TemporalPredicate{

	
	// Idea: Try to find general approach for finding 
	@Override
	public TemporalISO perform() {
		if (first instanceof TemporalNumber){
			return new TemporalDate("quarter", ((TemporalNumber) first).getNum());
			
		}
		
		
		// search through first's set fields, if any are >0, do nothing.
		if (checkForLargeDuration())
			return first;
		
		
		if (!(first instanceof TemporalDuration)){
			throw new IllegalArgumentException("The first ISO stored in TemporalNth is not a TemporalDuration! (It really should  be.) ");
		}
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second ISO stored in TemporalNth is not a number!");
		//TemporalNthOfEach nthOfEach = new TemporalNthOfEach();
		//nthOfEach.storeISO(first);
		//nthOfEach.storeISO(second);
		return nthOfEach(getThird());
		//return nthOfEach.perform();
	}
	
	
	
	public TemporalISO nthOfEach(TemporalISO third) {
		if (!(first instanceof TemporalDuration))
			throw new IllegalArgumentException("The first ISO stored in TemporalNth is not a TemporalDuration! (It really should  be.) ");
		if (!(second instanceof TemporalNumber))
			throw new IllegalArgumentException("The second ISO stored in TemporalNth is not a number!");
		TemporalNumber secondNum = (TemporalNumber)second;
		if (first.isSet("quarter") && third.isSet("year"))
			return new TemporalDate("quarter", secondNum.getNum());
		else if (first.isSet("month") && third.isSet("year"))
			return new TemporalDate("month", secondNum.getNum());
		else if (first.isSet("day") && third.isSet("month"))
			return new TemporalDate("day", secondNum.getNum());
		else if (first.isSet("weekday") && third.isSet("week"))
			return new TemporalDate("weekday", secondNum.getNum());
		
		
		throw new IllegalArgumentException("Constants passed to NthOfEach are not implemented yet! " + "Check TemporalNthOfEach for list of implemented constants.");
	}
	
	
	
	private boolean checkForLargeDuration(){
		for (String s : first.getKeys()){
			if (TemporalDate.getValueFromDate(first, s) > 0)
				return true;
		}
		return false;
	}
	// TODO: Fix problem here with hours!!
	private TemporalISO getThird(){
		if (first.isSet("quarter"))
			return new TemporalDate("year");
		else if (first.isSet("month"))
			return new TemporalDate("year");
		else if (first.isSet("day"))
			return new TemporalDate("month");
		else if (first.isSet("weekday"))
			return new TemporalDate("week");
		System.out.println("first: " + first);
		System.out.println(first.isConvexSet());
		throw new IllegalArgumentException("Problem with the first stored ISO in TemporalNth!");
	}
}