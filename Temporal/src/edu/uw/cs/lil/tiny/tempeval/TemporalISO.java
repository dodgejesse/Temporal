package edu.uw.cs.lil.tiny.tempeval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class TemporalISO {
	private final String[] fields = { "year", "month", "week", "day", "hour",
			"minute", "weekday" };
	private final Map<String, Set<Integer>> value;

	public TemporalISO(Map<String, Set<Integer>> data) {
		this.value = new HashMap<String, Set<Integer>>();
		for (String s : data.keySet()) {
			Set<Integer> tmpSet = new HashSet<Integer>();
			tmpSet.addAll(data.get(s));
			this.value.put(s, tmpSet);
		}
	}

	public TemporalISO(String field, int num) {
		if (!stringInFields(field))
			throw new IllegalArgumentException(field
					+ " is not a valid key! Problem in TemporalISO.");
		this.value = new HashMap<String, Set<Integer>>();
		Set<Integer> newValueSet = new HashSet<Integer>();
		newValueSet.add(Integer.valueOf(num));
		this.value.put(field, newValueSet);
	}

	public Set<String> getKeys() {
		Set<String> tmp = new HashSet<String>();
		tmp.addAll(this.value.keySet());
		return tmp;
	}

	public Set<Integer> getVal(String key) {
		if (isSet(key)) {
			Set<Integer> tmp = new HashSet<Integer>();
			tmp.addAll(this.value.get(key));
			return tmp;
		}
		if (stringInFields(key)) {
			throw new IllegalArgumentException("Value for " + key
					+ " has not been set yet in TemporalISO.");
		}
		throw new IllegalArgumentException(key + " is not a valid key!");
	}

	public boolean isSet(String key) {
		return this.value.containsKey(key);
	}

	public boolean stringInFields(String f) {
		boolean keyInFields = false;
		for (int i = 0; i < this.fields.length; i++) {
			if (this.fields[i].equals(f)) {
				keyInFields = true;
			}
		}
		return keyInFields;
	}
	
	// Sort of a hack, doesn't work if there is more than one value for a given
	// field!
	public static int getValueFromDate(TemporalISO d, String s) {
		int value = -1;
		if (d.getVal(s).size() > 1) {
			throw new IllegalArgumentException(
					"There is more than one value for " + s
							+ " and that's not implemented.");
		}
		for (int i : d.getVal(s)) {
			value = i;
		}
		if (value == -1) {
			throw new IllegalArgumentException(
					"Problem getting value in getValueFromDate, within TemporalISO");
		}
		return value;
	}
}
