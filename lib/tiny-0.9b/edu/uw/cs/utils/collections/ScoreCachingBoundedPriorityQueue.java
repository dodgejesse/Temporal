package edu.uw.cs.utils.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.uw.cs.utils.composites.Pair;

/**
 * {@link PriorityQueue} with bounded capacity.
 * 
 * @author Yoav Artzi
 * @param <E>
 */
public class ScoreCachingBoundedPriorityQueue<E> implements Queue<E> {
	
	private final BoundedPriorityQueue<Pair<Double, E>>	innerQeueu;
	private final IScorer<? super E>					scorer;
	
	public ScoreCachingBoundedPriorityQueue(int capacity,
			IScorer<? super E> scorer) {
		this.scorer = scorer;
		this.innerQeueu = new BoundedPriorityQueue<Pair<Double, E>>(capacity,
				new Comparator<Pair<Double, E>>() {
					@Override
					public int compare(Pair<Double, E> o1, Pair<Double, E> o2) {
						return Double.compare(o1.first(), o2.first());
					}
				});
	}
	
	@Override
	public boolean add(E e) {
		return innerQeueu.add(toPair(e));
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean ret = false;
		for (final E e : c) {
			ret |= add(e);
		}
		return ret;
	}
	
	public void addAll(Iterable<E> iterable) {
		for (final E e : iterable) {
			add(e);
		}
	};
	
	@Override
	public void clear() {
		innerQeueu.clear();
	}
	
	@Override
	public boolean contains(Object o) {
		return innerQeueu.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E element() {
		return innerQeueu.element().second();
	}
	
	@Override
	public boolean isEmpty() {
		return innerQeueu.isEmpty();
	}
	
	@Override
	public Iterator<E> iterator() {
		final Iterator<Pair<Double, E>> iterator = innerQeueu.iterator();
		return new Iterator<E>() {
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			
			@Override
			public E next() {
				return iterator.next().second();
			}
			
			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
	
	@Override
	public boolean offer(E e) {
		return innerQeueu.offer(toPair(e));
	}
	
	public void offerAll(Iterable<E> iterable) {
		for (final E e : iterable) {
			offer(e);
		}
	}
	
	@Override
	public E peek() {
		return innerQeueu.peek().second();
	}
	
	@Override
	public E poll() {
		return innerQeueu.poll().second();
	}
	
	@Override
	public E remove() {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		try {
			return innerQeueu.remove(toPair((E) o));
		} catch (final ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size() {
		return innerQeueu.size();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray() {
		final Object[] innerArray = innerQeueu.toArray();
		for (int i = 0; i < size(); ++i) {
			innerArray[i] = ((Pair<Double, E>) innerArray[i]).second();
		}
		return innerArray;
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		return innerQeueu.toString();
	}
	
	private Pair<Double, E> toPair(E e) {
		return Pair.of(scorer.score(e), e);
	};
}
