package edu.uw.cs.utils.collections;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * {@link PriorityQueue} with bounded capacity, which allows direct access to
 * its members.
 * 
 * @author Yoav Artzi
 * @param <E>
 */
public class DirectAccessBoundedPriorityQueue<E> extends PriorityQueue<E> {
	private static final ILogger		LOG					= LoggerFactory
																	.create(DirectAccessBoundedPriorityQueue.class);
	
	private static final long			serialVersionUID	= -4941683766888454400L;
	private final int					capacity;
	private final Comparator<? super E>	comparator;
	private final Map<E, E>				map					= new HashMap<E, E>();
	
	public DirectAccessBoundedPriorityQueue(int capacity,
			Comparator<? super E> comparator) {
		super(capacity, comparator);
		this.capacity = capacity;
		this.comparator = comparator;
	}
	
	@Override
	public boolean add(E e) {
		if (size() < capacity) {
			map.put(e, e);
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
	public boolean contains(Object o) {
		return map.containsKey(o);
	}
	
	public E get(Object o) {
		return map.get(o);
	}
	
	@Override
	public boolean offer(E e) {
		if (size() < capacity) {
			map.put(e, e);
			super.offer(e);
			return true;
		} else {
			// Case queue is full
			if (comparator.compare(e, peek()) > 0) {
				poll();
				map.put(e, e);
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
	public E poll() {
		final E obj = super.poll();
		map.remove(obj);
		LOG.debug("Removed from queue: %s", obj);
		return obj;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
}
