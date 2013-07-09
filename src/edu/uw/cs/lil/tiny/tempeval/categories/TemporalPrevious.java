package edu.uw.cs.lil.tiny.tempeval.categories;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

import edu.uw.cs.lil.tiny.tempeval.TemporalJoda;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDate;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDuration;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;

public class TemporalPrevious extends TemporalPredicate {

	@Override
	public TemporalISO perform() {
		// TODO: This is a hack!! If the temporal phrase is just "last", it
		// returns last year.
		if (second == null)
			return new TemporalDate("year", TemporalDate.getValueFromDate(
					first, "year"));
		testStoredDates();
		return findPrevious();
	}

	private TemporalDate quarterIsSet() {
		boolean thisYear = hasQuarterPassedThisYear();
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		if (thisYear)
			tmpMap.put("year", second.getVal("year"));
		else
			tmpMap.put("year", subtractOne(second.getVal("year")));
		return new TemporalDate(tmpMap);
	}

	private TemporalISO monthAndNotDay() {
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		// if ref_time is past first.month, return this year. else return last
		// year
		if (TemporalDate.getValueFromDate(second, "month") >= TemporalDate
				.getValueFromDate(first, "month"))
			tmpMap.put("year", second.getVal("year"));
		else
			tmpMap.put("year", subtractOne(second.getVal("year")));
		return new TemporalDate(tmpMap);
	}

	private TemporalISO weekdayAndTimeOfDay(){
		TemporalISO weekday = weekdayAndNotMonthOrDay();
		Map<String, Set<Integer>> tmpMap = weekday.getFullMapping();
		tmpMap.put("timeOfDay", first.getVal("timeOfDay"));
		return new TemporalDate(tmpMap);
	}

	private TemporalISO timeOfDay(){
		LocalDate date = TemporalJoda.convertISOToLocalDate(second);
		date = date.minusDays(1);
		TemporalISO isoDate = TemporalJoda.convertLocalDateToISO(date);
		Map<String, Set<Integer>> tmpMap = isoDate.getFullMapping();
		tmpMap.put("timeOfDay", first.getVal("timeOfDay"));
		return new TemporalDate(tmpMap);
	}

	private TemporalISO season(){
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		tmpMap.put("year", subtractOne(second.getVal("year")));
		return new TemporalDate(tmpMap);


		/*
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		int refSeason = super.getSeason(second);
		if (TemporalDate.getValueFromDate(first, "season") < refSeason)
			tmpMap.put("year",  second.getVal("year"));
		else
			tmpMap.put("year", subtractOne(second.getVal("year")));
		return new TemporalDate(tmpMap);
		 */
	}

	private TemporalISO monthAndDay() {
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		if (TemporalDate.getValueFromDate(second, "month") > TemporalDate
				.getValueFromDate(first, "month"))
			tmpMap.put("year", second.getVal("year"));
		else if (TemporalDate.getValueFromDate(second, "month") < TemporalDate
				.getValueFromDate(first, "month"))
			tmpMap.put("year", subtractOne(second.getVal("year")));
		// Case when the month is equal for ref_time and first
		else {
			if (TemporalDate.getValueFromDate(second, "day") > TemporalDate
					.getValueFromDate(first, "day"))
				tmpMap.put("year", second.getVal("year"));
			else
				tmpMap.put("year", subtractOne(second.getVal("year")));
		}
		return new TemporalDate(tmpMap);
	}

	private TemporalISO weekdayAndNotMonthOrDay() {
		LocalDate date = TemporalJoda.convertISOToLocalDate(second);
		if (date.getDayOfWeek() == TemporalISO.getValueFromDate(this.first,
				"weekday")) {
			date = date.minusDays(1);
		}
		while (date.getDayOfWeek() != TemporalISO.getValueFromDate(this.first,
				"weekday")) {
			date = date.minusDays(1);
		}
		return TemporalJoda.convertLocalDateToISO(date);
	}

	// TODO something here isn't working as expected. See phrase 
	// "the last twenty four hours"
	private TemporalISO dayAndNotMonth(){
		Map<String, Set<Integer>> tmpMap = second.getFullMapping();
		tmpMap.put("day", first.getVal("day"));
		if (TemporalDate.getValueFromDate(first, "day") >= TemporalDate.getValueFromDate(second, "day")){
			if (TemporalDate.getValueFromDate(second, "month") != 1)
				tmpMap.put("month", subtractOne(tmpMap.get("month")));
			else {
				tmpMap.put("year", subtractOne(tmpMap.get("year")));
				HashSet<Integer> tmpSet = new HashSet<Integer>();
				tmpSet.add(12);
				tmpMap.put("month", tmpSet);
			}
		}
		return new TemporalDate(tmpMap);
	}

	private TemporalISO convexYear() {
		if (TemporalDate.getValueFromDate(first, "year") >= 0)
			return first;

		int tmpRefYear = TemporalDate.getValueFromDate(second, "year");
		int amountToSubtract = TemporalDate.getValueFromDate(first, "year");
		return new TemporalDate("year", tmpRefYear + amountToSubtract);


		/*
		if (TemporalDate.getValueFromDate(first, "year")> 0)
			return first;
		int tmpYear = TemporalDate.getValueFromDate(second, "year");
		return new TemporalDate("year", tmpYear - 1);
		 */
	}

	private TemporalISO convexQuarter() {
		if (TemporalDate.getValueFromDate(first, "quarter")> 0)
			return first;		
		int quarterNum = (TemporalDate.getValueFromDate(second, "month") + 2) / 3;
		int year = TemporalDate.getValueFromDate(second, "year");
		if (quarterNum == 1) {
			year = year - 1;
			quarterNum = 4;
		} else
			quarterNum = quarterNum - 1;
		Map<String, Set<Integer>> tmpMap = new HashMap<String, Set<Integer>>();
		Set<Integer> tmpSet = new HashSet<Integer>();
		tmpSet.add(year);
		tmpMap.put("year", tmpSet);
		Set<Integer> tmpSet2 = new HashSet<Integer>();
		tmpSet2.add(quarterNum);
		tmpMap.put("quarter", tmpSet2);
		return new TemporalDate(tmpMap);
	}

	private TemporalISO convexMonth() {

		if (TemporalDate.getValueFromDate(first, "month")> 0)
			return first;
		int numMonthsToSubtract = -TemporalDate.getValueFromDate(first, "month");
		int year = TemporalDate.getValueFromDate(second, "year");
		int month = TemporalDate.getValueFromDate(second, "month");

		for (int i = 0; i < numMonthsToSubtract; i++){
			if (month == 1) {
				month = 12;
				year -= 1;
			} else {
				month -= 1;
			}
		}
		Set<Integer> monthSet = new HashSet<Integer>();
		monthSet.add(month);
		Set<Integer> yearSet = new HashSet<Integer>();
		yearSet.add(year);
		Map<String, Set<Integer>> tmpMap = new HashMap<String, Set<Integer>>();
		tmpMap.put("month", monthSet);
		tmpMap.put("year", yearSet);
		return new TemporalDate(tmpMap);

		/*
		if (TemporalDate.getValueFromDate(first, "month")> 0)
			return first;
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		Set<Integer> tmpSetMonth = second.getVal("month");
		Set<Integer> tmpSetYear = second.getVal("year");
		if (TemporalDate.getValueFromDate(second, "month") == 1) {
			tmpSetMonth.clear();
			tmpSetMonth.add(12);
			tmpSetYear.clear();
			tmpSetYear.add(TemporalDate.getValueFromDate(second, "year") - 1);
			// must subtract one from year
		} else {
			tmpSetMonth = subtractOne(tmpSetMonth);
		}
		tmpMap.put("month", tmpSetMonth);
		tmpMap.put("year", tmpSetYear);
		return new TemporalDate(tmpMap);
		 */
	}

	private TemporalISO convexWeek() {
		if (TemporalDate.getValueFromDate(first, "week")> 0)
			return first;
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		LocalDate tmpLocalDate = TemporalJoda.convertISOToLocalDate(second);
		tmpLocalDate = tmpLocalDate.minusWeeks(-TemporalDate.getValueFromDate(first, "week"));


		int weekNum = tmpLocalDate.getWeekOfWeekyear();

		Set<Integer> weekNums = new HashSet<Integer>();
		weekNums.add(weekNum);
		int yearNum = tmpLocalDate.getYear();
		Set<Integer> yearNums = new HashSet<Integer>();
		yearNums.add(yearNum);
		tmpMap.put("year", yearNums);
		tmpMap.put("week", weekNums);
		return new TemporalDate(tmpMap);
	}

	private TemporalISO convexDay(){
		if (TemporalISO.getValueFromDate(first, "day") > 0)
			return first;

		LocalDate refDate = TemporalJoda.convertISOToLocalDate(second);
		refDate = refDate.minusDays(-TemporalDate.getValueFromDate(first, "day"));
		return TemporalJoda.convertLocalDateToISO(refDate);
	}

	private TemporalISO findPrevious() {
		TemporalISO prevDate;
		if ((first.isSet("year") && !first.isConvexSet())
				|| first.isFullySpecified())
			return first;
		else {
			if (first instanceof TemporalDuration) {
				if (first.isSet("year")) {
					prevDate = convexYear();
				} else if (first.isSet("quarter")) {
					prevDate = convexQuarter();
				} else if (first.isSet("month")) {
					prevDate = convexMonth();
				} else if (first.isSet("week")) {
					prevDate = convexWeek();
				} else if (first.isSet("day")){
					prevDate = convexDay();
				} else if (first.isSet("hour")){
					// TODO: talk to people about this!!
					return first;
				} else{
					//System.out.println("Day: " + TemporalDate.getValueFromDate(first, "day"));

					throw new IllegalArgumentException(
							"Haven't implemented 'prevous' for convex set " + first);
				}
			} else if (first.isSet("quarter") && !first.isConvexSet()) {
				prevDate = quarterIsSet();
			} else if (first.isSet("season")){
				prevDate = season();
			} else if (first.isSet("month") && !first.isSet("day")
					&& !first.isConvexSet()) {
				prevDate = monthAndNotDay();
			} else if (first.isSet("weekday") && first.isSet("timeOfDay")){
				prevDate = weekdayAndTimeOfDay();
			} else if (first.isSet("timeOfDay")) {
				prevDate = timeOfDay();
			} else if (first.isSet("month") && first.isSet("day")) {
				prevDate = monthAndDay();
			} else if (first.isSet("weekday") && !first.isSet("month")
					&& !first.isSet("day")) {
				prevDate = weekdayAndNotMonthOrDay();
			} else if (first.isSet("day") && !first.isSet("month")){
				prevDate = dayAndNotMonth();
			} else
				throw new IllegalArgumentException(
						"Haven't implemented 'prevous' for " + first);
		}

		return prevDate;
	}

	// Only called when first is a quarter, and returns true if it is before the
	// ref_time, false otherwise.
	// Note: Will always return false if quarter = 4.
	private boolean hasQuarterPassedThisYear() {
		if ((TemporalDate.getValueFromDate(first, "quarter") == 1 && TemporalDate
				.getValueFromDate(second, "month") > 3)
				|| (TemporalDate.getValueFromDate(first, "quarter") == 2 && TemporalDate
				.getValueFromDate(second, "month") > 6)
				|| (TemporalDate.getValueFromDate(first, "quarter") == 3 && TemporalDate
				.getValueFromDate(second, "month") > 9))
			return true;
		else
			return false;
	}

	private Set<Integer> subtractOne(Set<Integer> oldIntSet) {
		Set<Integer> tmpInt = new HashSet<Integer>();
		for (int i : oldIntSet) {
			tmpInt.add(Integer.valueOf(i - 1));
		}
		return tmpInt;
	}

	private void testStoredDates() {

		if (!(first instanceof TemporalDate || first instanceof TemporalDuration)
				|| !(second instanceof TemporalDate))
			throw new IllegalArgumentException(
					"The two parameters to TemporalPrevious aren't TemporalDate objects, which they should be.");
	}
}
