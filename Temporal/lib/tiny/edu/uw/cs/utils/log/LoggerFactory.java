package edu.uw.cs.utils.log;

public class LoggerFactory {
	
	private LoggerFactory() {
	}
	
	public static ILogger create(Class<?> classObject) {
		return new Logger(classObject.getSimpleName());
	}
	
	public static ILogger create(Class<?> classObject, LogLevel forcedLogLevel) {
		return new Logger(classObject.getSimpleName(), forcedLogLevel);
	}
	
	public static ILogger create(String prefix) {
		return new Logger(prefix);
	}
	
	public static ILogger create(String prefix, LogLevel forcedLogLevel) {
		return new Logger(prefix, forcedLogLevel);
	}
}
