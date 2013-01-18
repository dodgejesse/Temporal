package edu.uw.cs.lil.tiny.parser.joint;

import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;

public interface IJointOutputLogger<Y, Z> {
	void log(IJointOutput<Y, Z> output, IJointDataItemModel<Y, Z> dataItemModel);
}
