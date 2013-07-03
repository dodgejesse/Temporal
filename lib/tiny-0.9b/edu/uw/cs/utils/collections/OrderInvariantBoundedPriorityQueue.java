package edu.uw.cs.utils.collections;

import java.util.Comparator;
import java.util.PriorityQueue;

import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * {@link PriorityQueue} with bounded capacity. The queue is invariant to the
 * order of insertion. When the queue is full and the offered object is tied
 * with the head of the queue, the offered object is rejected and the queue is
 * cleared from all equivalent objects. Future objects that are "smaller" or
 * equivalent will be rejected as well. Therefore, the queue might be
 * under-staffed but will still reject new objects.
 * 
 * @author Yoav Artzi
 * @param <E>
 */
public class OrderInvariantBoundedPriorityQueue<E> extends PriorityQueue<E> {
	private static final ILogger		LOG					= LoggerFactory
																	.create(OrderInvariantBoundedPriorityQueue.class);
	private static final long			serialVersionUID	= 581319921589030679L;
	private final int					capacity;
	private final Comparator<? super E>	comparator;
	private E							thresholdObject		= null;
	
	public OrderInvariantBoundedPriorityQueue(int capacity,
			Comparator<? super E> comparator) {
		super(capacity, comparator);
		this.capacity = capacity;
		this.comparator = comparator;
	}
	
	@Override
	public boolean add(E e) {
		if (size() < capacity
				&& (thresholdObject == null || comparator.compare(e,
						thresholdObject) > 0)) {
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
	public void clear() {
		super.clear();
		thresholdObject = null;
	}
	
	@Override
	public boolean offer(E e) {
		if (thresholdObject != null
				&& comparator.compare(e, thresholdObject) <= 0) {
			// Case there's a threshold object and it's equal to the offered
			// object, reject the offered object
			return false;
		} else if (size() < capacity) {
			super.offer(e);
			return true;
		} else {
			// Case queue is full
			final int comparison = comparator.compare(e, peek());
			if (comparison > 0) {
				// Remove the smallest element, update threshold
				updateThresholdObject(poll());
				// Add the new element
				super.offer(e);
				return true;
			} else if (comparison == 0) {
				updateThresholdObject(poll());
				return false;
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
		LOG.debug("Removed from queue: %s", obj);
		return obj;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	private void updateThresholdObject(E object) {
		thresholdObject = object;
		LOG.debug("Updated threshold object: %s", thresholdObject);
		while (!isEmpty() && comparator.compare(object, peek()) == 0) {
			poll();
		}
	}
	
}
