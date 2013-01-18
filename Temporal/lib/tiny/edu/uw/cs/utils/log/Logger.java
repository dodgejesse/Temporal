package edu.uw.cs.utils.log;

import java.io.PrintStream;

import edu.uw.cs.utils.log.thread.ILoggingThread;

public class Logger implements ILogger {
	public static Log		DEFAULT_LOG	= new Log();
	
	private static boolean	SKIP_PREFIX	= false;
	
	private final LogLevel	forcedLogLevel;
	
	private final String	prefix;
	
	public Logger(String prefix) {
		this(prefix, null);
	}
	
	public Logger(String prefix, LogLevel forcedLogLevel) {
		this.forcedLogLevel = forcedLogLevel;
		this.prefix = prefix + " :: ";
	}
	
	public static void setSkipPrefix(boolean skipPrefix) {
		SKIP_PREFIX = skipPrefix;
	}
	
	@Override
	public final void debug(Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			debug("%s", o1);
		}
	}
	
	@Override
	public final void debug(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			debug(msg, null, null, null, null, null);
		}
	}
	
	@Override
	public final void debug(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			debug(msg, o1, null, null, null, null);
		}
	}
	
	@Override
	public final void debug(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			debug(msg, o1, o2, null, null, null);
		}
	}
	
	@Override
	public final void debug(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			debug(msg, o1, o2, o3, null, null);
		}
	}
	
	@Override
	public final void debug(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			debug(msg, o1, o2, o3, o4, null);
		}
	}
	
	@Override
	public final void debug(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEBUG)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEBUG))) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}
	
	@Override
	public final void dev(Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			dev("%s", o1);
		}
	}
	
	@Override
	public final void dev(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			dev(msg, null, null, null, null, null);
		}
	}
	
	@Override
	public final void dev(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			dev(msg, o1, null, null, null, null);
		}
	}
	
	@Override
	public final void dev(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			dev(msg, o1, o2, null, null, null);
		}
	}
	
	@Override
	public final void dev(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			dev(msg, o1, o2, o3, null, null);
		}
	}
	
	@Override
	public final void dev(String msg, Object o1, Object o2, Object o3, Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			dev(msg, o1, o2, o3, o4, null);
		}
	}
	
	@Override
	public final void dev(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.DEV)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.DEV))) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}
	
	@Override
	public final void error(Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			error("%s", o1);
		}
	}
	
	@Override
	public final void error(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			error(msg, null, null, null, null, null);
		}
	}
	
	@Override
	public final void error(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			error(msg, o1, null, null, null, null);
		}
	}
	
	@Override
	public final void error(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			error(msg, o1, o2, null, null, null);
		}
	}
	
	@Override
	public final void error(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			error(msg, o1, o2, o3, null, null);
		}
	}
	
	@Override
	public final void error(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			error(msg, o1, o2, o3, o4, null);
		}
	}
	
	@Override
	public final void error(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.ERROR)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.ERROR))) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}
	
	@Override
	public final void info(Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			info("%s", o1);
		}
	}
	
	@Override
	public final void info(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			info(msg, null, null, null, null, null);
		}
	}
	
	@Override
	public final void info(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			info(msg, o1, null, null, null, null);
		}
	}
	
	@Override
	public final void info(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			info(msg, o1, o2, null, null, null);
		}
	}
	
	@Override
	public final void info(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			info(msg, o1, o2, o3, null, null);
		}
	}
	
	@Override
	public final void info(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			info(msg, o1, o2, o3, o4, null);
		}
	}
	
	@Override
	public final void info(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.INFO)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.INFO))) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}
	
	@Override
	public final void warn(Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			warn("%s", o1);
		}
	}
	
	@Override
	public final void warn(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			warn(msg, null, null, null, null, null);
		}
	}
	
	@Override
	public final void warn(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			warn(msg, o1, null, null, null, null);
		}
	}
	
	@Override
	public final void warn(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			warn(msg, o1, o2, null, null, null);
		}
	}
	
	@Override
	public final void warn(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			warn(msg, o1, o2, o3, null, null);
		}
	}
	
	@Override
	public final void warn(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			warn(msg, o1, o2, o3, o4, null);
		}
	}
	
	@Override
	public final void warn(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.includes(LogLevel.WARN)
				|| (forcedLogLevel != null && forcedLogLevel
						.includes(LogLevel.WARN))) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}
	
	private void println(String str) {
		if (Thread.currentThread() instanceof ILoggingThread) {
			((ILoggingThread) Thread.currentThread()).println(SKIP_PREFIX ? str
					: (prefix + str));
		} else {
			DEFAULT_LOG.println(SKIP_PREFIX ? str : (prefix + str));
		}
	}
	
	public interface IOutputStreamGetter {
		PrintStream get();
	}
	
}
