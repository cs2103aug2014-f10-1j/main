package util;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to contain all constants (that otherwise will clutter the other
 * classes) and helper methods (methods that help some of Stream's processes but
 * don't really have anything to do with the nature of Stream's processes; e.g
 * listing down an array). Feel free to add on to this class!
 * 
 * @author Wilson Kurniawan, with contributions from John Kevin Tjahjadi, Steven
 *         Khong Wai How, Jiang Shenhao
 */
public class StreamUtil {

	public static final String PARAM_VERSION = "V0.2";
	public static final String PARAM_FILENAME = "stream";
	public static final String PARAM_SAVEFILE = "%1$s.json";
	public static final String PARAM_LOGFILE = "%1$s %2$s%3$s%4$s %5$s%6$s%7$s.txt";

	// Log messages
	public static final String LOG_CMD_RECEIVED = "Command received [%1$s]";
	public static final String LOG_ADD = "Added %1$s";
	public static final String LOG_DELETE = "Deleted %1$s";
	public static final String LOG_VIEW = "Viewed the details for %1$s";
	public static final String LOG_DESC = "Changed description for %1$s to %2$s";
	public static final String LOG_CLEAR = "Cleared all tasks";
	public static final String LOG_RECOVER = "Recovered %1$s";
	public static final String LOG_MODIFY = "Changed name for %1$s to %2$s";
	public static final String LOG_MARK = "%1$s marked as %2$s";
	public static final String LOG_ERRORS = "%1$s: %2$s";
	public static final String LOG_UNDO_FAIL = "Nothing to undo";
	public static final String LOG_UNDO_SUCCESS = "Undoing the previous action";
	public static final String LOG_DUE_NEVER = "Due date for %1$s is removed";
	public static final String LOG_DUE = "Due date for %1$s set to %2$s";
	public static final String LOG_CMD_UNKNOWN = "Unknown command entered";
	public static final String LOG_TAGS_ADDED = "Tags added to %1$s: %2$s";
	public static final String LOG_TAGS_NOT_ADDED = "Tags not added to %1$s: %2$s";
	public static final String LOG_TAGS_REMOVED = "Tags removed from %1$s: %2$s";
	public static final String LOG_TAGS_NOT_REMOVED = "Tags not removed %1$s: %2$s";
	public static final String LOG_SEARCH = "Searching for %1$s, %2$s queries found";
	public static final String LOG_LOAD_FAILED = "Load from file failed, %1$s";
	public static final String LOG_SAVE_FAILED = "Save to file failed, %1$s";

	// Exception messages
	public static final String ERR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";

	// Reverse commands (for undo)
	public static final String CMD_DISMISS = "dismiss %1$s";
	public static final String CMD_RECOVER = "recover %1$s";
	public static final String CMD_MODIFY = "modify %1$s %2$s";
	public static final String CMD_DESC = "desc %1$s %2$s";
	public static final String CMD_DUE = "due %1$s %2$s";
	public static final String CMD_MARK = "mark %1$s %2$s";

	// Assertion failure messages
	public static final String FAIL_NULL_INPUT = "Null input value detected";
	public static final String FAIL_NOT_CLEARED = "Task clearance failed";
	public static final String FAIL_NOT_ADDED = "Task addition failed";
	public static final String FAIL_NOT_DELETED = "Task deletion failed";
	public static final String FAIL_NOT_EQUAL = "The two lists are not equal";

	public static final String[] MONTHS = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };

	// Messages to be shown to user
	public static final String MSG_WELCOME = "Welcome to Stream "
			+ PARAM_VERSION + "!";
	public static final String MSG_THANK_YOU = "Thank you for using this internal release of Stream[BETA]!";

	// @author A0093874N

	public static String listDownArrayContent(ArrayList<String> array,
			String connector) {
		String result = "";
		for (String str : array) {
			result += connector + str;
		}
		return result.substring(connector.length());
	}

	// @author A0093874N

	public static String addZeroToTime(Integer time) {
		String convertedTime = time.toString();
		if (time < 10) {
			convertedTime = "0" + convertedTime;
		}
		return convertedTime;
	}

	// @author A0093874N

	public static String showAsTerminalResponse(String logMessage) {
		final String PREFIX = ">> ";
		return PREFIX + logMessage;
	}

	// @author A0093874N

	public static Boolean listEqual(List<String> firstList,
			List<String> secondList) {
		if (firstList.size() != secondList.size()) {
			return false;
		}
		for (String str : firstList) {
			if (!secondList.contains(str)) {
				return false;
			}
		}
		return true;
	}

}