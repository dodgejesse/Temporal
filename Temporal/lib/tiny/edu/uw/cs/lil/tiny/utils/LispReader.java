/**
 * [lsz] This is one big hackish mess because I hate to write file processing
 * code!
 */
package edu.uw.cs.lil.tiny.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class LispReader {
	private final Reader	in;
	private char			lastc;
	private int				lasti;
	
	public LispReader(Reader i) {
		in = i;
		lastc = ' ';
		lasti = 0;
		skipPast('(');
		skipWS();
	}
	
	/**
	 * Testing code
	 */
	public static void main(String[] args) {
		final String s = "(a\nb)";
		final LispReader r = new LispReader(new StringReader(s));
		while (r.hasNext()) {
			System.out.println(r.next());
		}
	}
	
	public boolean hasNext() {
		return lasti != -1;
	}
	
	public String next() {
		if (lastc == '(') {
			return readList();
		}
		return readWord();
	}
	
	private String readList() {
		String result = "(";
		int depth = 1;
		try {
			while (depth != 0 && lasti != -1) {
				lasti = in.read();
				lastc = (char) lasti;
				if (lastc == '(') {
					depth++;
				}
				if (lastc == ')') {
					depth--;
				}
				result += lastc;
			}
			lasti = in.read();
			lastc = (char) lasti;
		} catch (final IOException e) {
			System.out.println(e);
		};
		skipWS();
		return result;
	}
	
	private String readWord() {
		String result = "";
		try {
			while (!Character.isWhitespace(lastc) && lastc != ')'
					&& lasti != -1) {
				result += lastc;
				lasti = in.read();
				lastc = (char) lasti;
			}
			lasti = in.read();
			lastc = (char) lasti;
		} catch (final IOException e) {
			System.out.println(e);
		};
		skipWS();
		return result;
	}
	
	private void skipPast(char seek) {
		try {
			while (lastc != seek && lasti != -1) {
				lasti = in.read();
				lastc = (char) lasti;
			}
			lasti = in.read();
			lastc = (char) lasti;
		} catch (final IOException e) {
			System.out.println(e);
		};
	}
	
	private void skipWS() {
		try {
			while (Character.isWhitespace(lastc) || lastc == ')') {
				lasti = in.read();
				lastc = (char) lasti;
			}
		} catch (final IOException e) {
			System.out.println(e);
		};
	}
	
}
