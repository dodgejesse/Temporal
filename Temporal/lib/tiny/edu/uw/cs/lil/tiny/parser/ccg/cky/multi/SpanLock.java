package edu.uw.cs.lil.tiny.parser.ccg.cky.multi;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpanLock {
	
	private final Lock[][]	spans;
	
	public SpanLock(int length) {
		this.spans = new Lock[length][length];
		for (int i = 0; i < length; ++i) {
			for (int j = 0; j < length; ++j) {
				spans[i][j] = new ReentrantLock();
			}
		}
	}
	
	public void lock(int start, int end) {
		spans[start][end].lock();
	}
	
	public void unlock(int start, int end) {
		spans[start][end].unlock();
	}
	
}
