package edu.uw.cs.lil.tiny.tempeval;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.GetPredicateCounts;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.counter.Counter;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/*
 * This is not complete!! Was in the process of changing from 
 * implements ILabeledDataItem<Pair<Sentence, String>, LogicalExpression>
 * to
 * implements ILabeledDataItem<Pair<Sentence, String>, Pair<String, LogicalExpression>>
 */
public class TemporalSentenceWithLogic implements
		ILabeledDataItem<Pair<Sentence, String>, Pair<String, LogicalExpression>> {
	private final Map<LogicalConstant, Counter> predCounts;
	private final String referenceDate;
	private final String ISO;
	private final Sentence sentence;
	private final LogicalExpression logic;

	public TemporalSentenceWithLogic(Sentence sentence, String ISO, LogicalExpression logic,
			String referenceDate) {
		this.sentence = sentence;
		this.ISO = ISO;
		this.logic = logic;
		this.referenceDate = referenceDate;

		this.predCounts = GetPredicateCounts.of(logic);

		final Iterator<Entry<LogicalConstant, Counter>> iterator = this.predCounts.entrySet().iterator();
		while (iterator.hasNext()) {
			LogicalConstant pred = iterator.next().getKey();
			if ((LogicLanguageServices.isArrayIndexPredicate(pred))
					|| (LogicLanguageServices.isArraySubPredicate(pred)))
				iterator.remove();
		}
	}

	public double calculateLoss(LogicalExpression label) {
		if (label.equals(this.logic)) {
			return 0.0D;
		}
		return 1.0D;
	}

	public Pair<String, LogicalExpression> getLabel() {
		return Pair.of(this.ISO, this.logic);
	}

	public String getRefDate() {
		return this.referenceDate;
	}

	public Pair<Sentence, String> getSample() {
		return Pair.of(this.sentence, this.referenceDate);
	}

	public boolean isCorrect(LogicalExpression label) {
		return label.equals(this.logic);
	}

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

	public double quality() {
		return 1.0D;
	}

	public String toString() {
		return this.sentence.toString() + '\n' + this.semantics;
	}

	@Override
	public double calculateLoss(Pair<String, LogicalExpression> label) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean prune(Pair<String, LogicalExpression> y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCorrect(Pair<String, LogicalExpression> label) {
		// TODO Auto-generated method stub
		return false;
	}
}
