package edu.uw.cs.lil.tiny.utils.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface ITinyExecutor extends Executor {
	<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException;
	
	<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException;
	
	<T> Future<T> submit(Callable<T> task);
	
	void wait(Object object) throws InterruptedException;
	
	/**
	 * @param object
	 * @param timeout
	 *            the maximum time to wait in milliseconds.
	 * @throws InterruptedException
	 */
	void wait(Object object, long timeout) throws InterruptedException;
}
