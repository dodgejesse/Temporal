package edu.uw.cs.utils.assertion;

public class Assert {
	private Assert() {
	}
	
	public static <T> T ifNull(T o) {
		if (o == null) {
			throw new IllegalStateException("Assert on null");
		}
		return o;
	}
	
}
