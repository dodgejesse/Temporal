package edu.uw.cs.lil.tiny.parser;

import edu.uw.cs.lil.tiny.data.ILossDataItem;

/**
 * Given a {@link ILossDataItem} wraps its loss and pruning abilities to
 * estimate candidate meaning representations during parsing. This object is
 * instantiated for a single data item and must be passed to the parser for
 * evaluating candidates meaning representations.
 * 
 * @author Yoav Artzi
 */
public class Pruner<X, Y> {
	
	private final ILossDataItem<X, Y>	dataItem;
	
	private Pruner(ILossDataItem<X, Y> dataItem) {
		this.dataItem = dataItem;
	}
	
	public static <X, Y> Pruner<X, Y> create(ILossDataItem<X, Y> dataItem) {
		return new Pruner<X, Y>(dataItem);
	}
	
	public double loss(Y y) {
		return dataItem.calculateLoss(y);
	}
	
	public boolean prune(Y y) {
		return dataItem.prune(y);
	}
}
