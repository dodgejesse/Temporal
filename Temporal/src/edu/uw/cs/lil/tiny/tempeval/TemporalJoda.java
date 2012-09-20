package edu.uw.cs.lil.tiny.tempeval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

public class TemporalJoda {
	
	public static LocalDate convertISOToLocalDate(TemporalISO d) {
		if (!d.isSet("year") || !d.isSet("month") || !d.isSet("day")){
			throw new IllegalArgumentException("The date " + d + " doesn't have all the required fields to specify a LocalDate ojbect.");
		}
		int year = TemporalISO.getValueFromDate(d, "year");
		int month = TemporalISO.getValueFromDate(d, "month");
		int day = TemporalISO.getValueFromDate(d, "day");
		return new LocalDate(year, month, day);
	}
	
	public static TemporalDate convertLocalDateToISO(LocalDate date) {
		Map<String, Set<Integer>> tmpMap = new HashMap<String, Set<Integer>>();
		tmpMap.put("year", new HashSet<Integer>());
		tmpMap.put("month", new HashSet<Integer>());
		tmpMap.put("day", new HashSet<Integer>());
		(tmpMap.get("year")).add(Integer.valueOf(date.getYear()));
		(tmpMap.get("month")).add(Integer.valueOf(date.getMonthOfYear()));
		(tmpMap.get("day")).add(Integer.valueOf(date.getDayOfMonth()));
		return new TemporalDate(tmpMap);
	}
	

}
