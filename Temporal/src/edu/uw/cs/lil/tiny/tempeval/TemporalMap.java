package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;

import java.util.*;

import org.joda.time.LocalDate;

public class TemporalMap {
		final Map<String, Integer> months;
		final Map<String, Integer> weekdays;
		final String ref_time;
		
	public TemporalMap(String ref_time){
		months = new HashMap<String, Integer>();
		weekdays = new HashMap<String, Integer>();
		this.ref_time = ref_time;
		makeMonths();
		makeWeekdays();
	}
	
	private void makeWeekdays(){
		weekdays.put("monday",1);
		weekdays.put("tuesday",2);
		weekdays.put("wednesday",3);
		weekdays.put("thursday",4);
		weekdays.put("friday",5);
		weekdays.put("saturday",6);
		weekdays.put("sunday",7);
	}
	
	private void makeMonths(){
		months.put("january",1);
		months.put("february",2);
		months.put("march",3);
		months.put("april",4);
		months.put("may",5);
		months.put("june",6);
		months.put("july",7);
		months.put("august",8);
		months.put("september",9);
		months.put("october",10);
		months.put("november",11);
		months.put("december",12);
	}
	

	// Finds maps for predicates. Should just be about 10 if statements.
	public TemporalPredicate findComplexMap(LogicalConstant l){
		if (l.getName().equals("intersect:<s*,s>")){
			return new TemporalIntersect();
		} else if (l.getName().equals("next:<s,<r,s>>")){
			return new TemporalNext();
		} else if (l.getName().equals("this:<s,<r,s>>")){
			return new TemporalThis();
		} else {
			throw new IllegalArgumentException("found predicate (" + l + ") that hasn't been implemented yet!");
			
		}
	}
	
	// Finds map for all simple constants.
	public TemporalISO findNonComplexMap(LogicalConstant l){
		if (l.getType().getName().toString().equals("s")){
			return findSequenceMap(l);
		} else if (l.getType().getName().toString().equals("r")){
			return findRangeMap(l);
		}else if (l.getType().getName().toString().equals("d")){
			throw new IllegalArgumentException("haven't implemented durations in TemporalMap yet.");
		} else
			throw new IllegalArgumentException("Unknown logical constant " + l);
	}
	
	private TemporalISO findRangeMap(LogicalConstant l){
		if (l.getName().equals("ref_time:r")){
			return TemporalDate.readDocumentDate(ref_time);
		} else if (l.getName().equals("present_ref:r")){
			return new TemporalDate("present_ref", 0);
		} else if (l.getName().equals("today:r")){
			return TemporalDate.readDocumentDate(ref_time);
		} else if (l.getName().equals("tomorrow:r")){
			return shiftISOByDay(TemporalDate.readDocumentDate(ref_time), 1);
		} else if (l.getName().equals("yesterday:r")){
			return shiftISOByDay(TemporalDate.readDocumentDate(ref_time), -1);
		}else if (isNumber(l.getName().substring(0,l.getName().length()-2))){
			int year = Integer.parseInt(l.getName().substring(0,l.getName().length()-2));
			return new TemporalDate("year", year);
		} else 
			throw new IllegalArgumentException("constants of type range other than years and document times are not implemented.");
	}
	
	// Shifts a given ISO by n days. If n is negative, shifts it backwards in time.
	private TemporalISO shiftISOByDay(TemporalISO t, int n){
		LocalDate tmp = TemporalJoda.convertISOToLocalDate(t);
		if (n >0)
			tmp = tmp.plusDays(n);
		else if (n < 0)
			tmp = tmp.minusDays(-n);
		return TemporalJoda.convertLocalDateToISO(tmp);
	}
	
	private boolean isNumber(String s){
		try {
			Integer.parseInt(s);
			return true;
		} catch(NumberFormatException nfe){
			return false;
		}
	}
	
	private TemporalISO findSequenceMap(LogicalConstant l){
		if (l.getName().endsWith("d:s")){
			return findDayOfMonthMap(l);
		} else if (months.containsKey(l.getName().substring(0, l.getName().length()-2))){
			return findMonthMap(l);
		} else if (weekdays.containsKey(l.getName().substring(0, l.getName().length()-2))){
			return findWeekdayMap(l);
		} else if (l.getName().toLowerCase().contains("quarter")){
			return findQuarterMap(l);
		} else 
			throw new IllegalArgumentException("Unimplemented map for logical constant " + l);
	}
	
	private TemporalISO findQuarterMap(LogicalConstant l){
		if (l.getName().equals("firstQuarter:s")){
			return new TemporalDate("quarter", 1);
		} else if (l.getName().equals("secondQuarter:s")){
			return new TemporalDate("quarter", 2);
		} else if (l.getName().equals("thirdQuarter:s")){
			return new TemporalDate("quarter", 3);
		} else if (l.getName().equals("fourthQuarter:s")){
			return new TemporalDate("quarter", 4);
		} else {
			throw new IllegalArgumentException("Trying to map a constant " + l + " which isn't an implemented quarter!");
		}
	}
	
	private TemporalISO findWeekdayMap(LogicalConstant l){
		int num = weekdays.get((l.getName().substring(0, l.getName().length()-2)));
		return new TemporalDate("weekday", num);
	}
	
	private TemporalISO findMonthMap(LogicalConstant l){
		int num = months.get((l.getName().substring(0, l.getName().length()-2)));
		return new TemporalDate("month", num);
	}
	
	private TemporalISO findDayOfMonthMap(LogicalConstant l){
		int num = Integer.parseInt(l.getName().substring(0, l.getName().length()-3));
		return new TemporalDate("day",num);
	}
}
