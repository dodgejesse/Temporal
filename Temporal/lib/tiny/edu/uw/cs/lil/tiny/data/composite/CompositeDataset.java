package edu.uw.cs.lil.tiny.data.composite;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataCollection;
import edu.uw.cs.utils.collections.CompositeIterator;

public class CompositeDataset<T> implements IDataCollection<T> {
	
	private final List<IDataCollection<? extends T>>	datasets;
	
	public CompositeDataset(IDataCollection<? extends T>... datasets) {
		this(Arrays.asList(datasets));
	}
	
	public CompositeDataset(List<IDataCollection<? extends T>> datasets) {
		this.datasets = datasets;
	}
	
	@Override
	public Iterator<T> iterator() {
		return createIterator();
	}
	
	@Override
	public int size() {
		return calculateSize();
	}
	
	private int calculateSize() {
		int sum = 0;
		for (final IDataCollection<? extends T> dataset : datasets) {
			sum += dataset.size();
		}
		return sum;
	}
	
	private Iterator<T> createIterator() {
		final List<Iterator<? extends T>> iterators = new LinkedList<Iterator<? extends T>>();
		for (final IDataCollection<? extends T> dataset : datasets) {
			iterators.add(dataset.iterator());
		}
		return new CompositeIterator<T>(iterators);
	}
}
