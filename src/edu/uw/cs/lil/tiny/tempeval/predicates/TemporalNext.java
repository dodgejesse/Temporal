package edu.uw.cs.lil.tiny.tempeval.predicates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.joda.time.LocalDate;

import edu.uw.cs.lil.tiny.tempeval.TemporalJoda;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDate;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDuration;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalISO;

public class TemporalNext extends TemporalPredicate {
	public TemporalISO perform() {
		testStoredDates();
		return findNext();
	}

	private TemporalISO findNext() {
		TemporalISO nextDate;
		//if (!first.isConvexSet()) {
		if (!(first instanceof TemporalDuration)) {

			if (this.first.getKeys().contains("year")
					|| this.first.isFullySpecified()) {
				return first;
			} else {
				if ((this.first.getKeys().contains("month"))
						&& (!this.first.getKeys().contains("day"))) {
					int ref_timeMonth = TemporalISO.getValueFromDate(
							this.second, "month");
					int dMonth = TemporalISO.getValueFromDate(this.first,
							"month");
					Map<String, Set<Integer>> tmpMap = this.first
							.getFullMapping();
					if (ref_timeMonth < dMonth) {
						tmpMap.put("year", this.second.getVal("year"));
					} else {
						Set<Integer> tmpInt = new HashSet<Integer>();
						for (int i : this.second.getVal("year")) {
							tmpInt.add(Integer.valueOf(i + 1));
						}
						tmpMap.put("year", tmpInt);
					}
					nextDate = new TemporalDate(tmpMap);
					// TODO: Change this to accurately give next if I say
					// "next third quarter" in Jaunary. Should be same year.
					// That's not possible with this.
				} else if (first.isSet("quarter")) {
					int ref_timeYear = TemporalISO.getValueFromDate(second,
							"year") + 1;
					Map<String, Set<Integer>> tmpMap = this.first
							.getFullMapping();
					Set<Integer> tmpSet = new HashSet<Integer>();
					tmpSet.add(ref_timeYear);
					tmpMap.put("year", tmpSet);
					nextDate = new TemporalDate(tmpMap);
				} else if (first.isSet("season")){
					nextDate = season();
				} else if (first.isSet("day") && !first.isSet("month") && !first.isSet("week") && !first.isSet("year")){
					nextDate = dayAndNotMonth();
				} else if (first.isSet("timeOfDay")&& first.isSet("weekday")){
					nextDate = timeOfDayAndWeekday();
				} else if (first.isSet("timeOfDay")) {
					nextDate = timeOfDay();
				} else {
					if ((this.first.getKeys().contains("month"))
							&& (this.first.getKeys().contains("day"))) {
						Map<String, Set<Integer>> tmpMap = this.first
								.getFullMapping();
						tmpMap.put("year", this.second.getVal("year"));
						TemporalDate firstTmpDate = new TemporalDate(tmpMap);
						Set<Integer> tmpInt = new HashSet<Integer>();
						for (int i : tmpMap.get("year")) {
							tmpInt.add(Integer.valueOf(i + 1));
						}
						tmpMap.put("year", tmpInt);
						TemporalDate secondTmpDate = new TemporalDate(tmpMap);
						if (areTemporallyOrdered(firstTmpDate, secondTmpDate))
							nextDate = firstTmpDate;
						else
							nextDate = secondTmpDate;
					} else {
						nextDate = weekdayNotMonth();
					}
				}
			}
		} else {
			if (first.isSet("year")){
				nextDate = convexYear();
			} else if (first.isSet("quarter")){
				nextDate = convexQuarter();
			} else if (first.isSet("month")){
				nextDate = convexMonth();
			} else if (first.isSet("week")){
				nextDate = convexWeek();
			} else if (first.isSet("day")){
				nextDate = convexDay();
			} else if (!first.isConvexSet()){
				nextDate = first;
			} else 
				throw new IllegalArgumentException("Haven't implemented 'next' for convex set " + first);
		}
		return nextDate;
	}
	
	private TemporalISO weekdayNotMonth() {
		if ((this.first.getKeys().contains("weekday"))
				&& (!this.first.getKeys().contains("month"))
				&& (!this.first.getKeys().contains("day"))) {
			LocalDate date = TemporalJoda
					.convertISOToLocalDate(this.second);
			if (date.getDayOfWeek() == TemporalISO
					.getValueFromDate(this.first, "weekday")) {
				date = date.plusDays(1);
			}
			while (date.getDayOfWeek() != TemporalISO
					.getValueFromDate(this.first, "weekday")) {
				date = date.plusDays(1);
			}
			return TemporalJoda.convertLocalDateToISO(date);
		} else {
			throw new IllegalArgumentException(
					"haven't implemented things other than"
							+ " dates with months or days. Problem in TemporalNext's findNext()");
		}
	}

	private TemporalISO timeOfDayAndWeekday() {
		Map<String, Set<Integer>> tmpMap = weekdayNotMonth().getFullMapping();
		tmpMap.put("timeOfDay", first.getVal("timeOfDay"));
		return new TemporalDate(tmpMap);
	}
	
	private TemporalISO timeOfDay() {
		LocalDate date = TemporalJoda.convertISOToLocalDate(second);
		date = date.plusDays(1);
		TemporalISO isoDate = TemporalJoda.convertLocalDateToISO(date);
		Map<String, Set<Integer>> tmpMap = isoDate.getFullMapping();
		tmpMap.put("timeOfDay", first.getVal("timeOfDay"));
		return new TemporalDate(tmpMap);
	}

	private TemporalISO season(){
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		tmpMap.put("year", addOne(second.getVal("year")));
		return new TemporalDate(tmpMap);
		/*
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		int refSeason = super.getSeason(second);
		if (TemporalDate.getValueFromDate(first, "season") > refSeason)
			tmpMap.put("year",  second.getVal("year"));
		else
			tmpMap.put("year", addOne(second.getVal("year")));
		return new TemporalDate(tmpMap);
		*/
	}
	
	private TemporalISO convexDay(){
		if (TemporalDate.getValueFromDate(first, "day") > 0)
			return first;
		LocalDate refDate = TemporalJoda.convertISOToLocalDate(second);
		refDate = refDate.plusDays(1);
		return TemporalJoda.convertLocalDateToISO(refDate);
	}
	
	private TemporalISO dayAndNotMonth(){
		Map<String, Set<Integer>> tmpMap = second.getFullMapping();
		tmpMap.put("day", first.getVal("day"));
		if (TemporalDate.getValueFromDate(first, "day") <= TemporalDate.getValueFromDate(second, "day")){
			if (TemporalDate.getValueFromDate(second, "month") != 12)
				tmpMap.put("month", addOne(tmpMap.get("month")));
			else {
				tmpMap.put("year", addOne(tmpMap.get("year")));
				HashSet<Integer> tmpSet = new HashSet<Integer>();
				tmpSet.add(1);
				tmpMap.put("month", tmpSet);
			}
		}
		return new TemporalDate(tmpMap);
	}
	
	private TemporalISO convexYear(){
		// TODO: test if the 'year' has a value other than 1 or -1 or whatever X is (as in PXY).
		int firstYear = TemporalDate.getValueFromDate(first, "year");
		if (firstYear > 0)
			return first;
		int tmpYear = TemporalDate.getValueFromDate(second, "year");
		return new TemporalDate("year", tmpYear + 1);
	}
	
	private TemporalISO convexQuarter(){
		int firstQuarter = TemporalDate.getValueFromDate(first, "quarter");
		if (firstQuarter > 0)
			return first;
		int quarterNum = (TemporalDate.getValueFromDate(second, "month") + 4)/4;
		int year = TemporalDate.getValueFromDate(second,"year");
		if (quarterNum == 4){
			year = year + 1;
			quarterNum = 1;
		} else 
			quarterNum = quarterNum + 1;
		Map<String, Set<Integer>> tmpMap = new HashMap<String, Set<Integer>>();
		Set<Integer> tmpSet = new HashSet<Integer>();
		tmpSet.add(year);
		tmpMap.put("year", tmpSet);
		Set<Integer> tmpSet2 = new HashSet<Integer>();
		tmpSet2.add(quarterNum);
		tmpMap.put("quarter", tmpSet2);
		return new TemporalDate(tmpMap);
	}
	
	private TemporalISO convexMonth(){
		int firstMonth = TemporalDate.getValueFromDate(first, "month");
		if (firstMonth > 0)
			return first;
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		Set<Integer> tmpSetMonth = second.getVal("month");
		Set<Integer> tmpSetYear = second.getVal("year");
		if (TemporalDate.getValueFromDate(second, "month") == 12){
			tmpSetMonth.clear();
			tmpSetMonth.add(1);
			tmpSetYear.clear();
			tmpSetYear.add(TemporalDate.getValueFromDate(second, "year") + 1);
			// must subtract one from year
		} else {
			tmpSetMonth = addOne(tmpSetMonth);
		}
		tmpMap.put("month", tmpSetMonth);
		tmpMap.put("year", tmpSetYear);
		return new TemporalDate(tmpMap);
	}
	
	private TemporalISO convexWeek(){
		int firstWeek = TemporalDate.getValueFromDate(first, "week");
		if (firstWeek > 0)
			return first;
		Map<String, Set<Integer>> tmpMap = first.getFullMapping();
		LocalDate tmpLocalDate = TemporalJoda.convertISOToLocalDate(second);
		tmpLocalDate = tmpLocalDate.plusWeeks(1);
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

	// Takes two TemporalDates, turns them into JodaTime LocalDate objects, and
	// compares them.
	// Assumes this.second is ref_time
	// Assumes f and s are TemporalDates with the fields "year", "month", and
	// "day", or
	// TemporalJoda.convertISOToLocalDate throws an error.
	public boolean areTemporallyOrdered(TemporalDate f, TemporalDate s) {
		LocalDate d1 = TemporalJoda.convertISOToLocalDate(f);
		LocalDate d2 = TemporalJoda.convertISOToLocalDate(s);
		LocalDate ref_time = TemporalJoda.convertISOToLocalDate(this.second);
		if ((ref_time.isBefore(d1)) && (d1.isBefore(d2)))
			return true;
		else if ((d1.isBefore(ref_time) || d1.isEqual(ref_time)) && (ref_time.isBefore(d2)))
			return false;
		else if (d2.isBefore(ref_time))
			throw new IllegalArgumentException(
					"We have two dates that are before ref_time, when at least one should be after it.");
		else if (d2.isBefore(d1)) {
			throw new IllegalArgumentException(
					"d2 is before d1, which shouldn't ever happen. In compareDates, TemporalNext");
		}
		else {
			throw new IllegalArgumentException(
				"Problem! Shouldn't be in this else clause. Could be because "
						+ f
						+ " and "
						+ s
						+ " are equal. Should test for this in predicate that calls this function.");
		}
	}

	private Set<Integer> addOne(Set<Integer> oldIntSet){
		Set<Integer> tmpInt = new HashSet<Integer>();
		for (int i : oldIntSet) {
			tmpInt.add(Integer.valueOf(i + 1));
		}
		return tmpInt;
	}
	
	private void testStoredDates() {
		if (!(first instanceof TemporalDate || first instanceof TemporalDuration)
				|| (!(second instanceof TemporalDate)))
			throw new IllegalArgumentException(
					"The two parameters to TemporalNext aren't TemporalDate objects, which they should be.");
	}
}
