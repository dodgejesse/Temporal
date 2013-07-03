package edu.uw.cs.utils.counter;

public class Counter implements IImmutableCounter {
	private int	count	= 0;
	
	public Counter() {
		this(0);
	}
	
	public Counter(int i) {
		this.count = i;
	}
	
	public void dec() {
		--count;
	}
	
	public void dec(int value) {
		count -= value;
	}
	
	public void inc() {
		count++;
	}
	
	public void inc(int value) {
		count += value;
	}
	
	@Override
	public String toString() {
		return Integer.toString(count);
	}
	
	@Override
	public int value() {
		return count;
	}
}
