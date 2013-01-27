package edu.uw.cs.utils.collections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetUtils {
	
	public static <T> Set<T> createSingleton(T item) {
		final Set<T> set = new HashSet<T>();
		set.add(item);
		return Collections.unmodifiableSet(set);
	}
	
	public static boolean isIntersecting(Set<?> s1, Set<?> s2) {
		if (s1.size() >= s2.size()) {
			for (final Object o : s2) {
				if (s1.contains(o)) {
					return true;
				}
			}
			return false;
		} else {
			return isIntersecting(s2, s1);
		}
	}
	
	public static <T> Set<T> retainAll(Set<T> set, Set<? extends T> superset) {
		set.retainAll(superset);
		return set;
	}
}
