package logic;

import util.StreamLogger;
import util.StreamLogger.LogLevel;

public abstract class BaseLogic {
	protected StreamLogger logger = StreamLogger.init(getLoggerComponentName());
	
	protected abstract String getLoggerComponentName();
	protected void logDebug(String message) {
		logger.log(LogLevel.DEBUG, message);
	}
	protected void logInfo(String message) {
		logger.log(LogLevel.INFO, message);
	}
	protected void logWarning(String message) {
		logger.log(LogLevel.WARNING, message);
	}
	protected void logError(String message) {
		logger.log(LogLevel.ERROR, message);
	}
	protected void logFatal(String message) {
		logger.log(LogLevel.FATAL, message);
	}
}
