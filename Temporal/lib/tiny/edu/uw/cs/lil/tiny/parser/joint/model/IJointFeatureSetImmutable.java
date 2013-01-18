package edu.uw.cs.lil.tiny.parser.joint.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.model.IFeatureSet;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public interface IJointFeatureSetImmutable<X, W, Y, Z> extends IFeatureSet {
	
	double score(Pair<Y, Z> result, IHashVector theta,
			IDataItem<Pair<X, W>> dataItem);
	
	void setFeats(Pair<Y, Z> result, IHashVector feats,
			IDataItem<Pair<X, W>> dataItem);
	
}
