package edu.uw.cs.lil.tiny.parser.joint.model;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.utils.composites.Pair;

public class JointDataItemWrapper<X, W> implements IDataItem<X> {
	
	private final IDataItem<Pair<X, W>>	baseDataItem;
	private final X						sample;
	
	public JointDataItemWrapper(X sample, IDataItem<Pair<X, W>> baseDataItem) {
		this.sample = sample;
		this.baseDataItem = baseDataItem;
	}
	
	public IDataItem<Pair<X, W>> getBaseDataItem() {
		return baseDataItem;
	}
	
	@Override
	public X getSample() {
		return sample;
	}
	
}
