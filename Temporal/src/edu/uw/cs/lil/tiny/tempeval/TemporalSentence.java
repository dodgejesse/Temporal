package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalSentence implements
		ILabeledDataItem<Pair<Sentence, String>, String> {
	//private final Map<LogicalConstant, Counter> predCounts;
	private final String referenceDate;
	private final String ISO;
	private final Sentence sentence;

	public TemporalSentence(Sentence sentence, String referenceDate,
			String ISO) {
		this.sentence = sentence;
		this.ISO = ISO;
		this.referenceDate = referenceDate;

		//this.predCounts = GetPredicateCounts.of(semantics);

		//final Iterator<Entry<LogicalConstant, Counter>> iterator = this.predCounts.entrySet().iterator();
		//while (iterator.hasNext()) {
		//	LogicalConstant pred = iterator.next().getKey();
		//	if ((LogicLanguageServices.isArrayIndexPredicate(pred))
		//			|| (LogicLanguageServices.isArraySubPredicate(pred)))
		//		iterator.remove();
		//}
	}

	public String getLabel() {
		return this.ISO;
	}

	public String getRefDate() {
		return this.referenceDate;
	}

	public Pair<Sentence, String> getSample() {
		return Pair.of(this.sentence, this.referenceDate);
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
		return this.sentence.toString() + '\n' + this.ISO;
	}

	@Override
	public double calculateLoss(String label) {
		if (label.equals(this.ISO))
			return 0.0;
		else
			return 0;
	}

	@Override
	public boolean prune(String y) {
		throw new IllegalArgumentException("Cannot prune a TemporalSentence becuase it doesn't contain logic.");
	}

	@Override
	public boolean isCorrect(String label) {
		return label.equals(ISO);
	}
}
