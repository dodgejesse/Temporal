package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;

public class TemporalDate extends TemporalISO{

	public TemporalDate(Map<String, Set<Integer>> data) {
		super(data);
	}
	
	public TemporalDate(String s, int n){
		super(s,n);
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
		if (super.isSet("weekday") || super.isSet("week")){
			return weekFormat();
		} else {
			return dateFormat();
		}
	}
	
	private String weekFormat(){
		throw new IllegalArgumentException("Week format printing for TemporalDates is not implemented!");
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
