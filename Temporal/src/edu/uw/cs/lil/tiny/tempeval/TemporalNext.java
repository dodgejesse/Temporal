package edu.uw.cs.lil.tiny.tempeval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.joda.time.LocalDate;

public class TemporalNext extends TemporalPredicate {
	public TemporalISO perform() {
		testStoredDates();
		return findNext();
	}

	private TemporalDate compareDates(TemporalDate f, TemporalDate s) {
		LocalDate d1 = makeLocalDate(f);
		LocalDate d2 = makeLocalDate(s);
		LocalDate ref_time = makeLocalDate(this.second);
		if ((ref_time.isBefore(d1)) && (d1.isBefore(d2)))
			return f;
		if ((d1.isBefore(ref_time)) && (ref_time.isBefore(d2)))
			return s;
		if (d2.isBefore(ref_time))
			throw new IllegalArgumentException(
					"We have two dates that are before ref_time, when at least one should be after it.");
		if (d2.isBefore(d1)) {
			throw new IllegalArgumentException(
					"d2 is before d1, which shouldn't ever happen. In compareDates, TemporalNext");
		}
		throw new IllegalArgumentException(
				"Problem! Shouldn't be in this else clause.");
	}

	private TemporalISO findNext() {
		TemporalDate nextDate;
		if (this.first.getKeys().contains("year")) {
			nextDate = (TemporalDate) this.first;
		} else {
			if ((this.first.getKeys().contains("month"))
					&& (!this.first.getKeys().contains("day"))) {
				int ref_timeMonth = getValueFromDate(this.second, "month");
				int dMonth = getValueFromDate(this.first, "month");
				Map<String, Set<Integer>> tmpMap = makeMap(this.first);
				if (ref_timeMonth < dMonth) {
					tmpMap.put("year", this.second.getVal("year"));
				} else {
					Set<Integer> tmpInt = new HashSet<Integer>();
					for (int i : tmpMap.get("year")) {
						tmpInt.add(Integer.valueOf(i + 1));
					}
					tmpMap.put("year", tmpInt);
				}
				nextDate = new TemporalDate(tmpMap);
			} else {
				if ((this.first.getKeys().contains("month"))
						&& (this.first.getKeys().contains("day"))) {
					Map<String, Set<Integer>> tmpMap = makeMap(this.first);
					tmpMap.put("year", this.second.getVal("year"));
					TemporalDate firstTmpDate = new TemporalDate(tmpMap);
					Set<Integer> tmpInt = new HashSet<Integer>();
					for (int i : tmpMap.get("year")){
						tmpInt.add(Integer.valueOf(i + 1));
					}
					tmpMap.put("year", tmpInt);
					TemporalDate secondTmpDate = new TemporalDate(tmpMap);
					nextDate = compareDates(firstTmpDate, secondTmpDate);
				} else {
					if ((this.first.getKeys().contains("weekday"))
							&& (!this.first.getKeys().contains("month"))
							&& (!this.first.getKeys().contains("day"))) {
						LocalDate date = makeLocalDate(this.second);
						if (date.getDayOfWeek() == getValueFromDate(this.first,
								"weekday")) {
							date.plusDays(1);
						}
						while (date.getDayOfWeek() != getValueFromDate(
								this.first, "weekday")) {
							date = date.plusDays(1);
						}
						nextDate = makeDateFromJodaLocalDate(date);
					} else {
						throw new IllegalArgumentException(
								"haven't implemented things other than dates with months or days.");
					}
				}
			}
		}
 		return nextDate;
	}

	// Sort of a hack, doesn't work if there is more than one value for a given
	// field!
	private int getValueFromDate(TemporalISO d, String s) {
		int value = -1;
		if (d.getVal(s).size() > 1) {
			throw new IllegalArgumentException(
					"There is more than one value for " + s
							+ " and that's not implemented.");
		}
		for (int i : d.getVal(s)) {
			value = i;
		}
		if (value == -1) {
			throw new IllegalArgumentException(
					"Problem getting value in getValueFromDate");
		}
		return value;
	}

	private TemporalDate makeDateFromJodaLocalDate(LocalDate date) {
		Map<String, Set<Integer>> tmpMap = new HashMap<String, Set<Integer>>();
		tmpMap.put("year", new HashSet<Integer>());
		tmpMap.put("month", new HashSet<Integer>());
		tmpMap.put("day", new HashSet<Integer>());
		(tmpMap.get("year")).add(Integer.valueOf(date.getYear()));
		(tmpMap.get("month")).add(Integer.valueOf(date.getMonthOfYear()));
		(tmpMap.get("day")).add(Integer.valueOf(date.getDayOfMonth()));
		return new TemporalDate(tmpMap);
	}

	private LocalDate makeLocalDate(TemporalISO d) {
		int year = getValueFromDate(d, "year");
		int month = getValueFromDate(d, "month");
		int day = getValueFromDate(d, "day");
		return new LocalDate(year, month, day);
	}

	private Map<String, Set<Integer>> makeMap(TemporalISO f) {
		Map<String, Set<Integer>> tmpMap = new HashMap<String, Set<Integer>>();
		for (String s : f.getKeys()) {
			tmpMap.put(s, new HashSet<Integer>());
			(tmpMap.get(s)).addAll(f.getVal(s));
		}
		return tmpMap;
	}

	private void testStoredDates() {
		if ((!this.first.getClass().toString().endsWith("TemporalDate"))
				|| (!this.second.getClass().toString().endsWith("TemporalDate")))
			throw new IllegalArgumentException(
					"The two parameters to TemporalNext aren't TemporalDate objects, which they should be.");
	}
}
