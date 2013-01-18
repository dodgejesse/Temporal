package edu.uw.cs.lil.tiny.mr.lambda.exec.naive;

import java.util.Arrays;

import edu.uw.cs.utils.collections.ListUtils;

public class Tuple {
	private final Object[]	keys;
	private final Object	value;
	
	public Tuple(Object[] keys, Object value) {
		this.keys = keys;
		this.value = value;
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
		final Tuple other = (Tuple) obj;
		if (!Arrays.equals(keys, other.keys)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
	
	public Object get(int i) {
		return keys[i];
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keys);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	public int numKeys() {
		return keys.length;
	}
	
	public Tuple subTuple(int start, int end) {
		return new Tuple(Arrays.copyOfRange(keys, start, end), value);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(ListUtils.join(Arrays.asList(keys), ","))
				.append(" -> ").append(value).toString();
	}
}
