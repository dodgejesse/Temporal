package edu.uw.cs.lil.tiny.data.utils;

import edu.uw.cs.lil.tiny.data.IDataItem;

public interface IValidator<X, Z> {
	
	boolean isValid(IDataItem<X> dataItem, Z label);
	
}
