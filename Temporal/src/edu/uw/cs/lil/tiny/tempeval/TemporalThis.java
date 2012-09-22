package edu.uw.cs.lil.tiny.tempeval;

import java.util.*;

public class TemporalThis extends TemporalPredicate{
	public TemporalISO perform() {
		testStoredDates();
		return findThis();
	}
	
	private TemporalISO findThis(){
		if (first.isSet("present_ref"))
			throw new IllegalArgumentException("PRESENT_REF found when we should have a regular date! Problem in TemporalThis");
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		if (!tmpMap.containsKey("year")){
			Set<Integer> tmpSet = new HashSet<Integer>();
			tmpSet.add(TemporalISO.getValueFromDate(second, "year"));
			tmpMap.put("year", tmpSet);
		}
		if (!(first.isSet("quarter") || first.isSet("month") || first.isSet("week") || first.isSet("weekday"))){
			Set<Integer> tmpSet = new HashSet<Integer>();
			tmpSet.add(TemporalISO.getValueFromDate(second, "month"));
			tmpMap.put("month", tmpSet);
		}
		if (!(first.isSet("quarter") || first.isSet("week") || first.isSet("day") || first.isSet("weekday"))){
			Set<Integer> tmpSet = new HashSet<Integer>();
			tmpSet.add(TemporalISO.getValueFromDate(second, "day"));
			tmpMap.put("day", tmpSet);
		}
		return new TemporalDate(tmpMap);
		
	}
	
	private void testStoredDates() {
		if ((!this.first.getClass().toString().endsWith("TemporalDate"))
				|| (!this.second.getClass().toString().endsWith("TemporalDate")))
			throw new IllegalArgumentException(
					"The two parameters to TemporalNext aren't TemporalDate objects, which they should be.");
	}
}
