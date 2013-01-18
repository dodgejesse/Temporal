package edu.uw.cs.lil.tiny.data;

/**
 * Represents a labeled data item.
 * 
 * @author Yoav Artzi
 * @param <X>
 * @param <Z>
 * @see IDataItem
 */
public interface ILabeledDataItem<X, Z> extends ILossDataItem<X, Z> {
	
	Z getLabel();
	
	/**
	 * Compares a label to the gold standard if such exist.
	 * 
	 * @param label
	 * @return null if not gold standard exists.
	 */
	boolean isCorrect(Z label);
}
