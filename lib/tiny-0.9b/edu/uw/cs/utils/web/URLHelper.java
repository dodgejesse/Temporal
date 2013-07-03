package edu.uw.cs.utils.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLHelper {
	final private static Pattern	domainPattern	= Pattern
															.compile("^(?:[^/]+//)?([^/:]+)");
	
	private URLHelper() {
	}
	
	public static String domainFromURL(String url) {
		final Matcher matcher = domainPattern.matcher(url);
		if (matcher.find()) {
			return url.substring(matcher.start(1), matcher.end(1));
		} else {
			return null;
		}
		
	}
}
