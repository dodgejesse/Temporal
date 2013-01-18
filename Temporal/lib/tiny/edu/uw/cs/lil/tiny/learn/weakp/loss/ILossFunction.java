package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Loss function for sentences and labels of type Y.
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Type of label
 */
public interface ILossFunction<Y> {
	double calculateLoss(IDataItem<Sentence> dataItem, Y label);
}
