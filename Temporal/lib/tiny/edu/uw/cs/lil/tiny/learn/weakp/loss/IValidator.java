package edu.uw.cs.lil.tiny.learn.weakp.loss;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;

/**
 * Validates a label
 * 
 * @author Yoav Artzi
 * @param <Y>
 *            Type of label
 */
public interface IValidator<Y> {
	boolean isValid(IDataItem<Sentence> dataItem, Y label);
}
