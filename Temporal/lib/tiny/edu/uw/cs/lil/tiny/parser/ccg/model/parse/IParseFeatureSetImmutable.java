package edu.uw.cs.lil.tiny.parser.ccg.model.parse;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.IParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.model.IFeatureSet;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

public interface IParseFeatureSetImmutable<X, Y> extends IFeatureSet {
	
	double score(IParseStep<Y> obj, IHashVector theta, IDataItem<X> dataItem);
	
	void setFeats(IParseStep<Y> obj, IHashVector feats, IDataItem<X> dataItem);
}
