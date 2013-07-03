package edu.uw.cs.utils.composites;

public class Triplet<F, S, T> {
	private final F	first;
	private final S	second;
	private final T	third;
	
	public Triplet(F first, S second, T third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public static <F, S, T> Triplet<F, S, T> of(F first, S second, T third) {
		return new Triplet<F, S, T>(first, second, third);
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
		final Triplet other = (Triplet) obj;
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
		if (third == null) {
			if (other.third != null) {
				return false;
			}
		} else if (!third.equals(other.third)) {
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
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}
	
	public S second() {
		return second;
	}
	
	public T third() {
		return third;
	}
	
	@Override
	public String toString() {
		return "(" + first.toString() + ", " + second.toString() + ", "
				+ third.toString() + ")";
	}
	
}
