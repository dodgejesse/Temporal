package edu.uw.cs.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A linked list with the last entity repeating endlessly. The length of the
 * list is infinity. To simplify implementation this list is unmodifiable (read
 * only). It's initialized with a list and from that point on is for reading
 * only. You can use {@link #toArray()} to get a finite array to iterate over
 * the real objects that hold the list. The length of the array will be the
 * length of the inner list, plus one. The last element in the array is the
 * string "*". Therefore, this method should be used mostly for printing.
 * 
 * @author Yoav Artzi
 * @param <E>
 */
public class ListAsterisk<E> implements List<E> {
	final private List<E>	innerList;
	
	public ListAsterisk(List<E> list) {
		if (list.isEmpty()) {
			throw new IllegalStateException(
					"Asterisk list must contain at least one item");
		}
		this.innerList = Collections.unmodifiableList(list);
	}
	
	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException("List is immutable");
		
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public boolean contains(Object o) {
		return innerList.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return innerList.containsAll(c);
	}
	
	@Override
	public E get(int index) {
		if (index >= innerList.size()) {
			return innerList.get(innerList.size() - 1);
		} else {
			return innerList.get(index);
		}
	}
	
	@Override
	public int indexOf(Object o) {
		return innerList.indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public Iterator<E> iterator() {
		return new ListAsteriskIterator<E>(innerList.iterator());
	}
	
	@Override
	public int lastIndexOf(Object o) {
		final int index = innerList.lastIndexOf(o);
		if (index == innerList.size() - 1) {
			return Integer.MAX_VALUE;
		} else {
			return index;
		}
	}
	
	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException("Not supported");
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException("Not supported");
	}
	
	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException("List is immutable");
	}
	
	/**
	 * Returns -1 to signal infinite length.
	 */
	@Override
	public int size() {
		return -1;
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		final List<E> sub = new ArrayList<E>(toIndex - fromIndex);
		for (int i = fromIndex; i < toIndex; ++i) {
			sub.add(get(i));
		}
		return sub;
	}
	
	@Override
	public Object[] toArray() {
		final Object[] array = innerList
				.toArray(new Object[innerList.size() + 1]);
		array[innerList.size()] = "*";
		return array;
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException(
				"List is infinite, so let's assume I ran out of memory");
	}
	
	private static class ListAsteriskIterator<E> implements Iterator<E> {
		
		private final Iterator<E>	innerIterator;
		private E					lastReturned;
		
		public ListAsteriskIterator(Iterator<E> innerIterator) {
			this.innerIterator = innerIterator;
		}
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public E next() {
			if (innerIterator.hasNext()) {
				lastReturned = next();
			}
			return lastReturned;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("List is immutable");
		}
		
	}
}
