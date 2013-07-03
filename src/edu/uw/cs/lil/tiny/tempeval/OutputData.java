package edu.uw.cs.lil.tiny.tempeval;

import java.util.Arrays;
import java.util.List;

public class OutputData {
	private int[] counters;
	private boolean set;
	/*
	 The elements in counters represent items in this order:
	 counter;
	 correct;
	 correctVal;
	 correctType;
	 incorrect;
	 tooManyParses;
	 notParsed;
	*/
	
	public OutputData(){
		set = false;
	}
	
	public void setCounters(int[] c){
		if (set)
			throw new IllegalArgumentException("Counters is already set, and you're trying to set it again!");
		else {
			counters = Arrays.copyOf(c, c.length);
			set = true;
		}
	}
	
	public String toString(){
		String s = "\n";
		s += "\n";
		s += "\n" + "Total phrases: " + counters[0];
		s += "\n" + "Number correctly parsed and executed, with correct type and val: " + counters[1]
						+ ", which is " + (double) counters[1] * 100 / counters[0] + " percent";
		s += "\n" + "Number parsed with correct val, but not type: " + counters[2] + ", which is "  + (double) counters[2] * 100 / counters[0] + " percent.";
		s += "\n" + "Number parsed with correct type, but not val: " + counters[3] + ", which is "  + (double) counters[3] * 100 / counters[0] + " percent.";
		s += "\n" + "Number parsed, but with incorrect type and val: "
						+ counters[4] + ", which his " + (double) counters[4] * 100 / counters[0] + " percent";
		s += "\n" + "Number with too many parses: " + counters[5];
		s += "\n" + "Number with no parses: " + counters[6];
		return s;
	}

	public static OutputData average(OutputData[] outList) {
		int[] totalCounters = {0,0,0,0,0,0,0};
		for (OutputData cur : outList){
			for (int i = 0; i < cur.getCounters().length; i++){
				totalCounters[i] += cur.getCounters()[i];
			}
		}
		OutputData tmp = new OutputData();
		tmp.setCounters(totalCounters);
		return tmp;
	}
	
	public int[] getCounters(){
		return Arrays.copyOf(counters, counters.length);
	}
}
