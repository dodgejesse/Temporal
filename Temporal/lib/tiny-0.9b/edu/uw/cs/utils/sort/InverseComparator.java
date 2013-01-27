package edu.uw.cs.utils.sort;

import java.util.Comparator;

/**
 * Provides the opposite order of a given comparator.
 * 
 * @author Yoav Artzi
 * @param <T>
 */
public class InverseComparator<T> implements Comparator<T> {
	private final Comparator<T>	comparator;
	
	public InverseComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	public static <T> InverseComparator<T> create(Comparator<T> comparator) {
		return new InverseComparator<T>(comparator);
	}
	
	public int compare(T o1, T o2) {
		final int ret = comparator.compare(o1, o2);
		
		if (ret == 0) {
			return ret;
		} else {
			return -ret;
		}
	}
	
}
