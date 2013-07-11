package edu.uw.cs.lil.tiny.tempeval.structures;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalSentence implements java.io.Serializable{
	private static final long serialVersionUID = 2013931525176952047L;
	private String docID;
	private String referenceTime;
	private LinkedList<String> tokens;
	private LinkedList<Timex> timexes;
	private String dp; //dependency parse

	public TemporalSentence(String docID, String referenceTime) {
		this.docID = docID;
		this.referenceTime = referenceTime;
		this.tokens = new LinkedList<String>();
		this.timexes = new LinkedList<Timex>();
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

	public void insertTimex(Timex t) {
		timexes.add(t);
	}

	public List<Timex> getTimexes() {
		return timexes;
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
	
	public List<TemporalObservation> getObservations(List<Timex> timexSubset) {
		List<TemporalObservation> observations = new LinkedList<TemporalObservation>();
		for(Timex t : timexSubset) {
			observations.add(new TemporalObservation(new Sentence(tokens.subList(t.getStartToken(), t.getEndToken())), prettyString(), referenceTime, t.getType(), t.getValue(), dp, t.getStartToken(), observations.isEmpty()));
		}
		return observations;
	}
	
	public List<TemporalObservation> getObservations() {
		return getObservations(timexes);
	}
}
