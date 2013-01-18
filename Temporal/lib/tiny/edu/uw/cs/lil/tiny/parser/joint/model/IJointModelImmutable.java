package edu.uw.cs.lil.tiny.parser.joint.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public interface IJointModelImmutable<X, W, Y, Z> extends IModelImmutable<X, Y> {
	
	/**
	 * Compute feature over execution and logical form.
	 * 
	 * @param result
	 * @param dataItem
	 * @return
	 */
	IHashVector computeFeatures(Pair<Y, Z> result,
			IDataItem<Pair<X, W>> dataItem);
	
	IJointDataItemModel<Y, Z> createJointDataItemModel(
			IDataItem<Pair<X, W>> dataItem);
	
	/**
	 * Score execution and logical form pair.
	 * 
	 * @param result
	 * @param dataItem
	 * @return
	 */
	double score(Pair<Y, Z> result, IDataItem<Pair<X, W>> dataItem);
}
