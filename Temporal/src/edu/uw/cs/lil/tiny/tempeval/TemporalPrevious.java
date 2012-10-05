package edu.uw.cs.lil.tiny.tempeval;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TemporalPrevious extends TemporalPredicate{

	@Override
	public TemporalISO perform() {
		testStoredDates();
		return findPrevious();
	}
	
	private TemporalISO findPrevious(){
		TemporalDate prevDate;
		if (first.isSet("year") || first.isSet("present_ref"))
			return first;
		else{
			if (first.isSet("quarter")){
				boolean thisYear = hasQuarterPassedThisYear();
				Map<String, Set<Integer>> tmpMap = first.getFullMapping();
				if (thisYear)
					tmpMap.put("year", second.getVal("year"));
				else 
					tmpMap.put("year", subtractOne(second.getVal("year")));
				prevDate = new TemporalDate(tmpMap);
			} else if (first.isSet("month")){
				// create two dates, one with this year, one with last year, return the one that is previous to ref_time
				Map<String, Set<Integer>> tmpMap = first.getFullMapping();
				if (!first.isSet("day"))
					tmpMap.put("day",second.getVal("day"));
				tmpMap.put("year", subtractOne(second.getVal("year")));
				TemporalDate firsTmpDate = new TemporalDate(tmpMap);
				tmpMap.put("year", second.getVal("year"));
				TemporalDate secondTmpDate = new TemporalDate(tmpMap);
				
				
			}
		}
		
		return prevDate;
	}
	
	// Only called when first is a quarter, and returns true if it is before the ref_time, false otherwise.
	// Note: Will always return false if quarter = 4.
	private boolean hasQuarterPassedThisYear(){
		if ((TemporalDate.getValueFromDate(first, "quarter") == 1 && TemporalDate.getValueFromDate(second, "month") > 3) ||
				(TemporalDate.getValueFromDate(first, "quarter") == 2 && TemporalDate.getValueFromDate(second, "month") > 6) ||
				(TemporalDate.getValueFromDate(first, "quarter") == 3 && TemporalDate.getValueFromDate(second, "month") > 9))
			return true;
		else 
			return false;
	}
	
	private Set<Integer> subtractOne(Set<Integer> oldIntSet){
		Set<Integer> tmpInt = new HashSet<Integer>();
		for (int i : this.second.getVal("year")) {
			tmpInt.add(Integer.valueOf(i - 1));
		}
		return tmpInt;
	}

	private void testStoredDates(){
		if (!(first instanceof TemporalDate) || !(second instanceof TemporalDate))
			throw new IllegalArgumentException("The two parameters to TemporalPrevious aren't TemporalDate objects, which they should be.");
	}
	
}
