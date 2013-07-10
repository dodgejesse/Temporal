package edu.uw.cs.lil.tiny.tempeval.readdata;

import java.util.LinkedList;
import java.util.List;

public class NewTemporalSentence implements java.io.Serializable{
	private static final long serialVersionUID = 2013931525176952047L;
	private String docID;
	private LinkedList<String> tokens;
	private LinkedList<Timex> timexes;
	private String dp; //dependency parse
	
	public NewTemporalSentence(String docID) {
		this.docID = docID;
		this.tokens = new LinkedList<String>();
		this.timexes = new LinkedList<Timex>();
	}
	
	public void insertToken(String token) {
		tokens.add(token);
	}
	
	public void insertTimex(Timex t) {
		timexes.add(t);
	}
	
	public int getNumTokens() {
		return tokens.size();
	}

	public String getDocID() {
		return docID;
	}

	public String toString() {
		String s = "";
		int counter = 0;
		for (String t : tokens) {
			s += "[" + counter + "]" + t + " ";
			counter++;
		}
		s += "( ";
		for (Timex t : timexes)
			s += t + " ";
		s += ")";
		return s;
	}
	
	public String getDependencyParse() {
		return dp;
	}

	public void saveDependencyParse(String dp) {
		this.dp = dp;
	}
}
