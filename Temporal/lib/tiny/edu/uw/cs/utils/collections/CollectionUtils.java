package edu.uw.cs.utils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uw.cs.utils.filter.IFilter;

public class CollectionUtils {
	
	public static <T> Iterator<List<T>> cartesianIterator(
			List<? extends Iterable<? extends T>> collections) {
		return new CartesianIterator<T>(collections);
	}
	
	public static <T> Iterator<List<T>> cartesianIteratorArgs(
			Iterable<? extends T>... collections) {
		return new CartesianIterator<T>(Arrays.asList(collections));
	}
	
	public static <T> Iterable<List<T>> cartesianProduct(
			final List<? extends Iterable<? extends T>> collections) {
		return new Iterable<List<T>>() {
			@Override
			public Iterator<List<T>> iterator() {
				return cartesianIterator(collections);
			}
			
		};
	}
	
	public static <T> Iterable<List<T>> cartesianProductArgs(
			final Iterable<? extends T>... collections) {
		return cartesianProduct(Arrays.asList(collections));
	}
	
	public static <C extends Iterable<? extends E>, E> void filterInPlace(C c,
			IFilter<E> filter) {
		final Iterator<? extends E> iterator = c.iterator();
		while (iterator.hasNext()) {
			if (!filter.isValid(iterator.next())) {
				iterator.remove();
			}
		}
	}
	
	public static <E> Set<E> singletonSetOf(E obj) {
		final HashSet<E> set = new HashSet<E>();
		set.add(obj);
		return set;
	}
	
	public static <T extends Comparable<? super T>> List<T> sorted(
			Collection<T> c) {
		final ArrayList<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list;
	}
	
	public static <T> List<T> sorted(Collection<T> c,
			Comparator<? super T> comparator) {
		final ArrayList<T> list = new ArrayList<T>(c);
		Collections.sort(list, comparator);
		return list;
	}
	
	private static class CartesianIterator<T> implements Iterator<List<T>> {
		
		private final List<? extends Iterable<? extends T>>	collections;
		private boolean										done	= false;
		private boolean										first	= true;
		private final List<Iterator<? extends T>>			iterators;
		private final List<T>								recents;
		
		public CartesianIterator(
				List<? extends Iterable<? extends T>> collections) {
			this.collections = collections;
			this.iterators = new ArrayList<Iterator<? extends T>>(
					collections.size());
			this.recents = new ArrayList<T>(collections.size());
			for (final Iterable<? extends T> c : this.collections) {
				final Iterator<? extends T> iterator = c.iterator();
				if (!iterator.hasNext()) {
					// Case one of the inner iterators doesn't have a single
					// item, mark the iterator as complete
					this.done = true;
				}
				iterators.add(iterator);
				recents.add(null);
			}
		}
		
		@Override
		public boolean hasNext() {
			if (done) {
				return false;
			}
			
			for (final Iterator<? extends T> iterator : iterators) {
				if (iterator.hasNext()) {
					return true;
				}
			}
			done = true;
			return false;
		}
		
		@Override
		public List<T> next() {
			if (updateRecents()) {
				return new ArrayList<T>(recents);
			} else {
				return null;
			}
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}
		
		private boolean updateRecents() {
			return updateRecents(iterators.size() - 1);
		}
		
		private boolean updateRecents(int index) {
			if (index < 0) {
				return false;
			} else if (first) {
				recents.set(index, iterators.get(index).next());
				updateRecents(index - 1);
				first = false;
				return true;
			} else if (iterators.get(index).hasNext()) {
				recents.set(index, iterators.get(index).next());
				return true;
			} else {
				if (updateRecents(index - 1)) {
					iterators.set(index, collections.get(index).iterator());
					recents.set(index, iterators.get(index).next());
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
}
