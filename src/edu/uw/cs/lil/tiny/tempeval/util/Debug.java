package edu.uw.cs.lil.tiny.tempeval.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Debug {
	public static enum Type {
		PROGRESS, STATS, ATTRIBUTE, DETECTION, DEBUG, ERROR;
	}
	private static Map<Type, Set<PrintStream>> filter = new HashMap<Type, Set<PrintStream>>();

	public static void addFilter(PrintStream out, Type... types) {
		for (Type t : types){
			if (!filter.containsKey(t))
				filter.put(t, new HashSet<PrintStream>());
			filter.get(t).add(out);
		}
	}
	
	public static void addFilter(String filename, Type... types) throws FileNotFoundException {
		addFilter(new PrintStream(new File(filename)), types);
	}

	public static void println(Type t, Object s) {
		if (filter.containsKey(t))
			for(PrintStream out : filter.get(t))
				out.println(s);
	}
	
	public static void println(Type t) {
		if (filter.containsKey(t))
			for(PrintStream out : filter.get(t))
				out.println();
	}
	
	public static void print(Type t, Object s) {
		if (filter.containsKey(t))
			for(PrintStream out : filter.get(t))
				out.print(s);
	}
	
	public static void printf(Type t, String s, Object... args) {
		if (filter.containsKey(t))
			for(PrintStream out : filter.get(t))
				out.printf(s, args);
	}
}
