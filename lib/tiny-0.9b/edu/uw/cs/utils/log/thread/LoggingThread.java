package edu.uw.cs.utils.log.thread;

import edu.uw.cs.utils.log.Log;
import edu.uw.cs.utils.log.Logger;

/**
 * A thread that carries a logging stream that can be modified.
 * 
 * @author Yoav Artzi
 */
public class LoggingThread extends Thread implements ILoggingThread {
	private Log				log	= Logger.DEFAULT_LOG;
	private final String	prefix;
	
	public LoggingThread(ThreadGroup group, Runnable target, String name,
			long stackSize) {
		super(group, target, name, stackSize);
		this.prefix = String.format("[%s] ", getName());
	}
	
	@Override
	public Log getLog() {
		return log;
	}
	
	@Override
	public void println(String string) {
		synchronized (log) {
			log.println(prefix + string);
		}
	}
	
	@Override
	public void setLog(Log log) {
		this.log = log;
	}
	
}
