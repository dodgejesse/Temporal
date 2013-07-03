package edu.uw.cs.utils.sort;

import java.util.Comparator;

public class PrimitiveComparators {
	public static final Comparator<Integer>	INTEGER_COMPARATOR	= new IntegerComparator();
	public static final Comparator<String>	STRING_COMPARATOR	= new StringComparator();
	
	private PrimitiveComparators() {
	}
	
	/**
	 * Comparator for Floats.
	 * 
	 * @author Yoav Artzi
	 */
	public static class FloatComparator implements Comparator<Float> {
		public int compare(Float o1, Float o2) {
			return o1.compareTo(o2);
		}
		
	}
	
	/**
	 * Comparator for Integers.
	 * 
	 * @author Yoav Artzi
	 */
	public static class IntegerComparator implements Comparator<Integer> {
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	}
	
	/**
	 * Case sensitive string comparator.
	 * 
	 * @see String#CASE_INSENSITIVE_ORDER for case insensitive comparator
	 * @author Yoav Artzi
	 */
	public static class StringComparator implements Comparator<String> {
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	}
	
}
