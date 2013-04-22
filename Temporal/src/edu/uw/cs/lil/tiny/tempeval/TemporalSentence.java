package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;


/*
 * Author: Jesse Dodge
 * 
 * Represents a single mention, AKA a set of docID, sentence, phrase, refDate, type, and val.
 * 
 */

public class TemporalSentence implements
		ILabeledDataItem<Pair<Sentence, String[]>, Pair<String, String>>{
	/**
	 * 
	 */
	private final String docID;
	private final String sentence;
	private final String charNum;
	private final Sentence phrase;
	private final String refDate;
	private final String type;
	private final String val;
	private final String prevDocID;
	private final String dependencyParse;

	
	// This previous isn't actually being used! Could take it out. Will need to change temporalSentenceDataset.
	public TemporalSentence(String d, String s, String c, Sentence p, String r, String t, String v, TemporalSentence prev, String dp) {
		docID = d;
		sentence = s;
		charNum = c;
		phrase = p;
		refDate = r;
		type = t;
		val = v;
		if (prev != null)
			prevDocID = prev.getSample().second()[0];
		else
			prevDocID = "";
		dependencyParse = dp;
	}
	
	public TemporalSentence( Sentence p, String d, String s, String c, String r, String t, String v, String prev, String dp){
		docID = d;
		sentence = s;
		charNum = c;
		phrase = p;
		refDate = r;
		type = t;
		val = v;
		prevDocID = prev;
		dependencyParse = dp;
	}

	public Pair<String, String> getLabel() {
		return Pair.of(type, val);
	}

	public String getRefDate() {
		return this.refDate;
	}
	
	public String getSentence(){
		return sentence;
	}
	
	public String getType(){
		return type;
	}
	
	public String getVal(){
		return val;
	}

	public Pair<Sentence, String[]> getSample() {
		String[] s = {docID, sentence, refDate, prevDocID, charNum, dependencyParse};
		return Pair.of(phrase, s);
	}

	/*
	public boolean prune(LogicalExpression y) {
		final Map<LogicalConstant, Counter> currentPredCounts = GetPredicateCounts
				.of(y);
		for (final Map.Entry<LogicalConstant, Counter> entry : predCounts
				.entrySet()) {
			if (currentPredCounts.containsKey(entry.getKey())
					&& currentPredCounts.get(entry.getKey()).value() > entry
							.getValue().value()) {
				// Prune because of too many predicates
				return true;
			}
		}
		return false;
	}
	*/

	public double quality() {
		return 1.0D;
	}

	public String toString() {
		return docID + "\n" + sentence + "\n" + charNum + "\n" + phrase.toString() + "\n" + refDate + "\n" + type + "\n" + val;
	}

	@Override
	public boolean isCorrect(Pair<String, String> l) {
		return l.first().equals(type) && l.second().equals(val);
	}

	@Override
	public double calculateLoss(Pair<String, String> label) {
		return 0;
	}

	@Override
	public boolean prune(Pair<String, String> y) {
		throw new IllegalArgumentException("Cannot prune a TemporalSentence becuase it doesn't contain logic.");
	}
}
