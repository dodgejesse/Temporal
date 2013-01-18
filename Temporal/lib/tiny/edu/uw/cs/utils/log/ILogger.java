package edu.uw.cs.utils.log;

/**
 * Logger interface.
 * 
 * @author Yoav Artzi
 */
public interface ILogger {
	void debug(Object o1);
	
	void debug(String msg);
	
	void debug(String msg, Object o1);
	
	void debug(String msg, Object o1, Object o2);
	
	void debug(String msg, Object o1, Object o2, Object o3);
	
	void debug(String msg, Object o1, Object o2, Object o3, Object o4);
	
	void debug(String msg, Object o1, Object o2, Object o3, Object o4, Object o5);
	
	void dev(Object o1);
	
	void dev(String msg);
	
	void dev(String msg, Object o1);
	
	void dev(String msg, Object o1, Object o2);
	
	void dev(String msg, Object o1, Object o2, Object o3);
	
	void dev(String msg, Object o1, Object o2, Object o3, Object o4);
	
	void dev(String msg, Object o1, Object o2, Object o3, Object o4, Object o5);
	
	void error(Object o1);
	
	void error(String msg);
	
	void error(String msg, Object o1);
	
	void error(String msg, Object o1, Object o2);
	
	void error(String msg, Object o1, Object o2, Object o3);
	
	void error(String msg, Object o1, Object o2, Object o3, Object o4);
	
	void error(String msg, Object o1, Object o2, Object o3, Object o4, Object o5);
	
	void info(Object o1);
	
	void info(String msg);
	
	void info(String msg, Object o1);
	
	void info(String msg, Object o1, Object o2);
	
	void info(String msg, Object o1, Object o2, Object o3);
	
	void info(String msg, Object o1, Object o2, Object o3, Object o4);
	
	void info(String msg, Object o1, Object o2, Object o3, Object o4, Object o5);
	
	void warn(Object o1);
	
	void warn(String msg);
	
	void warn(String msg, Object o1);
	
	void warn(String msg, Object o1, Object o2);
	
	void warn(String msg, Object o1, Object o2, Object o3);
	
	void warn(String msg, Object o1, Object o2, Object o3, Object o4);
	
	void warn(String msg, Object o1, Object o2, Object o3, Object o4, Object o5);
}
