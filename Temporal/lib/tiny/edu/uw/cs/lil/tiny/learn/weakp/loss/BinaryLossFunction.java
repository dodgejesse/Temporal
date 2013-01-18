package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Scaled binary loss function with a threshold.
 * 
 * @author Yoav Artzi
 */
public class BinaryLossFunction<Y> extends AbstractScaledLossFunction<Y> {
	
	private final ILossFunction<Y>	lossFunction;
	private final double			threshold;
	
	public BinaryLossFunction(double scale, ILossFunction<Y> lossFunction,
			double threshold) {
		super(scale);
		this.lossFunction = lossFunction;
		this.threshold = threshold;
	}
	
	@Override
	protected double doLossCalculation(IDataItem<Sentence> dataItem, Y label) {
		return lossFunction.calculateLoss(dataItem, label) >= threshold ? 1.0
				: 0.0;
	}
}
