package edu.uw.cs.lil.tiny.learn.weakp.loss;

import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Loss function composed of a series of loss functions.
 * 
 * @author Yoav Artzi
 * @param <Y>
 */
public class CompositeLossFunction<Y> extends AbstractScaledLossFunction<Y> {
	
	private final List<ILossFunction<Y>>	lossFunctions;
	
	public CompositeLossFunction(double scale,
			List<ILossFunction<Y>> lossFunctions) {
		super(scale);
		this.lossFunctions = lossFunctions;
	}
	
	public CompositeLossFunction(List<ILossFunction<Y>> lossFunctions) {
		this(1.0, lossFunctions);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(CompositeLossFunction.class.getName())
				.append(" :: ").append(lossFunctions).toString();
	}
	
	@Override
	protected double doLossCalculation(IDataItem<Sentence> dataItem, Y label) {
		double loss = 0.0;
		for (final ILossFunction<Y> lossFunction : lossFunctions) {
			loss += lossFunction.calculateLoss(dataItem, label);
		}
		return loss;
	}
	
}
