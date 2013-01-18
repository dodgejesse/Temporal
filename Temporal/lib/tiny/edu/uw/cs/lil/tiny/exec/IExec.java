package edu.uw.cs.lil.tiny.exec;

import edu.uw.cs.lil.tiny.data.IDataItem;

public interface IExec<X, Z> {
	
	IExecOutput<Z> execute(IDataItem<X> dataItem);
	
	/**
	 * @param dataItem
	 * @param model
	 * @param sloppy
	 *            Allow sloppy interpretation of the input (e.g., skip words in
	 *            the input sentence)
	 * @return
	 */
	IExecOutput<Z> execute(IDataItem<X> dataItem, boolean sloppy);
	
}
