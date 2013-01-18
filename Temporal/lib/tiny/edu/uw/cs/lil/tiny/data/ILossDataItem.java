package edu.uw.cs.lil.tiny.data;

/**
 * Represents a data item that can given loss and pruning information.
 * 
 * @author Yoav Artzi
 * @param <X>
 *            Type of the sample.
 * @param <Z>
 *            Type of the label.
 */
public interface ILossDataItem<X, Z> extends IDataItem<X> {
	
	/**
	 * Scores a label.
	 * 
	 * @param label
	 * @return
	 */
	double calculateLoss(Z label);
	
	/**
	 * Indicates if to prune a proposed label or not.
	 * 
	 * @param y
	 * @return true if to prune the proposed label
	 */
	boolean prune(Z y);
	
	/**
	 * A normalized (0 - 1.0) quality measure. The higher the better.
	 * 
	 * @return
	 */
	double quality();
}
