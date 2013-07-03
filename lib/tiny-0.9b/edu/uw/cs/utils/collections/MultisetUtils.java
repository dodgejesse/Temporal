package edu.uw.cs.utils.collections;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Utility methods for {@link Multiset}.
 * 
 * @author Yoav Artzi
 */
public class MultisetUtils {
	
	public static <T> boolean isContained(Iterable<T> iterable,
			Multiset<T> multiset) {
		return isContained(HashMultiset.create(iterable), multiset);
	}
	
	/**
	 * Checks if all the entries in a given set are contained in another,
	 * including their counts.
	 * 
	 * @param contained
	 * @param containing
	 * @return
	 */
	public static <T> boolean isContained(Multiset<T> contained,
			Multiset<T> containing) {
		for (final Multiset.Entry<T> entry : contained.entrySet()) {
			if (!containing.contains(entry.getElement())
					|| !(containing.count(entry.getElement()) >= entry
							.getCount())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Iterates over the given iterable, for each item, removes a single item
	 * from source.
	 * 
	 * @param iterable
	 * @param source
	 */
	public static <T> void substractCollection(Iterable<T> iterable,
			Multiset<T> source) {
		for (final T t : iterable) {
			source.remove(t);
		}
	}
	
	/**
	 * Removes the element in removed form the source multiset, taking counts
	 * into account.
	 * 
	 * @param removed
	 * @param source
	 */
	public static <T> void substractMultiset(Multiset<T> removed,
			Multiset<T> source) {
		for (final Multiset.Entry<T> entry : removed.entrySet()) {
			source.remove(entry.getElement(), entry.getCount());
		}
	}
	
}
