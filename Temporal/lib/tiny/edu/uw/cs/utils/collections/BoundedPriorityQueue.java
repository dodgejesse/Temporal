package edu.uw.cs.utils.collections;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * {@link PriorityQueue} with bounded capacity.
 * 
 * @author Yoav Artzi
 * @param <E>
 */
public class BoundedPriorityQueue<E> extends PriorityQueue<E> {
	
	private static final long			serialVersionUID	= -4941683766888454400L;
	private final int					capacity;
	private final Comparator<? super E>	comparator;
	
	public BoundedPriorityQueue(int capacity, Comparator<? super E> comparator) {
		super(capacity, comparator);
		this.capacity = capacity;
		this.comparator = comparator;
	}
	
	@Override
	public boolean add(E e) {
		if (size() < capacity) {
			return super.add(e);
		} else {
			throw new IllegalStateException("Queue is full");
		}
	}
	
	public void addAll(Iterable<E> iterable) {
		for (final E e : iterable) {
			add(e);
		}
	}
	
	@Override
	public boolean offer(E e) {
		if (size() < capacity) {
			super.offer(e);
			return true;
		} else {
			// Case queue is full
			if (comparator.compare(e, peek()) > 0) {
				poll();
				super.offer(e);
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	public void offerAll(Iterable<E> iterable) {
		for (final E e : iterable) {
			offer(e);
		}
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
}
