package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StreamLogger {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	private String component;
	private static List<String> logStack;
	private static final String LOG_FORMAT = "%$1s %$2s [%$3s] %$4s";
	
	public enum LogLevel {
		DEBUG, INFO, WARNING, ERROR, FATAL;
	}
	public static StreamLogger init(String component) {
		StreamLogger logger = new StreamLogger();
		logger.component = component;
		return logger;
	}
	
	public void log(LogLevel logLevel, String message) {
		synchronized (logStack) {
			logStack.add(String.format(LOG_FORMAT, getDate(), getLevel(logLevel), component, message));	
		}
	}
	
	private static String getDate() {
		return format.format(new Date());
	}
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
