package edu.uw.cs.utils.log;

/**
 * Static class to hold current log level.
 * 
 * @author Yoav Artzi
 */
public enum LogLevel {
	DEBUG(5), DEV(4), ERROR(1), INFO(3), NO_LOG(0), WARN(2);
	
	/**
	 * The current log level.
	 */
	static LogLevel		CURRENT_LOG_LEVEL	= ERROR;
	
	/** The numerical value of the level */
	private final int	level;
	
	private LogLevel(int level) {
		this.level = level;
	}
	
	public static void setLogLevel(LogLevel logLevel) {
		CURRENT_LOG_LEVEL = logLevel;
	}
	
	public boolean includes(LogLevel logLevel) {
		return level >= logLevel.level;
	}
	
	public boolean isActive() {
		return CURRENT_LOG_LEVEL.includes(this);
	}
	
	/**
	 * Set the current level to be the global log level.
	 */
	public void set() {
		LogLevel.setLogLevel(this);
	}
}
