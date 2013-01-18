package edu.uw.cs.lil.tiny.data.sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.utils.collections.ListUtils;

/**
 * Representing a single sentence.
 * 
 * @author Yoav Artzi
 */
public class Sentence implements IDataItem<Sentence> {
	private final String		string;
	private final List<String>	tokens;
	
	public Sentence(List<String> tokens) {
		this.tokens = ListUtils.map(tokens,
				new ListUtils.Mapper<String, String>() {
					
					@Override
					public String process(String obj) {
						return obj.replace("%", "%%");
					}
				});
		this.string = ListUtils.join(this.tokens, " ");
	}
	
	public Sentence(String string) {
		this.string = string.replace("%", "%%");
		this.tokens = Collections.unmodifiableList(tokenize(this.string));
	}
	
	private static List<String> tokenize(String input) {
		final List<String> tokens = new ArrayList<String>();
		final StringTokenizer st = new StringTokenizer(input);
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken().trim());
		}
		return tokens;
	}
	
	@Override
	public boolean equals(Object obj) {
		// Tokens are not included as they created automatically and
		// deterministically from the given text
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Sentence other = (Sentence) obj;
		if (string == null) {
			if (other.string != null) {
				return false;
			}
		} else if (!string.equals(other.string)) {
			return false;
		}
		return true;
	}
	
	@Override
	public Sentence getSample() {
		return this;
	}
	
	public String getString() {
		return string;
	}
	
	public List<String> getTokens() {
		return tokens;
	}
	
	@Override
	public int hashCode() {
		// Tokens are not included as they created automatically and
		// deterministically from the given text
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return string;
	}
}
