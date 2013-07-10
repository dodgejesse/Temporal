package edu.uw.cs.lil.tiny.tempeval.readdata;

import java.util.LinkedList;
import java.util.List;

public class NewTemporalSentence {
	private String docID;
	private List<String> tokens;
	private List<Timex> timexes;

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
}
