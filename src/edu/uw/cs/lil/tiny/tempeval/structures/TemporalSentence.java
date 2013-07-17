package edu.uw.cs.lil.tiny.tempeval.structures;

import java.util.LinkedList;
import java.util.List;

public class TemporalSentence implements java.io.Serializable{
	private static final long serialVersionUID = 2013931525176952047L;
	private String docID;
	private String referenceTime;
	private LinkedList<String> tokens;
	private LinkedList<TemporalMention> mentions;
	private String dp; //dependency parse

	public TemporalSentence(String docID, String referenceTime) {
		this.docID = docID;
		this.referenceTime = referenceTime;
		this.tokens = new LinkedList<String>();
		this.mentions = new LinkedList<TemporalMention>();
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void insertToken(String token) {
		tokens.add(token);
	}

	public int getNumTokens() {
		return tokens.size();
	}

	public void insertMention(TemporalMention t) {
		mentions.add(t);
	}

	public List<TemporalMention> getMentions() {
		return mentions;
	}
	
	public String getDocID() {
		return docID;
	}

	public String getDependencyParse() {
		return dp;
	}

	public void saveDependencyParse(String dp) {
		this.dp = dp;
	}
	
	public String getReferenceTime() {
		return referenceTime;
	}

	public String toString() {
		String s = "";
		int counter = 0;
		for (String t : tokens) {
			s += "[" + counter + "]" + t + " ";
			counter++;
		}
		s += "( ";
		for (TemporalMention t : mentions)
			s += t + " ";
		s += ")";
		return s;
	}

	public String prettyString(int start, int end) {
		String output = "";
		for (String s : tokens.subList(start, end)) {
			if (output.length() > 0)
				output += " ";
			output += s;
		}
		return output;
	}
	
	public String prettyString() {
		return prettyString(0, tokens.size());
	}
	
	public TemporalMention getPossibleMention(int startToken, int endToken) {
		return new TemporalMention(this, startToken, endToken);
	}
}
