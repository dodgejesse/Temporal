package edu.uw.cs.utils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ListUtils {
	private static final List<Object>	EMPTY_LIST	= Collections
															.unmodifiableList(new ArrayList<Object>(
																	0));
	
	public static <T> List<T> concat(List<T> l1, List<T> l2) {
		final ArrayList<T> concat = new ArrayList<T>(l1.size() + l2.size());
		concat.addAll(l1);
		concat.addAll(l2);
		return concat;
	}
	
	public static <T> List<T> createList(T... items) {
		return Arrays.asList(items);
	}
	
	public static <T> List<T> createSingletonList(T item) {
		final ArrayList<T> list = new ArrayList<T>(1);
		list.add(item);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	final public static <T> List<T> getEmptyImmutableList() {
		return (List<T>) EMPTY_LIST;
	}
	
	public static <T> boolean isPrefix(List<T> source, List<T> target) {
		if (target.size() > source.size()) {
			return false;
		}
		
		final Iterator<T> targetIterator = target.iterator();
		final Iterator<T> sourceIterator = source.iterator();
		while (targetIterator.hasNext()) {
			if (!targetIterator.next().equals(sourceIterator.next())) {
				return false;
			}
		}
		
		return true;
	}
	
	public static <T> boolean isPrefixOrSuffix(List<T> source, List<T> target) {
		if (target.size() > source.size()) {
			return false;
		}
		
		boolean isSuffix = true;
		boolean isPrefix = true;
		final Iterator<T> subIterator = target.iterator();
		final ListIterator<T> prefixIterator = source.listIterator();
		final ListIterator<T> suffixIterator = source.listIterator(source
				.size() - target.size());
		while ((isSuffix || isPrefix) && subIterator.hasNext()) {
			final T subNext = subIterator.next();
			isSuffix &= (isSuffix && suffixIterator.hasNext() && subNext
					.equals(suffixIterator.next()));
			isPrefix &= (isPrefix && prefixIterator.hasNext() && subNext
					.equals(prefixIterator.next()));
		}
		return isSuffix || isPrefix;
	}
	
	public static <T> int isSubList(List<T> list, List<T> sub) {
		final int len = list.size();
		final int lenSub = sub.size();
		final int boundList = len - lenSub + 1;
		for (int i = 0; i < boundList; ++i) {
			for (int j = 0; j < lenSub; ++j) {
				final T listItem = list.get(i + j);
				final T subItem = sub.get(j);
				if ((listItem == null && subItem == null)
						|| (listItem != null && listItem.equals(subItem))) {
					if (j + 1 == lenSub) {
						return i;
					}
				} else {
					break;
				}
			}
		}
		return -1;
	}
	
	public static <T> boolean isSuffix(List<T> source, List<T> target) {
		if (target.size() > source.size()) {
			return false;
		}
		
		final Iterator<T> targetIterator = target.iterator();
		final ListIterator<T> sourceIterator = source.listIterator(source
				.size() - target.size());
		while (targetIterator.hasNext()) {
			if (!targetIterator.next().equals(sourceIterator.next())) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Emulate Python's join method.
	 * 
	 * @param list
	 * @param sep
	 * @return
	 */
	final public static <T> String join(Iterable<T> list, String sep) {
		final StringBuilder ret = new StringBuilder();
		final Iterator<T> iterator = list.iterator();
		while (iterator.hasNext()) {
			ret.append(iterator.next());
			if (iterator.hasNext()) {
				ret.append(sep);
			}
		}
		return ret.toString();
	}
	
	/**
	 * Emulate Pytho's map method.
	 * 
	 * @param list
	 * @param mapper
	 * @return
	 */
	final public static <I, O> List<O> map(Iterable<? extends I> list,
			Mapper<I, O> mapper) {
		final List<O> ret;
		if (list instanceof Collection<?>) {
			ret = new ArrayList<O>(((Collection<?>) list).size());
		} else {
			ret = new LinkedList<O>();
		}
		
		for (final I obj : list) {
			ret.add(mapper.process(obj));
		}
		return ret;
	}
	
	/**
	 * Get the maximum value from a list of integers.
	 * 
	 * @param list
	 * @return
	 */
	final public static Integer max(List<Integer> list) {
		Integer ret = null;
		for (final Integer i : list) {
			if (i != null && (ret == null || i >= ret)) {
				ret = i;
			}
		}
		return ret;
	}
	
	public static interface Mapper<I, O> {
		public O process(I obj);
	}
	
}
