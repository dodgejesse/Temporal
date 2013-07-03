package edu.uw.cs.utils.text;

/**
 * Styles terminal text in ANSI mode.
 * 
 * @author Yoav Artzi
 */
public enum TermStyle {
	
	BG_BLACK("\033[40m"), BG_BLUE("\033[44m"), BG_CYAN("\033[46m"), BG_GREEN(
			"\033[42m"), BG_MAGENTA("\033[45m"), BG_RED("\033[41m"), BG_WHITE(
			"\033[47m"), BG_YELLOW("\033[43m"), BOLD("\033[1m"), FG_BLACK(
			"\033[30m"), FG_BLUE("\033[34m"), FG_CYAN("\033[36m"), FG_GREEN(
			"\033[32m"), FG_MAGENTA("\033[35m"), FG_RED("\033[31m"), FG_WHITE(
			"\033[37m"), FG_YELLOW("\033[33m"), INVERSE("\033[7m"), ITALIC(
			"\033[3m"), RESET("\033[0m"), STRIKETHROUGH("\033[9m"), UNDERLINE(
			"\033[4m");
	
	private final String	code;
	
	private TermStyle(String code) {
		this.code = code;
	}
	
	/**
	 * Color a given string. Resets style after the string ends.
	 * 
	 * @param text
	 * @param color
	 * @return styled string
	 */
	public static String color(String text, TermStyle color) {
		return color.code + text + RESET.code;
	}
	
	/**
	 * Demos all the possible styles.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final TermStyle[] enumConstants = TermStyle.class.getEnumConstants();
		
		for (final TermStyle color : enumConstants) {
			System.out.println(color(color.name(), color));
		}
	}
	
	@Override
	public String toString() {
		return code;
	}
	
}
