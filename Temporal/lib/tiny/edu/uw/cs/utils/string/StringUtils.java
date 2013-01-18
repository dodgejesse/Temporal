package edu.uw.cs.utils.string;

/**
 * String services
 * 
 * @author Yoav Artzi
 */
public class StringUtils {
	
	private StringUtils() {
	}
	
	public static String lstrip(String string, char c) {
		return string.replaceAll(String.format("^[%c]+", c), "");
	}
	
	public static String multiply(String string, int multiplier) {
		final StringBuilder ret = new StringBuilder();
		for (int i = 0; i < multiplier; ++i) {
			ret.append(string);
		}
		return ret.toString();
	}
	
	public static String rstrip(String string, char c) {
		return string.replaceAll(String.format("[%c]+$", c), "");
	}
	
	public static String strip(String string, char c) {
		return string.replaceAll(String.format("(^[%c]+)|([%c]+$)", c, c), "");
	}
}
