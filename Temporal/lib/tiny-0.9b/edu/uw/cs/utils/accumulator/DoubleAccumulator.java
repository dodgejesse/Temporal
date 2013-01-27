package edu.uw.cs.utils.accumulator;

public class DoubleAccumulator {
	
	private double	value;
	
	public DoubleAccumulator() {
		this(0.0);
	}
	
	public DoubleAccumulator(double init) {
		this.value = init;
	}
	
	public double add(double num) {
		value += num;
		return value;
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
		final DoubleAccumulator other = (DoubleAccumulator) obj;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	@Override
	public String toString() {
		return "DoubleAccumulator [value=" + value + "]";
	}
	
	public double value() {
		return value;
	}
	
}
