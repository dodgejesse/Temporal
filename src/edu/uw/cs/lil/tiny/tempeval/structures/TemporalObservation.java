package edu.uw.cs.lil.tiny.tempeval.structures;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;


/*
 * Author: Jesse Dodge
 * 
 * Represents a single mention, AKA a set of docID, sentence, phrase, refDate, type, and val.
 * 
 */

public class TemporalObservation implements ILabeledDataItem<Pair<Sentence, String[]>, TemporalResult>{
	
	private final String sentence;
	private final Sentence phrase;
	private final String refDate;
	private final String type;
	private final String val;
	private final String dependencyParse;
	
	public TemporalObservation(Sentence p, String s, String r, String t, String v, String dp){
		phrase = p;
		sentence = s;
		refDate = r;
		type = t;
		val = v;
		dependencyParse = dp;
	}
	
	public TemporalObservation() {
		this(null, null, null, null, null, null);
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
		String[] s = {sentence, refDate, dependencyParse};
		return Pair.of(phrase, s);
	}

	public double quality() {
		return 1.0D;
	}

	public String toString() {
		String s = "phrase:            " + phrase.toString() + "\n";
		s += "sentence:          " + sentence + "\n";
		s += "ref_time:          " + refDate + "\n";
		s += "gold type:         " + type + "\n";
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
