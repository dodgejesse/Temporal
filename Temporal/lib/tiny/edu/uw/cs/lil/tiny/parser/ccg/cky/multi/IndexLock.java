package edu.uw.cs.lil.tiny.parser.ccg.cky.multi;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IndexLock {
	
	private final Lock[]	indices;
	
	public IndexLock(int length) {
		this.indices = new Lock[length];
		for (int i = 0; i < length; ++i) {
			indices[i] = new ReentrantLock();
		}
	}
	
	public void lock(int index) {
		indices[index].lock();
	}
	
	public void unlock(int index) {
		indices[index].unlock();
	}
	
}
