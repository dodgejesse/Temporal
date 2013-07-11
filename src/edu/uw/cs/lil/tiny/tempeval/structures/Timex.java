package edu.uw.cs.lil.tiny.tempeval.structures;

public class Timex implements java.io.Serializable{
	private static final long serialVersionUID = -5859852309847402300L;
	private String type;
	private String value;
	private int tokenStart;
	private int tokenEnd; //inclusive-exclusive
	
	// Temporary variables used only during preprocessing
	private int offset; //character offset
	private String text;
	
	public Timex(String type, String value, int offset) {
		this.type = type;
		this.value = value;
		this.offset = offset;
	}
	
	public void setTokenRange(int tokenStart, int tokenEnd) {
		this.tokenStart = tokenStart;
		this.tokenEnd = tokenEnd + 1;
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
	
	public int getStartToken() {
		return tokenStart;
	}
	
	public int getEndToken() {
		return tokenEnd;
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return text + "(" + value + ")";
	}
	
	public String prettyString() {
		return "[" + tokenStart + "-" + tokenEnd + "]";
	}
}