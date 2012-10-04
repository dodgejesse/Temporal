package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;

public class TemporalDate extends TemporalISO{

	public TemporalDate(Map<String, Set<Integer>> data) {
		super(data);
	}
	
	public TemporalDate(String s, int n){
		super(s,n);
	}
	
	public TemporalDate(String s){
		super(s);
	}
	
	public static TemporalDate readDocumentDate(String s){
		Map<String, Set<Integer>> data = new HashMap<String, Set<Integer>>();
		String[] dateInfo = s.split("-");
		if (dateInfo.length != 3){
			throw new IllegalArgumentException(s + " is not a valid date!");
		}
		addToData("year", Integer.parseInt(dateInfo[0]), data);
		addToData("month", Integer.parseInt(dateInfo[1]), data);
		addToData("day",Integer.parseInt(dateInfo[2]), data);
		return new TemporalDate(data);
	}
	
	private static void addToData(String s, int n, Map<String, Set<Integer>> data){
		data.put(s, new HashSet<Integer>());
		data.get(s).add(n);
	}
	
	public String toString(){
		if (super.isConvexSet())
			return convexSetFormat();
		else if (super.isSet("weekday") || super.isSet("week")){
			return weekFormat();
		} else if (super.isSet("quarter")){
			return quarterFormat();
		} else if (super.isSet("present_ref")){
			return "PRESENT_REF";
		} else {
			return dateFormat();
		}
	}
	
	private String convexSetFormat(){
		if (super.isSet("quarter"))
			return "XXXX-QX";
		else 
			throw new IllegalArgumentException("printing of ISOs that are convex sets are not implemented for sets other than quarters.");
	}
	
	private String quarterFormat(){
		String s = "";
		if (super.isSet("year"))
				s = s + super.getValueFromDate(this, "year");
		else 
			s = s + "XXXX";
		if (super.getValueFromDate(this, "quarter") == 0)
			return s + "-QX";
		else{
			int qNum = super.getValueFromDate(this, "quarter");
			String q = (qNum == -1) ? "X" : "" + qNum;
			return s + "-Q" + q;
		}
	}
	
	private String weekFormat(){
		String s = "";
		if (super.isSet("year"))
			s = s + getIntValue("year");
		else
			s = s + "XXXX";
		if (super.isSet("week"))
			s = s + "-W" + getIntValue("week");
		else 
			s = s + "-WXX";
		if (super.isSet("weekday"))
			s = s + "-" + getIntValue("weekday");
		return s;
	}
	
	private String dateFormat(){
		String s = "";
		if (super.isSet("year")){
			s = s + getIntValue("year");
		} else {
			s = s + "XXXX";
		}
		if (super.isSet("month")){
			int v = getIntValue("month");
			if (v<10){
				s = s + "-0" + v;
			} else
				s = s + "-" + v;
		}
		if (super.isSet("day")){
			int v = getIntValue("day");
			if (v<10)
				s = s + "-0" + v;
			else 
				s = s + "-" + v;
		}
		return s;
	}
	
	private int getIntValue(String key){
		Set<Integer> tmpSet = super.getVal(key);
		for (int i : tmpSet){
			return i;
		}
		throw new IllegalArgumentException("Key " + key + " is not set in TemporalDate, and yet is trying to be printed.");
	}

}
