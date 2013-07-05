package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.utils.composites.Pair;


/*
 * Author: Jesse Dodge
 * 
 * Represents a single mention, AKA a set of docID, sentence, phrase, refDate, type, and val.
 * 
 */

public class TemporalSentence implements
		ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>{
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

	public TemporalResult getLabel() {
		return new TemporalResult(null, type, val, null, null, null);
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
		String s = "Phrase:            " + phrase.toString() + "\n";
		s += "Sentence:          " + sentence + "\n";
		s += "ref_time:          " + refDate + "\n";
		s += "Gold type:         " + type + "\n";
		s += "gold val:          " + val;
		//out.println("Lexical Entries:   " + lexicalEntries);
		//out.println("Logic:             " + label);
		//out.println("Average max feats: " + theta.printValues(averageMaxFeatureVector));
		//out.println("Guess type:        " + guessType);
		//out.println("Guess val:         " + guessVal);
		//out.println("Correct type?      " + (correct == 0 || correct == 1));
		//out.println("Correct val?       " + (correct == 0 || correct == 2));
		//out.println("Correct logics:    " + correctLogicalForms);
		return s;
	}

	@Override
	public boolean isCorrect(TemporalResult other) {
		return other.type.equals(type) && other.val.equals(val);
	}

	@Override
	public double calculateLoss(TemporalResult label) {
		return 0;
	}

	@Override
	public boolean prune(TemporalResult y) {
		throw new IllegalArgumentException("Cannot prune a TemporalSentence becuase it doesn't contain logic.");
	}
}