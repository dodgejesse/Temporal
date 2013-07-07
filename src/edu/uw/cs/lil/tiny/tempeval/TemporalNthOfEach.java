package edu.uw.cs.lil.tiny.tempeval;

public class TemporalNthOfEach extends TemporalPredicate{

	// The only constants implemented here are:
	// quarter of year
	// month of year
	// day of month
	// weekday of week
	// 
	
	public TemporalISO perform() {
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
	

}
