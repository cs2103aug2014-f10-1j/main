package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

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
	public static final String LOG_ADD = "Added \"%1$s\"";
	public static final String LOG_DELETE = "Deleted \"%1$s\"";
	public static final String LOG_VIEW = "Viewed the details for \"%1$s\"";
	public static final String LOG_DESC = "Changed description for \"%1$s\" to \"%2$s\"";
	public static final String LOG_CLEAR = "Cleared all tasks";
	public static final String LOG_RECOVER = "Recovered %1$s task(s)";
	public static final String LOG_MODIFY = "Changed name for \"%1$s\" to \"%2$s\"";
	public static final String LOG_MARK = "\"%1$s\" marked as %2$s";
	public static final String LOG_ERRORS = "%1$s: %2$s";
	public static final String LOG_UNDO_FAIL = "Nothing to undo";
	public static final String LOG_UNDO_SUCCESS = "Undoing the previous action";
	public static final String LOG_DUE_NEVER = "Due date for \"%1$s\" is removed";
	public static final String LOG_DUE = "Due date for \"%1$s\" set to \"%2$s\"";
	public static final String LOG_CMD_UNKNOWN = "Unknown command entered";
	public static final String LOG_TAGS_ADDED = "Tags added to \"%1$s\": %2$s";
	public static final String LOG_TAGS_NOT_ADDED = "Tags not added to \"%1$s\": %2$s";
	public static final String LOG_NO_TAGS_ADDED = "No new tags added";
	public static final String LOG_TAGS_REMOVED = "Tags removed from \"%1$s\": %2$s";
	public static final String LOG_TAGS_NOT_REMOVED = "Tags not removed \"%1$s\": %2$s";
	public static final String LOG_NO_TAGS_REMOVED = "No tags removed";
	public static final String LOG_SEARCH = "Searching for \"%1$s\", %2$s queries found";
	public static final String LOG_LOAD_FAILED = "Load from file failed, %1$s";
	public static final String LOG_SAVE_FAILED = "Save to file failed, %1$s";

	// Exception messages
	public static final String ERR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";

	// Reverse commands (for undo)
	public static final String CMD_DISMISS = "dismiss %1$s";
	public static final String CMD_RECOVER = "recover %1$s";
	public static final String CMD_DESC = "desc %1$s %2$s";
	public static final String CMD_DUE = "due %1$s %2$s";
	public static final String CMD_MARK_DONE = "mark %1$s done";
	public static final String CMD_MARK_NOT_DONE = "mark %1$s ongoing";
	public static final String CMD_DELETE = "delete %1$s";
	public static final String CMD_TAG = "tag %1$s %2$s";
	public static final String CMD_UNTAG = "untag %1$s %2$s";
	public static final String CMD_MODIFY = "modify %1$s desc %2$s tag %3$s due %4$s";

	// Assertion failure messages
	public static final String FAIL_NULL_INPUT = "Null input value detected";
	public static final String FAIL_NOT_CLEARED = "Task clearance failed";
	public static final String FAIL_NOT_ADDED = "Task addition failed";
	public static final String FAIL_NOT_DELETED = "Task deletion failed";
	public static final String FAIL_NOT_EQUAL = "The two lists are not equal";

	public static final String[] MONTHS = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };

	// Messages to be shown to user
	public static final String MSG_WELCOME = "Welcome to Stream "
			+ PARAM_VERSION + "!";
	public static final String MSG_THANK_YOU = "Thank you for using this internal release of Stream[BETA]!";

	public static final String TEXT_TITLE = "STREAM " + PARAM_VERSION
			+ ": Simple Task Reader and Manager";
	public static final String TEXT_HEADER = "<html>S<font color=\"#ed7d31\">T</font>RE<font color=\"#ed7d31\">A</font>M</html>";
	public static final String TEXT_FOOTER = "Copyright \u00a9 2014 CS2013AUG2014-F10-01J. All rights reserved.";
	public static final String TEXT_INDEX = "#%1$s";

	public static final Color COLOR_HEADER = Color.getHSBColor(
			(float) 208.52 / 360, (float) 0.5728, (float) 0.8353);
	public static final Color COLOR_LOG_MSG = Color.BLACK;
	public static final Color COLOR_ERR_MSG = Color.RED;

	public static final Font FONT_TITLE = new Font("Awesome Java", Font.PLAIN,
			100);
	public static final Font FONT_CONSOLE = new Font("Ubuntu", Font.PLAIN, 13);
	public static final Font FONT_LOGGER = new Font("Ubuntu", Font.PLAIN, 13);
	public static final Font FONT_FOOTER = new Font("Ubuntu", Font.PLAIN, 12);
	public static final Font FONT_INDEX = new Font("Awesome Java", Font.PLAIN,
			70);
	public static final Font FONT_TASK = new Font("Ubuntu", Font.PLAIN, 13);

	public static final Border MARGIN_MAINFRAME = BorderFactory
			.createEmptyBorder(20, 10, 20, 10);
	public static final Insets MARGIN_HEADER = new Insets(0, 0, 0, 0);
	public static final Insets MARGIN_FOOTER = new Insets(10, 10, 0, 10);
	public static final Insets MARGIN_ELEM = new Insets(10, 10, 0, 10);

	public static final int HEIGHT_MAINFRAME = 750;
	public static final int WIDTH_MAINFRAME = 750;

	public static final int MAX_VIEWABLE_TASK = 7;

	public static final int GRIDWIDTH_NAVIG = 1;
	public static final int GRIDWIDTH_BUTTON = GRIDWIDTH_NAVIG * 4;
	public static final int GRIDWIDTH_HEADER = GRIDWIDTH_BUTTON * 3;
	public static final int GRIDWIDTH_CONSOLE = GRIDWIDTH_BUTTON * 3;
	public static final int GRIDWIDTH_LOGGER = GRIDWIDTH_BUTTON * 3;
	public static final int GRIDWIDTH_FOOTER = GRIDWIDTH_BUTTON * 3;
	public static final int GRIDWIDTH_TASK = GRIDWIDTH_BUTTON * 3;

	public static final int GRIDX_HEADER = 0;
	public static final int GRIDX_UNDO = 0;
	public static final int GRIDX_FIRST = GRIDX_UNDO + GRIDWIDTH_BUTTON;
	public static final int GRIDX_PREV = GRIDX_FIRST + GRIDWIDTH_NAVIG;
	public static final int GRIDX_NEXT = GRIDX_PREV + GRIDWIDTH_NAVIG;
	public static final int GRIDX_LAST = GRIDX_NEXT + GRIDWIDTH_NAVIG;
	public static final int GRIDX_CLEAR = GRIDX_LAST + GRIDWIDTH_NAVIG;
	public static final int GRIDX_CONSOLE = 0;
	public static final int GRIDX_LOGGER = 0;
	public static final int GRIDX_FOOTER = 0;
	public static final int GRIDX_TASK = 0;

	public static final int GRIDY_HEADER = 0;
	public static final int GRIDY_BUTTON = MAX_VIEWABLE_TASK + 1;
	public static final int GRIDY_CONSOLE = GRIDY_BUTTON + 1;
	public static final int GRIDY_LOGGER = GRIDY_CONSOLE + 1;
	public static final int GRIDY_FOOTER = GRIDY_LOGGER + 1;

	public static final int IPADY_HEADER = 0;
	public static final int IPADY_BUTTON = 5;
	public static final int IPADY_CONSOLE = 10;
	public static final int IPADY_LOGGER = 10;
	public static final int IPADY_FOOTER = 0;
	public static final int IPADY_TASK = 0;

	public static final String BTN_FIRST = "<<";
	public static final String BTN_PREV = "<";
	public static final String BTN_NEXT = ">";
	public static final String BTN_LAST = ">>";
	public static final String BTN_CLEAR = "Clear search result";
	public static final String BTN_UNDO = "Undo last action";
	public static final String BTN_DELETE = "Delete";
	public static final String BTN_MARK_DONE = "Mark as done";
	public static final String BTN_MARK_NOT_DONE = "Mark as ongoing";

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

	// @author A0093874N

	public static void clearAllActionListeners(JButton button,
			ActionListener[] actions) {
		for (ActionListener action : actions) {
			button.removeActionListener(action);
		}
	}

	// @author A0093874N

	public static String getWrittenTime(Calendar startTime, Calendar endTime) {
		if (startTime == null && endTime == null) {
			return "No timing specified";
		} else if (startTime == null) {
			return "By " + getCalendarWriteUp(endTime);
		} else if (endTime == null) {
			// is there a task like this?
			return "From " + getCalendarWriteUp(startTime);
		} else {
			return "From " + getCalendarWriteUp(startTime) + " to "
					+ getCalendarWriteUp(endTime);
		}
	}

	// @author A0093874N

	public static String getCalendarWriteUp(Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH) + "/"
				+ (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.YEAR);
	}

}