package edu.uw.cs.lil.tiny.utils.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TinyExecutorService implements ExecutorService, ITinyExecutor {
	private final ThreadPoolExecutor	executor;
	
	public TinyExecutorService(int nThreads) {
		this(nThreads, Executors.defaultThreadFactory());
	}
	
	public TinyExecutorService(int nThreads, ThreadFactory threadFactory) {
		this.executor = new ThreadPoolExecutor(nThreads, nThreads, 0L,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				threadFactory);
		executor.allowCoreThreadTimeOut(false);
	}
	
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() + 1);
		}
		final boolean result = executor.awaitTermination(timeout, unit);
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() + 1);
		}
		return result;
	}
	
	@Override
	public void execute(Runnable command) {
		executor.execute(command);
	}
	
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() + 1);
		}
		final List<Future<T>> result = executor.invokeAll(tasks);
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() - 1);
		}
		return result;
	}
	
	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() + 1);
		}
		final List<Future<T>> result = executor.invokeAll(tasks, timeout, unit);
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() - 1);
		}
		return result;
	}
	
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public boolean isShutdown() {
		return executor.isShutdown();
	}
	
	@Override
	public boolean isTerminated() {
		return executor.isTerminated();
	}
	
	@Override
	public void shutdown() {
		executor.shutdown();
	}
	
	@Override
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}
	
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return executor.submit(task);
	}
	
	@Override
	public Future<?> submit(Runnable task) {
		return executor.submit(task);
	}
	
	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return executor.submit(task, result);
	}
	
	@Override
	public void wait(Object object) throws InterruptedException {
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() + 1);
		}
		try {
			object.wait();
		} finally {
			synchronized (executor) {
				executor.setCorePoolSize(executor.getCorePoolSize() - 1);
			}
		}
	}
	
	@Override
	public void wait(Object object, long timeout) throws InterruptedException {
		synchronized (executor) {
			executor.setCorePoolSize(executor.getCorePoolSize() + 1);
		}
		try {
			object.wait(timeout);
		} finally {
			synchronized (executor) {
				executor.setCorePoolSize(executor.getCorePoolSize() - 1);
			}
		}
	}
	
}
