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
	
	public String getType(){
		return "DURATION";
	}
	
	public String getVal(){
		return this.toString();
	}
	
	public String toString(){
		String s = "P";
		if (super.isSet("year")){
			s = yearString(s);
		} else if (super.isSet("month")){
			s = addString("month", s, "M");
		} else if (super.isSet("week")){
			s = addString("week", s, "W");
		} else if (super.isSet("day")){
			s = addString("day", s, "D");
		} else if (super.isSet("quarter")){
			s = addString("quarter", s, "Q");
		} else if (super.isSet("hour")) {
			s = addString("hour", s + "T", "H");
		
		} else
			throw new IllegalArgumentException("Printing for durations is limited. You're trying to print something not implemented yet.");
			//s = "Haven't implemented toString for durations other than years";
		return s;
			
	}
	
	private String quarterFormat(){
		String s = "";
		//if ()
		return s;
	}
	
	private String convexSetFormat(){
		if (super.isSet("quarter"))
			return "XXXX-QX";
		else if (super.isSet("year"))
			return "XXXX";
		else if (super.isSet("week"))
			return "XXXX-WXX";
		else if (super.isSet("month"))
			return "XXXX-XX";
		else 
			throw new IllegalArgumentException("printing of ISOs that are convex sets are not implemented for sets other than quarters, years and weeks.");
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
	
	private String yearString(String s){
		int numYears = TemporalISO.getValueFromDate(this, "year");
		if (numYears % 1000 == 0 && numYears > 999){
			return s + (numYears / 1000) + "L";
		}
		else if (numYears % 100 == 0 && numYears > 99)
			return s + (numYears / 100) + "C";
		else
			return addString("year", s, "Y");
	}

	@Override
	// this method shouldn't be necessary for this type
	public boolean isFullySpecified() {
		return false;
	}
}

/*

public TemporalDuration(String field, int num) {
	super(field, num);
}
*/