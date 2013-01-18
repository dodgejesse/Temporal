package edu.uw.cs.lil.tiny.learn.weakp.loss;

import java.util.List;

import edu.uw.cs.lil.tiny.weakp.loss.parser.IScoreFunction;

/**
 * Supports only score function that don't require the original data item.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class SimpleScoringFunction<Y> implements IScoreFunction<Y> {
	
	private final List<ILossFunction<Y>>	lossFunctions;
	
	public SimpleScoringFunction(List<ILossFunction<Y>> lossFunctions) {
		this.lossFunctions = lossFunctions;
	}
	
	@Override
	public double score(Y label) {
		double loss = 0.0;
		for (final ILossFunction<Y> lossFunction : lossFunctions) {
			loss += lossFunction.calculateLoss(null, label);
		}
		return -loss;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(SimpleScoringFunction.class.getName())
				.append(" :: ").append(lossFunctions).toString();
	}
	
}
