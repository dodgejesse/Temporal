package edu.uw.cs.utils.cache;

import java.util.LinkedHashMap;

/**
 * Least recently used cache with predefined maximum size. Not thread safe.
 * 
 * @author Yoav Artzi
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	private static final long	serialVersionUID	= 200296264684758492L;
	private final int			size;
	
	public LRUCache(int size) {
		super(size, 0.75f, true);
		this.size = size;
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() >= size;
	}
}
