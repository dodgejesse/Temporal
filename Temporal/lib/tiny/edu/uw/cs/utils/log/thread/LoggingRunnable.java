package edu.uw.cs.utils.log.thread;

import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.Logger;

public abstract class LoggingRunnable implements Runnable {
	
	private Log	log;
	
	public LoggingRunnable() {
		if (Thread.currentThread() instanceof ILoggingThread) {
			this.log = ((ILoggingThread) Thread.currentThread()).getLog();
		} else {
			this.log = Logger.DEFAULT_LOG;
		}
	}
	
	public LoggingRunnable(Log log) {
		this.log = log;
	}
	
	public abstract void loggedRun();
	
	@Override
	public final void run() {
		final Log originalLog;
		if (Thread.currentThread() instanceof ILoggingThread) {
			originalLog = ((ILoggingThread) Thread.currentThread()).getLog();
			((ILoggingThread) Thread.currentThread()).setLog(log);
		} else {
			originalLog = null;
		}
		
		loggedRun();
		
		if (Thread.currentThread() instanceof ILoggingThread) {
			((ILoggingThread) Thread.currentThread()).setLog(originalLog);
		}
	}
	
}
