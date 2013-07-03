package edu.uw.cs.utils.log;

import java.io.PrintStream;

public class Log {
	
	private final PrintStream	err;
	
	public Log() {
		this(System.err);
	}
	
	public Log(PrintStream err) {
		this.err = err;
	}
	
	public void println(String string) {
		err.println(string);
	}
}
