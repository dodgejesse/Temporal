package edu.uw.cs.lil.tiny.tempeval.categories;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.tempeval.TemporalJoda;

import java.util.*;

import org.joda.time.LocalDate;

public class TemporalMap {
		final Map<String, Integer> months;
		final Map<String, Integer> weekdays;
		final Map<String, Integer> seasons;
		final String ref_time;
		
	public TemporalMap(String ref_time){
		months = new HashMap<String, Integer>();
		weekdays = new HashMap<String, Integer>();
		seasons = new HashMap<String, Integer>();
		this.ref_time = ref_time;
		makeMonths();
		makeWeekdays();
		makeSeasons();
	}
	
	private void makeSeasons(){
		seasons.put("spring", 1);
		seasons.put("summer", 2);
		seasons.put("fall", 3);
		seasons.put("autumn", 3);
		seasons.put("winter", 4);
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
	public TemporalPredicate findComplexMap(LogicalConstant l, TemporalISO previous){
		if (l.getName().equals("intersect:<s*,s>")){
			return new TemporalIntersect();
		} else if (l.getName().equals("previous:<s,<r,s>>") || l.getName().equals("previous:<d,<r,s>>")){
			return new TemporalPrevious();
		} else if (l.getName().equals("next:<s,<r,s>>") || l.getName().equals("next:<d,<r,s>>")){
			return new TemporalNext();
		} else if (l.getName().equals("this:<s,<r,s>>") || l.getName().equals("this:<d,<r,s>>")){
			return new TemporalThis();
		} else if (l.getName().equals("nth:<d,<n,s>>")){
			return new TemporalNth();
		} else if (l.getName().equals("*:<d,<n,d>>")){
			return new TemporalMultiplication();
		} else if (l.getName().equals("temporal_ref:<s,s>") || l.getName().equals("temporal_ref:<d,s>")){
			return new TemporalReference(previous);
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
		} else if (l.getType().getName().toString().equals("d")){
			return findDurationMap(l);
		} else if (l.getType().getName().toString().equals("n")){
			return findNumberMap(l);
		} else 
			throw new IllegalArgumentException("Unknown logical constant " + l);
	}
	
	private TemporalISO findDurationMap(LogicalConstant l){
		if (l.getName().equals("year:d"))
			return new TemporalDuration("year");
		else if (l.getName().equals("years:d"))
			return new TemporalDuration("year", true);
		else if (l.getName().equals("quarter:d"))
			return new TemporalDuration("quarter");
		else if (l.getName().equals("quarters:d"))
			return new TemporalDuration("quarter",true);
		else if (l.getName().equals("month:d"))
			return new TemporalDuration("month");
		else if (l.getName().equals("months:d"))
			return new TemporalDuration("month", true);
		else if (l.getName().equals("week:d"))
			return new TemporalDuration("week");
		else if (l.getName().equals("weeks:d"))
			return new TemporalDuration("week", true);
		else if (l.getName().equals("day:d"))
			return new TemporalDuration("day");
		else if (l.getName().equals("days:d"))
			return new TemporalDuration("day", true);
		else if (l.getName().equals("hour:d"))
			return new TemporalDuration("hour");
		else
			throw new IllegalArgumentException("Unimplemented stuff in TemporalMap's findDurationMap.");
	}
	
	private TemporalISO findNumberMap(LogicalConstant l){
		if (l.getName().toString().endsWith("o:n")){
			int n = Integer.parseInt(l.getName().substring(0,l.getName().length()-3));
			return new TemporalNumber(n, true);
		} else if (l.getName().toString().endsWith(":n")){
			int n = Integer.parseInt(l.getName().substring(0,l.getName().length()-2));
			return new TemporalNumber(n, false);
		} else
			throw new IllegalArgumentException("Unknown number in findNumberMap, within TemporalMap!");
	}
	
	private TemporalISO findRangeMap(LogicalConstant l){
		if (l.getName().equals("ref_time:r")){
			return TemporalDate.readDocumentDate(ref_time);
		} else if (l.getName().equals("present_ref:r")){
			return new TemporalDate("present_ref", 0);
		} else if (l.getName().equals("past_ref:r")){
			return new TemporalDate("past_ref", 0);
		} else if (l.getName().equals("future_ref:r")) {
			return new TemporalDate("future_ref",0);
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
		if (n > 0)
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
		} else if (seasons.containsKey(l.getName().substring(0, l.getName().length()-2))){
			return findSeasonMap(l);
		} else if (l.getName().equals("quarter:s")){
			return new TemporalDate("quarter");
		} else if (l.getName().equals("year:s")){
			return new TemporalDate("year");
		} else if (l.getName().equals("month:s")){
			return new TemporalDate("month");
		} else if (l.getName().equals("week:s")){
			return new TemporalDate("week");
		} else if (l.getName().equals("day:s")) {
			return new TemporalDate("day");
		} else 
			throw new IllegalArgumentException("Unimplemented map for logical constant " + l);
	}
	
	private TemporalISO findSeasonMap(LogicalConstant l){
		int num = seasons.get((l.getName().substring(0, l.getName().length()-2)));
		return new TemporalDate("season", num);
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

/*
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
*/
