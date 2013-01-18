package edu.uw.cs.utils.composites;

import java.util.Comparator;

public class Pair<F, S> {
	private final F	first;
	private final S	second;
	
	private Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public static <F, S> Pair<F, S> of(F first, S second) {
		return new Pair<F, S>(first, second);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		final Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}
	
	public F first() {
		return first;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}
	
	public S second() {
		return second;
	}
	
	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
	
	public static class PairComparator<F, S> implements
			java.util.Comparator<Pair<F, S>> {
		private final Comparator<F>	firstComparator;
		private final Comparator<S>	secondComparator;
		
		public PairComparator(Comparator<F> firstComparator,
				Comparator<S> secondComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
		}
		
		public int compare(Pair<F, S> o1, Pair<F, S> o2) {
			final int firsts = firstComparator.compare(o1.first, o2.first);
			
			if (firsts == 0) {
				return secondComparator.compare(o1.second, o2.second);
			} else {
				return firsts;
			}
		}
	}
	
}
