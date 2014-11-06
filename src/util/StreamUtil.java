package util;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

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

	public static final String DATE_DELIMITER = "/";
	public static final String TIME_DELIMITER = ":";
	public static final String PREFIX_INPUT = "<< ";
	public static final String PREFIX_OUTPUT = ">> ";
	public static final SimpleDateFormat cleanDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static final String[] MODIFICATION_ATTRIBUTES = { "-name", "-desc", "-start", "-from", "-due", "-by",
		"-tag", "-untag", "-settags", "-rank", "-mark", "-to" }; //Not sure if this is the right place, but for now its here.
	private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", 
		"October", "November", "December"};
	

	// @author A0118007R
	public static boolean isValidAttribute(String param) {
		for (String s : MODIFICATION_ATTRIBUTES) {
			if (s.equals(param)) {
				return true;
			}
		}

		return false;
	}
	
	// @author A0118007R

	public static boolean isValidMonth(int month) {
		return (month >= 1) && (month <= 12);
	}

	// @author A0118007R

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

	// @author A0118007R

	public static boolean isLeapYear(int year) {
		if (year % 400 == 0) {
			return true;
		} else if (year % 100 == 0) {
			return false;
		} else {
			return year % 4 == 0;
		}
	}

	// @author A0118007R

	private static boolean isMonthWith31Days(int month) {
		return (month == 1) || (month == 3) || (month == 5) || (month == 7)
				|| (month == 8) || (month == 10) || (month == 12);
	}

	// @author A0118007R

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

	// @author A0093874N

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

	public static String showAsTerminalInput(String logMessage) {
		return PREFIX_INPUT + logMessage;
	}

	// @author A0093874N

	public static String showAsTerminalResponse(String logMessage) {
		return PREFIX_OUTPUT + logMessage;
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
		return addZeroToTime(calendar.get(Calendar.DAY_OF_MONTH))
				+ " "
				+ MONTHS[calendar.get(Calendar.MONTH)]
				+ " " + calendar.get(Calendar.YEAR) + " "
				+ addZeroToTime(calendar.get(Calendar.HOUR_OF_DAY))
				+ TIME_DELIMITER + addZeroToTime(calendar.get(Calendar.MINUTE))
				+ TIME_DELIMITER + addZeroToTime(calendar.get(Calendar.SECOND));
	}

	// @author A0096529N

	public static String getDateString(Calendar calendar) {
		return cleanDateFormat.format(calendar.getTime());
	}

	// @author A0093874N

	public static String stripCalendarChars(String str) {
		str = str.replaceAll(TIME_DELIMITER, " ");
		return str;
	}
	
	// @author A0118007R
	public static String parseWithChronic(String due) {
		Span x;
		try {
			x = Chronic.parse(due);
			Calendar begin = x.getBeginCalendar();
			String calendarWriteUp = StreamUtil.getCalendarWriteUp(begin);
			due = StreamUtil.stripCalendarChars(calendarWriteUp);
		} catch (NullPointerException e) {
			System.out.println("\"" + due + "\" cannot be parsed");
			//change to logging... show to user?
		}
		return due;
	}

	// @author A0118007R

	public static Calendar parseCalendar(String contents) {
		String[] dueDate = contents.split(" ");
		int[] dueDateParameters = new int[dueDate.length];
		for (int i = 0; i < dueDate.length; i++) {
			if (i != 1) {
			dueDateParameters[i] = Integer.parseInt(dueDate[i].trim());
			}
			
		}
		
		int date = dueDateParameters[0];
		int month = getMonthIndex(dueDate[1]);
		int year = dueDateParameters[2];
		int hour = dueDateParameters[3];
		int minute = dueDateParameters[4];
		int second = dueDateParameters[5];
		Calendar calendar = new GregorianCalendar(year, month - 1, date, hour, minute, second);
		return calendar;
	}

	private static int getMonthIndex(String month) {
		switch (month) {
			case "January":
				return 1;
			case "February":
				return 2;
			case "March":
				return 3;
			case "April":
				return 4;
			case "May":
				return 5;
			case "June":
				return 6;
			case "July":
				return 7;
			case "August":
				return 8;
			case "September":
				return 9;
			case "October":
				return 10;
			case "November":
				return 11;
			case "December":
				return 12;
			default:
				return 0;
		}
	}

	// @author A0118007R

	public static int parseYear(String[] dueDate) {
		int year;
		if (dueDate.length == 2) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		} else {
			year = Integer.parseInt(dueDate[2].trim());
		}
		return year;
	}

	// @author A0119401U

	public static boolean isInteger(String str) {
		int size = str.length();
		for (int i = 0; i < size; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return size > 0;
	}

}