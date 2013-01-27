package edu.uw.cs.utils.log.thread;

import java.util.concurrent.Callable;

import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.Logger;

public abstract class LoggingCallable<V> implements Callable<V> {
	
	private Log	log;
	
	public LoggingCallable() {
		if (Thread.currentThread() instanceof ILoggingThread) {
			this.log = ((ILoggingThread) Thread.currentThread()).getLog();
		} else {
			this.log = Logger.DEFAULT_LOG;
		}
	}
	
	public LoggingCallable(Log log) {
		this.log = log;
	}
	
	@Override
	public final V call() throws Exception {
		final Log originalLog;
		if (Thread.currentThread() instanceof ILoggingThread) {
			originalLog = ((ILoggingThread) Thread.currentThread()).getLog();
			((ILoggingThread) Thread.currentThread()).setLog(log);
		} else {
			originalLog = null;
		}
		
		final V ret = loggedCall();
		
		if (Thread.currentThread() instanceof ILoggingThread) {
			((ILoggingThread) Thread.currentThread()).setLog(originalLog);
		}
		
		return ret;
	}
	
	public abstract V loggedCall();
	
}
