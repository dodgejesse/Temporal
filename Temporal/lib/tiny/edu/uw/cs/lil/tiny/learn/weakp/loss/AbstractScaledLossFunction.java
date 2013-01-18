package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Loss function with a inner scale value.
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Type of label
 */
abstract public class AbstractScaledLossFunction<Y> implements ILossFunction<Y> {
	private final double	scale;
	
	public AbstractScaledLossFunction(double scale) {
		this.scale = scale;
	}
	
	final public double calculateLoss(IDataItem<Sentence> dataItem, Y label) {
		return scale * doLossCalculation(dataItem, label);
	}
	
	/**
	 * The actual loss calculation.
	 * 
	 * @param dataItem
	 * @param label
	 * @return
	 */
	protected abstract double doLossCalculation(IDataItem<Sentence> dataItem,
			Y label);
}