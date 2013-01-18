package edu.uw.cs.lil.tiny.utils.date;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a gregorian month.
 * 
 * @author Yoav Artzi
 */
public enum Month {
	
	APR("apr", 4), AUG("aug", 8), DEC("dec", 12), FEB("feb", 2), JAN("jan", 1), JUL(
			"jul", 7), JUN("jun", 6), MAR("mar", 3), MAY("may", 5), NOV("nov",
			11), OCT("oct", 10), SEP("sep", 9);
	
	public static final Comparator<Month>	COMPARATOR		= new MonthComparator();
	
	private static final Map<Object, Month>	MONTH_MAPPING	= new ConcurrentHashMap<Object, Month>();
	
	private final String					label;
	
	private final int						order;
	
	private Month(String label, int order) {
		this.label = label;
		this.order = order;
	}
	
	/**
	 * Get the month mapped to a given object.
	 * 
	 * @param key
	 * @return
	 */
	public static Month getMonth(Object key) {
		return MONTH_MAPPING.get(key);
	}
	
	/**
	 * Inits a mapping of a month to an user defined object.
	 * 
	 * @param key
	 * @param month
	 */
	public static void setMonthMapping(Object key, Month month) {
		MONTH_MAPPING.put(key, month);
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	private static class MonthComparator implements Comparator<Month> {
		@Override
		public int compare(Month o1, Month o2) {
			return o1.order - o2.order;
		}
	}
}
