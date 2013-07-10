package edu.uw.cs.lil.tiny.tempeval.readdata;

import edu.uw.cs.utils.composites.Pair;


public class Timex {
	private String type;
	private String value;
	private Timex anchor;
	private Pair<Integer, Integer> tokenRange; //inclusive-exclusive

	// Temporary character offset during preprocessing
	private int offset;
	private String text;
	
	public Timex(String type, String value, Timex anchor, int offset) {
		this.type = type;
		this.value = value;
		this.anchor = anchor;
		this.offset = offset;
	}
	
	public void setTokenRange(int start, int end) {
		tokenRange = Pair.of(start, end - 1);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public int getStartChar() {
		return offset;
	}
	
	public int getEndChar() {
		return offset + text.length();
	}
	
	public String toString() {
		return "[" + tokenRange.first() + "-" + tokenRange.second() + "]";
	}
}