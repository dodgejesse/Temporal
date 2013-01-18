package edu.uw.cs.lil.tiny.parser.joint.model;

import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public interface IJointDataItemModel<Y, Z> extends IDataItemModel<Y> {
	
	IHashVector computeFeatures(Pair<Y, Z> result);
	
	double score(Pair<Y, Z> result);
	
}
