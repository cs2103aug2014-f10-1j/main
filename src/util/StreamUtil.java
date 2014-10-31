package util;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

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
	// Date-Validity Checking methods
	// @author A0118007R

	public static boolean isValidMonth(int month) {
		return (month >= 1) && (month <= 12);
	}

	public static boolean isValidDate(int day, int month, int year) {
		if (isMonthWith31Days(month)) {
			return (day >= 1) && (day <= 31);
		} else if (month == 2) {
			if (isLeapYear(year)) {
				return (day >= 1) && (day <= 29);
			} else {
				return (day >= 1) && (day <= 28);
			}	
		} else {
			return (day >= 1) && (day <= 30);
		}
		
	}
	
	public static boolean isLeapYear(int year) {
		if (year % 400 == 0) {
			return true;
		} else if (year % 100 == 0) {
			return false;
		} else {
			return year % 4 == 0;
		}
	}

	private static boolean isMonthWith31Days(int month) {
		return (month == 1) || (month == 3) || (month == 5) || (month ==7) ||
				(month == 8) || (month == 10) || (month == 12);
	}

	public static boolean isValidYear(int year) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return year >= currentYear;
	}

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

	public static String displayDescription(String desc) {
		if (desc == null) {
			return "No description provided";
		} else {
			return desc;
		}
	}

	// @author A0093874N

	public static String displayTags(ArrayList<String> tags) {
		if (tags.size() == 0) {
			return "No tags added";
		} else {
			return listDownArrayContent(tags, ", ");
		}
	}

	public static String displayTagsAsCommand(ArrayList<String> tags) {
		if (tags.size() == 0) {
			return null;
		} else {
			return listDownArrayContent(tags, " ");
		}
	}

	// @author A0093874N

	public static String displayStatus(Boolean isDone) {
		if (isDone) {
			return "Done";
		} else {
			return "Ongoing";
		}
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

	public static void clearAllMouseListeners(JLabel label,
			MouseListener[] actions) {
		for (MouseListener action : actions) {
			label.removeMouseListener(action);
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

	// TODO @author?

	public static Calendar parseCalendar(String contents) {
		String[] dueDate = contents.split("/");
		int year = parseYear(dueDate);
		int day = Integer.parseInt(dueDate[0].trim());
		int month = Integer.parseInt(dueDate[1].trim());
		Calendar calendar = new GregorianCalendar(year, month - 1, day);
		return calendar;
	}

	// TODO @author ?

	public static int parseYear(String[] dueDate) {
		int year;
		if (dueDate.length == 2) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		} else {
			year = Integer.parseInt(dueDate[2].trim());
		}
		return year;
	}

	// @author A0118007R

	public static boolean isInteger(String x) {
		try {
			Integer.parseInt(x);
			return true;
		} catch (Exception e) {

		}
		return false;
	}
	
}