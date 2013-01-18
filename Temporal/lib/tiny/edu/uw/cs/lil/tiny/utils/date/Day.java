package edu.uw.cs.lil.tiny.utils.date;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a weekday, in a 7 days week.
 * 
 * @author Yoav Artzi
 */
public enum Day {
	FRI("fri", 6), MON("mon", 2), SAT("sat", 7), SUN("sun", 1), THU("thu", 5), TUE(
			"tue", 3), WED("wed", 4);
	
	public static Comparator<Day>			COMPARATOR	= new DayComparator();
	
	private static final Map<Object, Day>	DAY_MAPPING	= new ConcurrentHashMap<Object, Day>();
	
	private final String					label;
	
	private final int						order;
	
	private Day(String label, int order) {
		this.label = label;
		this.order = order;
	}
	
	/**
	 * Get the day mapped to a given object.
	 * 
	 * @param key
	 * @return
	 */
	public static Day getDay(Object key) {
		return DAY_MAPPING.get(key);
	}
	
	/**
	 * Set mapping of a user defined object to a Day objet.
	 * 
	 * @param key
	 * @param day
	 */
	public static void setDayMapping(Object key, Day day) {
		DAY_MAPPING.put(key, day);
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	private static class DayComparator implements Comparator<Day> {
		
		@Override
		public int compare(Day o1, Day o2) {
			return o1.order - o2.order;
		}
		
	}
}
