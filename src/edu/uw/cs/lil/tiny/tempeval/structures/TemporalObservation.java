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
	private final String referenceTime;
	private final String type;
	private final String val;
	private final String dependencyParse;
	private final int tokenIndex;
	private final String docID;
	
	public TemporalObservation(Sentence phrase, String sentence, String referenceTime, String type, String val, String dependencyParse, int tokenIndex, String docID){
		this.phrase = phrase;
		this.sentence = sentence;
		this.referenceTime = referenceTime;
		this.type = type;
		this.val = val;
		this.dependencyParse = dependencyParse;
		this.docID = docID;
		this.tokenIndex = tokenIndex;
	}

	public TemporalResult getLabel() {
		return new TemporalResult(null, type, val, null, null, null);
	}

	public String getReferenceTime() {
		return this.referenceTime;
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
		String[] s = {sentence, referenceTime, dependencyParse, "" + tokenIndex, docID};
		return Pair.of(phrase, s);
	}

	public double quality() {
		return 1.0D;
	}

	public String toString() {
		String s = "phrase:            " + phrase.toString() + "\n";
		s += "sentence:          " + sentence + "\n";
		s += "refTime:          " + referenceTime + "\n";
		s += "gold type:         " + type + "\n";
		s += "gold val:          " + val;
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
