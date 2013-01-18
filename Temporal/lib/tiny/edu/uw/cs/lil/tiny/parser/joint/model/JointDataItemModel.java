package edu.uw.cs.lil.tiny.parser.joint.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.parser.ccg.model.DataItemModel;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.utils.composites.Pair;

public class JointDataItemModel<X, W, Y, Z> extends DataItemModel<X, Y>
		implements IJointDataItemModel<Y, Z> {
	
	private final IDataItem<Pair<X, W>>				dataItem;
	private final IJointModelImmutable<X, W, Y, Z>	model;
	
	public JointDataItemModel(IJointModelImmutable<X, W, Y, Z> model,
			final IDataItem<Pair<X, W>> dataItem) {
		super(model, new JointDataItemWrapper<X, W>(dataItem.getSample()
				.first(), dataItem));
		this.model = model;
		this.dataItem = dataItem;
	}
	
	@Override
	public IHashVector computeFeatures(Pair<Y, Z> result) {
		return model.computeFeatures(result, dataItem);
	}
	
	@Override
	public double score(Pair<Y, Z> result) {
		return model.score(result, dataItem);
	}
}
