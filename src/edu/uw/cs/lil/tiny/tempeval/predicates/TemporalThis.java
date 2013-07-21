package edu.uw.cs.lil.tiny.tempeval.predicates;

import java.util.*;

import org.joda.time.LocalDate;

import edu.uw.cs.lil.tiny.tempeval.types.TemporalDate;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDuration;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalUtil;

public class TemporalThis extends TemporalPredicate {
	public TemporalISO perform() {
		testStoredDates();
		return findThis();
	}

	private TemporalISO findThis() {
		if (first.isFullySpecified())
			return first;
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		if (!(first instanceof TemporalDuration)) {
			// TODO: This timeOfDay doesn't work for some examples, e.g. "friday evening".
			if (tmpMap.containsKey("timeOfDay") && !tmpMap.containsKey("weekday")){
				tmpMap.putAll(second.getFullMapping());
			} else if (tmpMap.containsKey("timeOfDay") && 
					(TemporalUtil.convertISOToLocalDate(second).dayOfWeek().get() == TemporalISO.getValueFromDate(first, "weekday"))){
				tmpMap = second.getFullMapping();
				tmpMap.put("timeOfDay", first.getVal("timeOfDay"));
			} else if (tmpMap.containsKey("weekday")) {
				LocalDate date = TemporalUtil.convertISOToLocalDate(this.second);
				while (date.getDayOfWeek() != TemporalISO.getValueFromDate(this.first, "weekday"))
					date = date.plusDays(1);
				return TemporalUtil.convertLocalDateToISO(date);
			} else if (!tmpMap.containsKey("year")) {
				Set<Integer> tmpSet = new HashSet<Integer>();
				tmpSet.add(TemporalISO.getValueFromDate(second, "year"));
				tmpMap.put("year", tmpSet);
			// case for things like (this:<s,<r,s>> 1901:r ref_time:r)
			} else if (tmpMap.containsKey("year") && 
					!(first.isSet("quarter") || first.isSet("month")
					|| first.isSet("week") || first.isSet("weekday")
					|| first.isSet("season")))
				return first;
			else if (!(first.isSet("quarter") || first.isSet("month")
					|| first.isSet("week") || first.isSet("weekday")
					|| first.isSet("season"))) {
				Set<Integer> tmpSet = new HashSet<Integer>();
				tmpSet.add(TemporalISO.getValueFromDate(second, "month"));
				tmpMap.put("month", tmpSet);
			}
		// Case where first is a duration. Will implement 'this year', 'this month' and 'this week'
		} else {
			if (first.isSet("year"))
				tmpMap.put("year", second.getVal("year"));
			else if (first.isSet("month")){
				tmpMap.put("year", second.getVal("year"));
				tmpMap.put("month", second.getVal("month"));
			} else if (first.isSet("week")){
				LocalDate tmpLocalDate = TemporalUtil.convertISOToLocalDate(second);
				int weekNum = tmpLocalDate.getWeekOfWeekyear();
				Set<Integer> weekNums = new HashSet<Integer>();
				weekNums.add(weekNum);
				tmpMap.put("week", weekNums);
				tmpMap.put("year", second.getVal("year"));
			}
		}

		return new TemporalDate(tmpMap);

	}

	private void testStoredDates() {
		if (!(first instanceof TemporalDate || first instanceof TemporalDuration)
				|| !(second instanceof TemporalDate)){			
			throw new IllegalArgumentException(
					"The two parameters to TemporalThis aren't TemporalDate objects, which they should be.");
		}
	}
}

// TODO: Find a case where this is useful. I don't think it is, but look anyway. 
/*
if (!(first.isSet("quarter") || first.isSet("week") || first.isSet("day") || first.isSet("weekday"))){
	Set<Integer> tmpSet = new HashSet<Integer>();
	tmpSet.add(TemporalISO.getValueFromDate(second, "day"));
	tmpMap.put("day", tmpSet);
}
*/