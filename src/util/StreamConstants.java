package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class StreamConstants {

	public static final String VERSION = "V0.4";
	public static final String FILENAME = "stream";
	public static final String SAVEFILE = "%1$s.json";
	public static final String LOGFILE = "%1$s.txt";

	public static class Calendar {
		public static final String[] MONTHS = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
	}

	public static class ComponentTag {
		public static final String STREAM = "stream";
		public static final String STREAMUI = "ui";
		public static final String STREAMLOGIC = "streamLogic";
		public static final String STREAMTASK = "taskLogic";
		public static final String STREAMSTACK = "stackLogic";
		public static final String STREAMIO = "io";
		public static final String STREAMPARSER = "parser";
	}

	public static class LogMessage {
		public static final String CMD_RECEIVED = "Command received [%1$s]";
		public static final String ADD = "Added \"%1$s\"";
		public static final String DELETE = "Deleted \"%1$s\"";
		public static final String VIEW = "Viewed the details for \"%1$s\"";
		public static final String DESC = "Changed description for \"%1$s\" to \"%2$s\"";
		public static final String RANK = "Changed ranking for \"%1$s\" to \"%2$s\"";
		public static final String CLEAR = "Cleared all tasks";
		public static final String RECOVER = "Recovered %1$s task(s)";
		public static final String UNSORT = "Restored to previous task sorting";
		public static final String NAME = "Changed name for \"%1$s\" to \"%2$s\"";
		public static final String MARK = "\"%1$s\" marked as %2$s";
		public static final String ERRORS = "%1$s: %2$s";
		public static final String UNDO_FAIL = "No previous action to undo";
		public static final String UNDO_SUCCESS = "Reverted the previous action";
		public static final String DUE_NEVER = "Due date for \"%1$s\" is removed";
		public static final String DUE = "Due date for \"%1$s\" set to \"%2$s\"";
		public static final String START = "Start date for \"%1$s\" set to \"%2$s\"";
		public static final String START_NOT_SPECIFIED = "Start Time for \"%1$s\" is not specified";
		public static final String CMD_UNKNOWN = "Unknown command entered";
		public static final String TAGS_TO_ADD = "Tags to add \"%1$s\": %2$s";
		public static final String TAGS_TO_REMOVE = "Tags to remove \"%1$s\": %2$s";
		public static final String TAGS_NOT_ADDED = "Tags not added to \"%1$s\": %2$s";
		public static final String TAGS_NOT_REMOVED = "Tags not removed \"%1$s\": %2$s";
		public static final String TAGS_ADDED = "Tags added to \"%1$s\": %2$s";
		public static final String NO_TAGS_ADDED = "No new tags added";
		public static final String TAGS_REMOVED = "Tags removed from \"%1$s\": %2$s";
		public static final String NO_TAGS_REMOVED = "No tags removed";
		public static final String SEARCH = "Searching for \"%1$s\", %2$s queries found";
		public static final String FILTER = "Filtering for tasks \"%1$s\", %2$s queries found";
		public static final String LOAD_FAILED = "Load from file failed, %1$s";
		public static final String SAVE_FAILED = "Save to file failed, %1$s";
		public static final String MODIFY = "Modified %1$s";
		public static final String NEW_MODIFICATION = "Modify \"%1$s\" attribute \"%2$s\" contents \"%3$s\"";
		public static final String UI_LOOKANDFEEL_FAIL = "Could not set look and feel";
		public static final String PUSH_INVERSE_COMMAND = "Pushed inverse command \"%1$s\"";
		public static final String PUSH_ORDER = "Pushed inverse order \"%1$s\"";
		public static final String PUSH_INVERSE_TASK = "Pushed inverse task \"%1$s\"";
		public static final String POP_INVERSE_COMMAND = "Pop inverse command \"%1$s\"";
		public static final String POP_ORDER = "Pop inverse order \"%1$s\"";
		public static final String POP_INVERSE_TASK = "Pop inverse task \"%1$s\"";
		public static final String SET_DEADLINE = "Set deadline for \"%1$s\" on \"%2$s\"";
		public static final String SET_STARTTIME = "Set start time for \"%1$s\" on \"%2$s\"";
		public static final String REORDER_TASKS = "Reordered tasks %1$s";
		public static final String ADDED_TASK = "Added task \"%1$s\"";
		public static final String ADD_DUPLICATE_TASK = "Task name already present \"%1$s\"";
		public static final String RECOVERED_TASK = "Recovered task \"%1$s\"";
		public static final String CLEARED_TASKS = "Removed all tasks";
		public static final String UPDATE_TASK_NAME_DUPLICATE = "Task name not available \"%1$s\"";
		public static final String UPDATE_TASK_NAME = "Updated task name \"%1$s\" to \"%2$s\"";
		public static final String FILTERED_TASKS = "Filter tasks with criteria \"%1$s\", result: %2$s";
		public static final String SEARCHED_TASKS = "Searched tasks with keyphrase \"%1$s\", result: %2$s";
		public static final String PARSER_ERROR = "Could not understand your command, "
				+ "please refer to the manual for list of commands.\nDetails: %1$s";
		public static final String EMPTY_INPUT_ERROR = "Please enter a command!";
		public static final String UNEXPECTED_ERROR = "Opps! An unexpected error occured, please retry.\nDetails: %1$s";
	}

	public static class Commands {

		// Reverse commands (for undo)
		public static final String DISMISS = "dismiss %1$s";
		public static final String RECOVER = "recover %1$s";
		public static final String DESC = "desc %1$s %2$s";
		public static final String DUE = "due %1$s %2$s";
		public static final String START = "start %1$s %2$s";
		public static final String RANK = "rank %1$s %2$s";
		public static final String MARK_DONE = "mark %1$s done";
		public static final String MARK_NOT_DONE = "mark %1$s ongoing";
		public static final String DELETE = "delete %1$s";
		public static final String TAG = "tag %1$s %2$s";
		public static final String UNTAG = "untag %1$s %2$s";
		public static final String MODIFY = "modify %1$s desc %2$s tag %3$s due %4$s";
		public static final String VIEW = "view %1$s";
		public static final String UNDO = "undo";
		public static final String CLRSRC = "clrsrc";
		public static final String NAME = "name %1$s %2$s";
		public static final String SORT_ALPHA = "sort a a";
		public static final String SORT_DEADLINE = "sort t d";
		public static final String ADD_TASK = "add %1$s";
	}

	public static class Assertion {
		public static final String NULL_INPUT = "Null input value detected";
		public static final String NOT_CLEARED = "Task clearance failed";
		public static final String NOT_ADDED = "Task addition failed";
		public static final String NOT_DELETED = "Task deletion failed";
		public static final String NOT_EQUAL = "The two lists are not equal";
		public static final String NO_PREV_PAGE = "Cannot navigate backward from first page";
		public static final String NO_NEXT_PAGE = "Cannot navigate forward from last page";
		public static final String SIZE_DIFFERENT = "The size of the indices and tasks arrays are different";
		public static final String TOO_MANY_PAGES = "The requested page exceeds the total number of pages";
		public static final String TASK_DUPLICATE_TAG = "Duplicate tag";
		public static final String TASK_TAG_NOTFOUND = "Delete tag not found";
		public static final String TASK_TAG_NOTUPPERCASE = "Tag not in uppercase";
		public static final String EMPTY_INVERSE_COMMAND = "Empty inverse command";
		public static final String EMPTY_INVERSE_ORDER = "Empty inverse order";
		public static final String NULL_INVERSE_TASK = "Null inverse task";
	}

	public static class Message {
		public static final String WELCOME = "Welcome to Stream " + VERSION
				+ "!";
		public static final String THANK_YOU = "Thank you for using this internal release of Stream[BETA]!";

		public static final String TEXT_TITLE = "STREAM " + VERSION
				+ ": Simple Task Reader and Manager";
		public static final String TEXT_HEADER = "<html>S<font color=\"#ed7d31\">T</font>RE<font color=\"#ed7d31\">A</font>M</html>";
		public static final String TEXT_FOOTER = "Copyright \u00a9 2014 CS2013AUG2014-F10-01J. All rights reserved.";
		public static final String TEXT_INDEX = "#%1$s";

		public static final String DETAILS_HEADER = "Details for %1$s";
		public static final String DETAILS_CONTENT = "Task name: %1$s \nStatus: %2$s\nTiming: %3$s\nDescription: %4$s \nTags: %5$s\nRank: %6$s";
	}

	public static class ExceptionMessage {
		public static final String ERR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";
		public static final String ERR_TASK_ALREADY_EXISTS = "\"%1$s\" already exists in the tasks list.";
		public static final String ERR_TASK_DOES_NOT_EXIST = "The task \"%1$s\" does not exist.";
		public static final String ERR_NEW_TASK_NAME_NOT_AVAILABLE = "The name \"%1$s\" is not available.";
		public static final String ERR_CREATEFILE = "Test file could not be created "
				+ "- %1$s\nDelete the file if already present.";
		public static final String ERR_TASK_MODIFICATION = "Could not modify file: %1$s";
	}

	public static class UI {
		public static final Color COLOR_HEADER = Color.getHSBColor(
				(float) 208.52 / 360, (float) 0.5728, (float) 0.8353);
		public static final Color COLOR_LOG_MSG = Color.BLACK;
		public static final Color COLOR_ERR_MSG = Color.RED;

		public static Font FONT_TITLE;
		public static Font FONT_CONSOLE;
		public static Font FONT_LOGGER;
		public static Font FONT_FOOTER;
		public static Font FONT_INDEX;
		public static Font FONT_TASK;

		public static final Border MARGIN_MAINFRAME = BorderFactory
				.createEmptyBorder(20, 10, 20, 10);
		public static final Insets MARGIN_HEADER = new Insets(0, 0, 0, 0);
		public static final Insets MARGIN_FOOTER = new Insets(10, 10, 0, 10);
		public static final Insets MARGIN_ELEM = new Insets(10, 10, 0, 10);
		public static final Insets MARGIN_TASKVIEW = new Insets(0, 0, 0, 0);

		public static final int HEIGHT_MAINFRAME = 750;
		public static final int WIDTH_MAINFRAME = 750;

		public static final int MAX_VIEWABLE_TASK = 5;

		public static final int GRIDWIDTH_NAVIG = 1;
		public static final int GRIDWIDTH_BUTTON = GRIDWIDTH_NAVIG * 4;
		public static final int GRIDWIDTH_HEADER = GRIDWIDTH_BUTTON * 3;
		public static final int GRIDWIDTH_CONSOLE = GRIDWIDTH_BUTTON * 3;
		public static final int GRIDWIDTH_INPUT = GRIDWIDTH_NAVIG * 6;
		public static final int GRIDWIDTH_LOGGER = GRIDWIDTH_BUTTON * 3;
		public static final int GRIDWIDTH_FOOTER = GRIDWIDTH_BUTTON * 3;
		public static final int GRIDWIDTH_TASK = GRIDWIDTH_BUTTON * 3;

		public static final int GRIDHEIGHT_INDEX = 2;
		public static final int GRIDHEIGHT_TASKNAME = 1;
		public static final int GRIDHEIGHT_TIMING = 1;
		public static final int GRIDHEIGHT_DELETE_BTN = 1;
		public static final int GRIDHEIGHT_MARK_BTN = 1;

		public static final int GRIDX_HEADER = 0;
		public static final int GRIDX_UNDO = 0;
		public static final int GRIDX_FIRST = GRIDX_UNDO + GRIDWIDTH_BUTTON;
		public static final int GRIDX_PREV = GRIDX_FIRST + GRIDWIDTH_NAVIG;
		public static final int GRIDX_NEXT = GRIDX_PREV + GRIDWIDTH_NAVIG;
		public static final int GRIDX_LAST = GRIDX_NEXT + GRIDWIDTH_NAVIG;
		public static final int GRIDX_CLEAR = GRIDX_LAST + GRIDWIDTH_NAVIG;
		public static final int GRIDX_ADD_TASK_TEXTFIELD = 0;
		public static final int GRIDX_ADD_TASK_BTN = GRIDX_ADD_TASK_TEXTFIELD
				+ GRIDWIDTH_INPUT;
		public static final int GRIDX_SORT_DEADLINE = GRIDX_ADD_TASK_BTN
				+ GRIDWIDTH_NAVIG + 1;
		public static final int GRIDX_SORT_ALPHA = GRIDX_SORT_DEADLINE
				+ GRIDWIDTH_NAVIG;
		public static final int GRIDX_CONSOLE = 0;
		public static final int GRIDX_LOGGER = 0;
		public static final int GRIDX_FOOTER = 0;
		public static final int GRIDX_TASK = 0;
		public static final int GRIDX_INDEX = 0;
		public static final int GRIDX_TASKNAME = 1;
		public static final int GRIDX_TIMING = 1;
		public static final int GRIDX_DELETE_BTN = 2;
		public static final int GRIDX_MARK_BTN = 2;

		public static final int GRIDY_HEADER = 0;
		public static final int GRIDY_MENU = GRIDY_HEADER + 1;
		public static final int GRIDY_TASK_START = GRIDY_MENU + 1;
		public static final int GRIDY_BUTTON = GRIDY_TASK_START
				+ MAX_VIEWABLE_TASK + 1;
		public static final int GRIDY_CONSOLE = GRIDY_BUTTON + 1;
		public static final int GRIDY_LOGGER = GRIDY_CONSOLE + 1;
		public static final int GRIDY_FOOTER = GRIDY_LOGGER + 1;
		public static final int GRIDY_INDEX = 0;
		public static final int GRIDY_TASKNAME = 0;
		public static final int GRIDY_TIMING = 1;
		public static final int GRIDY_DELETE_BTN = 0;
		public static final int GRIDY_MARK_BTN = 1;

		public static final int IPADY_HEADER = 0;
		public static final int IPADY_BUTTON = 5;
		public static final int IPADY_CONSOLE = 10;
		public static final int IPADY_LOGGER = 10;
		public static final int IPADY_FOOTER = 0;
		public static final int IPADY_TASK = 0;

		public static final float WEIGHTX_INDEX = (float) 1.0 / 9;
		public static final float WEIGHTX_TASKNAME = (float) 7.0 / 9;
		public static final float WEIGHTX_TIMING = (float) 7.0 / 9;
		public static final float WEIGHTX_DELETE_BTN = (float) 1.0 / 9;
		public static final float WEIGHTX_MARK_BTN = (float) 1.0 / 9;

		public static final String BTN_FIRST = "<<";
		public static final String BTN_PREV = "<";
		public static final String BTN_NEXT = ">";
		public static final String BTN_LAST = ">>";
		public static final String BTN_SORT_ALPHA = "A";
		public static final String BTN_SORT_DEADLINE = "T";
		public static final String BTN_ADD_TASK = "+";
		public static final String BTN_CLEAR = "Clear search result";
		public static final String BTN_UNDO = "Undo last action";
		public static final String BTN_DELETE = "Delete";
		public static final String BTN_MARK_DONE = "Mark as done";
		public static final String BTN_MARK_NOT_DONE = "Mark as ongoing";

		public static void initFonts(InputStream titleFont,
				InputStream indexFont, InputStream consoleFont,
				InputStream loggerFont, InputStream footerFont,
				InputStream taskFont) {
			try {
				FONT_TITLE = Font.createFont(Font.TRUETYPE_FONT, titleFont)
						.deriveFont(Font.PLAIN, 100);
				FONT_INDEX = Font.createFont(Font.TRUETYPE_FONT, indexFont)
						.deriveFont(Font.PLAIN, 70);
				FONT_CONSOLE = Font.createFont(Font.TRUETYPE_FONT, consoleFont)
						.deriveFont(Font.PLAIN, 13);
				FONT_LOGGER = Font.createFont(Font.TRUETYPE_FONT, loggerFont)
						.deriveFont(Font.PLAIN, 13);
				FONT_FOOTER = Font.createFont(Font.TRUETYPE_FONT, footerFont)
						.deriveFont(Font.PLAIN, 12);
				FONT_TASK = Font.createFont(Font.TRUETYPE_FONT, taskFont)
						.deriveFont(Font.PLAIN, 13);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
