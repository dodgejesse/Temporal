package edu.uw.cs.utils.log.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory to generated {@link LoggingThread}. Code copied from
 * {@link Executors.DefaultThreadFactory}.
 * 
 * @author Yoav Artzi
 */
public class LoggingThreadFactory implements ThreadFactory {
	static final AtomicInteger	poolNumber		= new AtomicInteger(1);
	final ThreadGroup			group;
	final String				namePrefix;
	final AtomicInteger			threadNumber	= new AtomicInteger(1);
	
	public LoggingThreadFactory() {
		final SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
				.getThreadGroup();
		namePrefix = "P" + poolNumber.getAndIncrement() + "T";
	}
	
	public Thread newThread(Runnable r) {
		final Thread t = new LoggingThread(group, r, namePrefix
				+ threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}
