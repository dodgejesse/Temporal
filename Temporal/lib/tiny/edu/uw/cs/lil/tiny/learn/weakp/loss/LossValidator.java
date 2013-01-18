package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Validator based on a loss function and threshold.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class LossValidator<Y> implements IValidator<Y> {
	
	private final ILossFunction<Y>	lossFunction;
	private final double			threshold;
	
	public LossValidator(ILossFunction<Y> lossFunction, double threshold) {
		this.lossFunction = lossFunction;
		this.threshold = threshold;
	}
	
	@Override
	public boolean isValid(IDataItem<Sentence> dataItem, Y label) {
		return lossFunction.calculateLoss(dataItem, label) <= threshold;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(LossValidator.class.getName())
				.append(", lossFunction=").append(lossFunction)
				.append(", threshold=").append(threshold).toString();
	}
}
