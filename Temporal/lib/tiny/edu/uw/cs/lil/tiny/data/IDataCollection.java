package edu.uw.cs.lil.tiny.data;

/**
 * A iterable data collection with a certain number of data items.
 * 
 * @author Yoav Artzi
 * @param <T>
 */
public interface IDataCollection<T> extends Iterable<T> {
	
	/**
	 * Size of the collection.
	 * 
	 * @return
	 */
	int size();
}
