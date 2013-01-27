package edu.uw.cs.utils.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Iterate over a few separate collections.
 * 
 * @author Yoav Artzi
 * @param <E>
 */
public class CompositeIterator<E> implements Iterator<E> {
	
	private final List<Iterator<? extends E>>	iterators;
	
	public CompositeIterator(Iterator<? extends E>... iterators) {
		this(Arrays.asList(iterators));
	}
	
	public CompositeIterator(List<Iterator<? extends E>> iterators) {
		this.iterators = iterators;
	}
	
	@Override
	public boolean hasNext() {
		while (!iterators.isEmpty()) {
			if (iterators.get(0).hasNext()) {
				return true;
			} else {
				iterators.remove(0);
			}
		}
		return false;
	}
	
	@Override
	public E next() {
		if (hasNext()) {
			return iterators.get(0).next();
		} else {
			return null;
		}
	}
	
	@Override
	public void remove() {
		if (!iterators.isEmpty()) {
			iterators.get(0).remove();
		}
	}
}
