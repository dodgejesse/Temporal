package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

/*
 * Author: Jesse Dodge
 * 
 * Represents a single mention, AKA a set of docID, sentence, phrase, refDate, type, and val.
 */

public class TemporalSentence implements
		ILabeledDataItem<Pair<String[], Sentence>, Pair<String, String>> {
	private final String docID;
	private final String sentence;
	private final Sentence phrase;
	private final String refDate;
	private final String type;
	private final String val;

	public TemporalSentence(String d, String s, Sentence p, String r, String t, String v) {
		docID = d;
		sentence = s;
		phrase = p;
		refDate = r;
		type = t;
		val = v;
		//this.predCounts = GetPredicateCounts.of(semantics);

		//final Iterator<Entry<LogicalConstant, Counter>> iterator = this.predCounts.entrySet().iterator();
		//while (iterator.hasNext()) {
		//	LogicalConstant pred = iterator.next().getKey();
		//	if ((LogicLanguageServices.isArrayIndexPredicate(pred))
		//			|| (LogicLanguageServices.isArraySubPredicate(pred)))
		//		iterator.remove();
		//}
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

	public Pair<String[], Sentence> getSample() {
		String[] s = {docID, sentence, refDate};
		return Pair.of(s, phrase);
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
		return docID + "\n" + sentence + "\n" + phrase.toString() + "\n" + refDate + "\n" + type + "\n" + val;
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
