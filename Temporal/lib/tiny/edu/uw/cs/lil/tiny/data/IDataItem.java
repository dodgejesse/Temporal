package edu.uw.cs.lil.tiny.data;

/**
 * Represents a data item.
 * 
 * @author Yoav Artzi
 * @param <X>
 *            Type of the sample.
 */
public interface IDataItem<X> {
	@Override
	public String toString();
	
	X getSample();
}
