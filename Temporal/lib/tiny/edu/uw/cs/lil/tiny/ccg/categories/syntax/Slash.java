package edu.uw.cs.lil.tiny.ccg.categories.syntax;


public class Slash {
	public static final Slash	BACKWARD	= new Slash('\\');
	public static final Slash	FORWARD		= new Slash('/');
	public static final Slash	VERTICAL	= new Slash('|');
	
	private final char			c;
	
	private Slash(char c) {
		this.c = c;
	}
	
	public static Slash getSlash(char c) {
		if (c == BACKWARD.getChar()) {
			return BACKWARD;
		} else if (c == FORWARD.getChar()) {
			return FORWARD;
		} else if (c == VERTICAL.getChar()) {
			return VERTICAL;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	public char getChar() {
		return c;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		return result;
	}
	
	@Override
	public String toString() {
		return String.valueOf(c);
	}
}