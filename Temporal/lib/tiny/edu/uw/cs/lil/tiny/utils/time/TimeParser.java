package edu.uw.cs.lil.tiny.utils.time;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jregex.Matcher;
import jregex.Pattern;

public class TimeParser {
	private static final Map<String, AmPm>		AM_PM_STRINGS	= new ConcurrentHashMap<String, AmPm>();
	private static final Map<Integer, AmPm>		AMPM_DEFAULTS	= new ConcurrentHashMap<Integer, AmPm>();
	private static final Map<String, Integer>	HOUR_TO_INT		= new ConcurrentHashMap<String, Integer>();
	private static final Map<String, Integer>	TEENS			= new ConcurrentHashMap<String, Integer>();
	private static final Map<String, Integer>	TENS_DIGIT		= new ConcurrentHashMap<String, Integer>();
	private static final Map<String, Integer>	UNIT_DIGIT		= new ConcurrentHashMap<String, Integer>();
	private final Pattern						pattern;
	
	public TimeParser() {
		final StringBuilder patternBuilder = new StringBuilder();
		
		// Hour
		{
			patternBuilder.append("({hour}(");
			final Iterator<String> iterator = HOUR_TO_INT.keySet().iterator();
			while (iterator.hasNext()) {
				patternBuilder.append(iterator.next());
				if (iterator.hasNext()) {
					patternBuilder.append(")|(");
				}
			}
			patternBuilder.append("))");
		}
		
		// Minutes, optional
		{
			// 12, 15, 16....
			patternBuilder.append("(()|( ({teens}(");
			{
				// Add teens
				final Iterator<String> iterator = TEENS.keySet().iterator();
				while (iterator.hasNext()) {
					patternBuilder.append(iterator.next());
					if (iterator.hasNext()) {
						patternBuilder.append(")|(");
					}
				}
			}
			patternBuilder.append("))");
			
			// 20, 30, 21, 31, 41 ...
			patternBuilder.append("|( ({tens}(");
			{
				final Iterator<String> iterator = TENS_DIGIT.keySet()
						.iterator();
				while (iterator.hasNext()) {
					patternBuilder.append(iterator.next());
					if (iterator.hasNext()) {
						patternBuilder.append(")|(");
					}
				}
				
			}
			patternBuilder.append("))");
			
			// 1, 2, 3 ...
			patternBuilder.append("(()|( ({units}(");
			{
				final Iterator<String> iterator = UNIT_DIGIT.keySet()
						.iterator();
				while (iterator.hasNext()) {
					patternBuilder.append(iterator.next());
					if (iterator.hasNext()) {
						patternBuilder.append(")|(");
					}
				}
			}
			patternBuilder.append(")))))");
			
			patternBuilder.append("))");
		}
		
		// Possible "o'clock" before the AM/PM indicator
		patternBuilder.append("(()| o'clock)");
		
		// AM/PM
		{
			patternBuilder.append("(()| ({ampm}(");
			final Iterator<String> iterator = AM_PM_STRINGS.keySet().iterator();
			while (iterator.hasNext()) {
				patternBuilder.append(iterator.next());
				if (iterator.hasNext()) {
					patternBuilder.append(")|(");
				}
			}
			patternBuilder.append(")))");
		}
		
		pattern = new Pattern(patternBuilder.toString());
		
	}
	
	static {
		// AM/PM
		AM_PM_STRINGS.put("a m", AmPm.AM);
		AM_PM_STRINGS.put("eh m", AmPm.AM);
		AM_PM_STRINGS.put("a.m.", AmPm.AM);
		AM_PM_STRINGS.put("a. m.", AmPm.AM);
		AM_PM_STRINGS.put("a . m .", AmPm.AM);
		AM_PM_STRINGS.put("am", AmPm.AM);
		AM_PM_STRINGS.put("p m", AmPm.PM);
		AM_PM_STRINGS.put("pm", AmPm.PM);
		AM_PM_STRINGS.put("p.m.", AmPm.PM);
		AM_PM_STRINGS.put("p. m.", AmPm.PM);
		AM_PM_STRINGS.put("p . m .", AmPm.PM);
		AM_PM_STRINGS.put("in the morning", AmPm.AM);
		AM_PM_STRINGS.put("in the evening", AmPm.PM);
		AM_PM_STRINGS.put("in the afternoon", AmPm.PM);
		
		// Strings describing the hour
		HOUR_TO_INT.put("noon", 12);
		HOUR_TO_INT.put("one", 1);
		HOUR_TO_INT.put("two", 2);
		HOUR_TO_INT.put("three", 3);
		HOUR_TO_INT.put("four", 4);
		HOUR_TO_INT.put("five", 5);
		HOUR_TO_INT.put("six", 6);
		HOUR_TO_INT.put("seven", 7);
		HOUR_TO_INT.put("eight", 8);
		HOUR_TO_INT.put("nine", 9);
		HOUR_TO_INT.put("ten", 10);
		HOUR_TO_INT.put("eleven", 11);
		HOUR_TO_INT.put("twelve", 12);
		HOUR_TO_INT.put("midnight", 0);
		
		// Unit digit
		UNIT_DIGIT.put("one", 1);
		UNIT_DIGIT.put("two", 2);
		UNIT_DIGIT.put("three", 3);
		UNIT_DIGIT.put("four", 4);
		UNIT_DIGIT.put("five", 5);
		UNIT_DIGIT.put("six", 6);
		UNIT_DIGIT.put("seven", 7);
		UNIT_DIGIT.put("eight", 8);
		UNIT_DIGIT.put("nine", 9);
		
		// Tens digit
		TENS_DIGIT.put("oh", 0);
		TENS_DIGIT.put("o '", 0);
		TENS_DIGIT.put("twenty", 20);
		TENS_DIGIT.put("thirty", 30);
		TENS_DIGIT.put("forty", 40);
		TENS_DIGIT.put("fifty", 50);
		
		// Teens
		TEENS.put("ten", 10);
		TEENS.put("eleven", 11);
		TEENS.put("twelve", 12);
		TEENS.put("thirteen", 13);
		TEENS.put("fourteen", 14);
		TEENS.put("fifteen", 15);
		TEENS.put("sixteen", 16);
		TEENS.put("seventeen", 17);
		TEENS.put("eighteen", 18);
		TEENS.put("nineteen", 19);
		
		// AM/PM defaults
		AMPM_DEFAULTS.put(0, AmPm.AM);
		AMPM_DEFAULTS.put(1, AmPm.PM);
		AMPM_DEFAULTS.put(2, AmPm.PM);
		AMPM_DEFAULTS.put(3, AmPm.PM);
		AMPM_DEFAULTS.put(4, AmPm.PM);
		AMPM_DEFAULTS.put(5, AmPm.PM);
		AMPM_DEFAULTS.put(6, AmPm.PM);
		AMPM_DEFAULTS.put(7, AmPm.AM);
		AMPM_DEFAULTS.put(8, AmPm.AM);
		AMPM_DEFAULTS.put(9, AmPm.AM);
		AMPM_DEFAULTS.put(10, AmPm.AM);
		AMPM_DEFAULTS.put(11, AmPm.AM);
		AMPM_DEFAULTS.put(12, AmPm.PM);
		
	}
	
	public static void main(String[] args) {
		
		// TODO [yoav] [test]
		
		final List<String> samples = new LinkedList<String>();
		samples.add("seven twenty seven");
		samples.add("eleven o'clock in the morning");
		samples.add("four thirty five o'clock");
		samples.add("eight twenty one a m");
		samples.add("eleven eleven a m");
		samples.add("four forty five p. m.");
		samples.add("six fifteen a. m.");
		samples.add("six twenty one a. m.");
		samples.add("twelve twenty p. m.");
		samples.add("ten thirty four a m");
		samples.add("ten fifty five a m");
		samples.add("seven forty a. m.");
		samples.add("eight fifty four a m");
		samples.add("four thirty five p. m.");
		samples.add("five thirteen p. m.");
		samples.add("twelve fifteen p. m.");
		samples.add("eight forty eight a. m.");
		samples.add("seven fifty eight a. m.");
		samples.add("seven fifteen a. m.");
		samples.add("one a. m.");
		samples.add("eleven forty a m");
		samples.add("eleven fifty five a. m.");
		samples.add("one eighteen p. m.");
		samples.add("seven ten a. m.");
		samples.add("seven thirteen a. m.");
		samples.add("eight thirty a. m.");
		samples.add("seven nineteen p. m.");
		samples.add("four p. m.");
		samples.add("ten eleven a. m.");
		samples.add("three fifteen p. m.");
		samples.add("seven p. m.");
		samples.add("two twenty one p. m.");
		samples.add("six twenty five a. m.");
		samples.add("six fifty a. m.");
		samples.add("five fifty a. m.");
		samples.add("seven thirty five a. m.");
		samples.add("five sixteen p. m.");
		samples.add("seven oh nine p. m.");
		samples.add("eight forty seven p. m.");
		samples.add("seven forty six a m");
		samples.add("four ten p. m.");
		samples.add("four twenty p. m.");
		samples.add("seven thirty a m");
		samples.add("five fifty two p. m.");
		samples.add("ten fifty seven p. m.");
		samples.add("nine forty seven p. m.");
		samples.add("five oh one p. m.");
		samples.add("three p. m.");
		samples.add("nine thirty eight p. m.");
		samples.add("one a m");
		samples.add("twelve thirty three p. m.");
		samples.add("eleven fifty a. m.");
		samples.add("five thirty seven p. m.");
		samples.add("twelve fourteen p. m.");
		samples.add("six a. m.");
		samples.add("eight twenty five a. m.");
		samples.add("nine oh five a. m.");
		samples.add("seven forty five a m");
		samples.add("three twenty five p. m.");
		samples.add("seven fifteen p. m.");
		samples.add("eight twenty eight p. m.");
		samples.add("twelve oh six p. m.");
		samples.add("six oh eight p. m.");
		samples.add("two ten p. m.");
		samples.add("eleven oh five a. m.");
		samples.add("six twenty p. m.");
		samples.add("three fifty p. m.");
		samples.add("seven forty five a. m.");
		samples.add("ten fifty five a. m.");
		samples.add("two thirty five p. m.");
		samples.add("three twenty eight p. m.");
		samples.add("eleven p. m.");
		samples.add("nine twenty five a. m.");
		samples.add("six thirty four p. m.");
		samples.add("eight fifty a. m.");
		samples.add("five fifty one p. m.");
		samples.add("eight fifteen a. m.");
		samples.add("six forty six p. m.");
		samples.add("eleven eleven a. m.");
		samples.add("one fifteen p. m.");
		samples.add("one forty five p. m.");
		samples.add("one oh two p. m.");
		samples.add("twelve thirty four p. m.");
		samples.add("three twenty four p. m.");
		samples.add("twelve thirty two p. m.");
		samples.add("eleven seventeen a m");
		samples.add("ten fifteen a. m.");
		samples.add("eight twenty p. m.");
		samples.add("five forty nine p. m.");
		samples.add("three oh eight p. m.");
		samples.add("four thirty nine p. m.");
		samples.add("one twenty three p. m.");
		samples.add("eleven thirty p. m.");
		samples.add("eleven forty eight a. m.");
		samples.add("twelve thirty four a. m.");
		samples.add("eight oh two p. m.");
		samples.add("eight twenty three a. m.");
		samples.add("two forty nine p. m.");
		samples.add("three thirty five p. m.");
		samples.add("nine twenty a. m.");
		samples.add("ten oh two");
		
		final TimeParser parser = new TimeParser();
		
		for (final String text : samples) {
			final Time time = parser.parse(text);
			if (time == null) {
				System.out.println("FAIL :: " + text);
			} else {
				System.out.println(String.format("%02d:%02d", time.getHour(),
						time.getMinute()) + " :: " + text);
			}
		}
	}
	
	public Time parse(String text) {
		// Strip any periods
		final String stripped = text.replaceAll("\\.", "");
		
		final Matcher matcher = pattern.matcher(stripped);
		if (matcher.matches()) {
			int hour = -1;
			int minutes = 0;
			
			hour = HOUR_TO_INT.get(matcher.group("hour"));
			if (matcher.isCaptured("teens")) {
				minutes = TEENS.get(matcher.group("teens"));
			} else if (matcher.isCaptured("tens")) {
				minutes = TENS_DIGIT.get(matcher.group("tens"));
				if (matcher.isCaptured("units")) {
					minutes += UNIT_DIGIT.get(matcher.group("units"));
				}
			}
			
			if (matcher.isCaptured("ampm")) {
				// Case AM/PM tag captured
				if (AM_PM_STRINGS.get(matcher.group("ampm")) == AmPm.PM) {
					hour += 12;
				}
			} else {
				// No AM/PM tag specified
				if (AMPM_DEFAULTS.containsKey(hour)
						&& AMPM_DEFAULTS.get(hour) == AmPm.PM) {
					hour += 12;
				}
			}
			
			return new Time(hour, minutes);
		}
		
		return null;
	}
}
