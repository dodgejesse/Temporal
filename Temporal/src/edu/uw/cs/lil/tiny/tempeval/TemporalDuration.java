package edu.uw.cs.lil.tiny.tempeval;

public final class TemporalDuration extends TemporalISO {

	// For "year"
	public TemporalDuration(String field) {
		super(field);
	}
	
	// for the approximate durations, like "years"
	public TemporalDuration(String field, boolean approx){
		super(field, -1, false);
	}
	
	// For "five years"
	public TemporalDuration(String field, int n){
		super(field, n, true);
	}
	
	public String toString(){
		String s = "P";
		if (super.isSet("year")){
			s = addString("year", s, "Y");
		} else if (super.isSet("month")){
			s = addString("month", s, "M");
		} else if (super.isSet("week")){
			s = addString("week", s, "W");
		} else if (super.isSet("day")){
			s = addString("day", s, "D");
		} else
			s = "Haven't implemented toString for durations other than years";
		return s;
			
	}
	
	private String addString(String dur, String s, String abrev){
		int durNum = TemporalISO.getValueFromDate(this, dur);
		if (durNum == -1 && super.isConvexSet())
			s += "1" + abrev;
		else if (!super.isConvexSet())
			s += "X" + abrev;
		else
			s += durNum + abrev;
		return s;
	}
}

/*

public TemporalDuration(String field, int num) {
	super(field, num);
}
*/