package edu.uw.cs.utils.log.thread;

import edu.uw.cs.utils.log.Log;

public interface ILoggingThread {
	Log getLog();
	
	void println(String string);
	
	void setLog(Log log);
}
