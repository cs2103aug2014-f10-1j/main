package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Steven Khong
 */
public class StreamLogger {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	private String componentName;
	private static List<String> logStack;
	private static final String LOG_FORMAT = "%$1s %$2s [%$3s] %$4s";
	
	public enum LogLevel {
		DEBUG, INFO, WARNING, ERROR, FATAL;
	}

	//@author A0096529N
	/**
	 * Logger component for Stream.
	 * 
	 * <pre>
	 * {@code
	 * //Initialize logger, similar to Object.getInstance() taught.
	 * StreamLogger logger = StreamLogger.init(componentName);
	 * 
	 * //Use logger to add log to log stack
	 * logger.log(LogLevel.DEBUG, logMessage);
	 * }
	 * </pre>
	 * 
	 * @param componentName standardized name of component
	 * @return StreamLogger instance for use to log
	 */
	public static StreamLogger init(String componentName) {
		StreamLogger logger = new StreamLogger();
		logger.componentName = componentName;
		return logger;
	}

	//@author A0096529N
	/**
	 * Adds log message to synchronized log stack.
	 * 
	 * @param logLevel importance level of log message
	 * @param message the log message to be logged
	 */
	public void log(LogLevel logLevel, String message) {
		synchronized (logStack) {
			logStack.add(String.format(LOG_FORMAT, getDate(), getLevel(logLevel), componentName, message));	
		}
	}

	//@author A0096529N
	private static String getDate() {
		return format.format(new Date());
	}

	//@author A0096529N
	private static String getLevel(LogLevel logLevel) {
		switch (logLevel) {
		case DEBUG:
			return "DEBUG";
		case INFO:
			return "INFO";
		case WARNING:
			return "WARNING";
		case ERROR:
			return "ERROR";
		case FATAL:
			return "FATAL";
		}
		return null;
	}
}
