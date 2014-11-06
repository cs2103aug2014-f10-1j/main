package parser;

import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;
import exception.StreamParserException;

import com.mdimension.jchronic.Chronic;

// @author A0119401U
/**
 * Parser is used to interpret the user input to a pack of information and later
 * on pass it to the Logic part
 * 
 * @version V0.4
 * @author Jiang Shenhao
 */
public class StreamParser {

	public enum CommandType {
		INIT, ADD, DEL, DESC, DUE, START, VIEW, RANK, MODIFY, NAME, MARK,
		TAG, UNTAG, SEARCH, SORT, UNSORT, FILTER, CLRSRC, CLEAR, UNDO, EXIT,
		ERROR, RECOVER, DISMISS, FIRST, PREV, NEXT, LAST, PAGE;
	}

	public enum MarkType {
		DONE, NOT, NULL;
	}

	public enum RankType {
		HI, MED, LO, NULL;
	}

	public enum SortType {
		ALPHA, START, END, NULL;
	}

	public enum FilterType {
		DONE, NOT, HIRANK, MEDRANK, LORANK, DUEON, DUEBEF,
		DUEAFT, STARTON, STARTBEF, STARTAFT, NOTIMING,
		DEADLINED, EVENT, NULL;
	}

	private CommandType commandKey;
	private String commandContent;

	private static final StreamLogger logger = StreamLogger
			.init(StreamConstants.ComponentTag.STREAMPARSER);

	static final String ERROR_INCOMPLETE_INPUT = "Please provide more information!";
	static final String ERROR_INCOMPLETE_INDEX = "Please provide the index number!";
	static final String ERROR_INVALID_INDEX = "Please provide a valid index number!";
	static final String ERROR_INVALID_FILTER = "Please enter a valid filter type!";
	static final String ERROR_INVALID_SORT = "Please enter a valid sorting type!";
	static final String ERROR_INVALID_MARK = "Please enter a valid marking type!";
	static final String ERROR_INVALID_RANK = "Please enter a valid input rank!";
	static final String ERROR_EMPTY_INPUT = "Empty input detected!";
	static final String ERROR_INDEX_OUT_OF_BOUNDS = "The index you entered is out of range!";
	static final String ERROR_DATE_NOT_PARSEABLE = "Date cannot be understood!";
	static final String ERROR_UNKNOWN_COMMAND = "Unknown command type!";

	private static final int PARAM_POS_KEYWORD = 0;
	private static final int PARAM_POS_INDEX = 1;
	private static final int PARAM_POS_CONTENT = 2;
	private static final int PARAM_POS_FILTERTYPE = 1;
	private static final int PARAM_POS_SORTTYPE = 1;
	private static final int PARAM_POS_SORTORDER = 2;

	public StreamParser() {
		this.commandKey = CommandType.INIT;
		this.commandContent = null;
	}

	public void interpretCommand(String input, int numOfTasks)
			throws StreamParserException {
		if (input.isEmpty()) {
			throw new StreamParserException(ERROR_EMPTY_INPUT);
		}
		String[] contents = input.trim().split(" ", 2);
		String[] contentsSplitWithIndex = input.trim().split(" ", 3);
		String key = contents[PARAM_POS_KEYWORD].toLowerCase();
		switch (key) {
		case "add":
			checkTypeOneValidity(contents);
			this.commandKey = CommandType.ADD;
			break;

		case "del":
		case "delete":
			checkTypeTwoValidity(contents, numOfTasks);
			this.commandKey = CommandType.DEL;
			break;

		case "desc":
		case "describe":
			checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.DESC;
			break;

		case "due":
			checkTypeFourValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.DUE;
			break;

		case "start":
			checkTypeFourValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.START;
			break;

		case "view":
			checkTypeTwoValidity(contents, numOfTasks);
			this.commandKey = CommandType.VIEW;
			break;

		case "rank":
			checkRankValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.RANK;
			break;

		case "mod":
		case "modify":
			checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.MODIFY;
			break;

		case "name":
			checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.NAME;
			break;

		case "mark":
		case "done":
		case "finished":
			checkMarkValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.MARK;
			break;

		case "tag":
			checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.TAG;
			break;

		case "untag":
			checkTypeThreeValidity(contentsSplitWithIndex, numOfTasks);
			this.commandKey = CommandType.UNTAG;
			break;

		case "search":
		case "find":
			checkTypeOneValidity(contents);
			this.commandKey = CommandType.SEARCH;
			break;

		case "sort":
			checkSortValidity(contentsSplitWithIndex);
			this.commandKey = CommandType.SORT;
			break;

		case "unsort":
			this.commandKey = CommandType.UNSORT;
			break;

		case "filter":
			checkFilterValidity(contents);
			this.commandKey = CommandType.FILTER;
			break;

		case "clrsrc":
			this.commandKey = CommandType.CLRSRC;
			break;

		case "clear":
		case "clr":
			this.commandKey = CommandType.CLEAR;
			break;

		case "undo":
			this.commandKey = CommandType.UNDO;
			break;

		case "recover":
			this.commandKey = CommandType.RECOVER;
			break;

		case "dismiss":
			this.commandKey = CommandType.DISMISS;
			break;

		case "exit":
			this.commandKey = CommandType.EXIT;
			break;

		case "first":
			this.commandKey = CommandType.FIRST;
			break;

		case "prev":
		case "previous":
			this.commandKey = CommandType.PREV;
			break;

		case "next":
			this.commandKey = CommandType.NEXT;
			break;

		case "last":
			this.commandKey = CommandType.LAST;
			break;

		case "page":
			this.commandKey = CommandType.PAGE;
			break;

		default:
			logger.log(LogLevel.DEBUG, "Input cannot be interpreted.");
			throw new StreamParserException(ERROR_UNKNOWN_COMMAND);

		}
		this.commandContent = executeCommand(contents);
	}

	private void checkIndexValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (!StreamUtil.isInteger(contents[PARAM_POS_INDEX])) {
			throw new StreamParserException(ERROR_INVALID_INDEX);
		} else if (!isWithinRange(Integer.parseInt(contents[PARAM_POS_INDEX]), numOfTasks)) {
			throw new StreamParserException(
					ERROR_INDEX_OUT_OF_BOUNDS);
		}
	}

	/**
	 * Type one command: commands with format (CommandWord) (String arguments of
	 * any length)
	 */
	private void checkTypeOneValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		}
	}

	/**
	 * Type two command: commands with format (CommandWord) (index number)
	 */
	private void checkTypeTwoValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException(ERROR_INCOMPLETE_INDEX);
		} else {
			checkIndexValidity(contents, numOfTasks);
		}
	}

	/**
	 * Type three command: commands with format (CommandWord) (index number)
	 * (String arguments of any length)
	 */
	private void checkTypeThreeValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
		}
	}

	/**
	 * Type four command: commands with format (CommandWord) (index number)
	 * (date String to be parsed)
	 */
	private void checkTypeFourValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			if (!isParseableDate(contents[PARAM_POS_CONTENT])) {
				throw new StreamParserException(ERROR_DATE_NOT_PARSEABLE);
			}
		}
	}

	private void checkRankValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			if (!checkRanking(contents[PARAM_POS_CONTENT])) {
				throw new StreamParserException(ERROR_INVALID_RANK);
			}
		}
	}

	private void checkMarkValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else {
			checkIndexValidity(contents, numOfTasks);
			if (!checkMarking(contents[PARAM_POS_CONTENT])) {
				throw new StreamParserException(ERROR_INVALID_MARK);
			}
		}
	}

	private void checkFilterValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else if (!checkFilter(contents[PARAM_POS_FILTERTYPE].trim())) {
			throw new StreamParserException(ERROR_INVALID_FILTER);
		}
	}

	private static String executeCommand(String[] contents) {
		String content = null;
		if (contents.length > 1) {
			content = contents[1];
			logger.log(LogLevel.DEBUG, "Command consists of multiple words.");
		} else {
			content = "";
			logger.log(LogLevel.DEBUG, "No content for the command.");
		}

		return content;
	}

	public CommandType getCommandType() {
		return this.commandKey;
	}

	public String getCommandContent() {
		return this.commandContent;
	}

	// @author A0093874N

	public static SortType parseSorting(String sortType) {
		switch (sortType) {
		case "d":
		case "due":
		case "deadline":
		case "end":
		case "endtime":
			return SortType.END;
		case "s":
		case "start":
		case "begin":
		case "starttime":
			return SortType.START;
		case "a":
		case "alpha":
		case "alphabetical":
		case "alphabetically":
			return SortType.ALPHA;
		default:
			return SortType.NULL;
		}
	}

	public static boolean getSortingOrder(String order) {
		switch (order) {
		case "":
		case "a":
		case "asc":
		case "ascending":
			return false;
		case "d":
		case "desc":
		case "descending":
			return true;
		default:
			return false;
		}
	}

	public static RankType parseRanking(String rankInput) {
		switch (rankInput) {
		case "high":
		case "hi":
		case "h":
			return RankType.HI;
		case "medium":
		case "med":
		case "m":
			return RankType.MED;
		case "low":
		case "l":
			return RankType.LO;
		default:
			return RankType.NULL;
		}		
	}

	public static String translateRanking(RankType parsedRank) {
		switch (parsedRank) {
		case HI:
			return "high";
		case MED:
			return "medium";
		case LO:
			return "low";
		default:
			return null;
		}
	}

	public static MarkType parseMarking(String markInput) {
		switch (markInput) {
		case "done":
		case "finished":
		case "over":
			return MarkType.DONE;
		case "not done":
		case "not finished":
		case "ongoing":
			return MarkType.NOT;
		default:
			return MarkType.NULL;
		}		
	}

	public static MarkType parseMarking(Boolean isDone) {
		if (isDone) {
			return MarkType.DONE;
		} else {
			return MarkType.NOT;
		}
	}

	public static String translateMarking(MarkType parsedMark) {
		switch (parsedMark) {
		case DONE:
			return "done";
		case NOT:
			return "ongoing";
		default:
			return null;
		}
	}

	public static FilterType parseFilterType(String filterInput) {
		String[] contents = filterInput.split(" ", 2);
		MarkType parsedMark = parseMarking(contents[0]);
		switch (parsedMark) {
		case DONE:
			return FilterType.DONE;
		case NOT:
			return FilterType.NOT;
		default:
		}
		if (contents[0].equals("rank")) {
			if (contents.length == 2) {
				RankType parsedRank = parseRanking(contents[1]);
				switch (parsedRank) {
				case HI:
					return FilterType.HIRANK;
				case MED:
					return FilterType.MEDRANK;
				case LO:
					return FilterType.LORANK;
				default:
					return FilterType.NULL;
				}
			} else {
				return FilterType.NULL;
			}
		} else {
			contents = filterInput.split(" ", 3);
			if (contents.length == 3 && isParseableDate(contents[2])) {
				switch (contents[0] + " " + contents[1]) {
				case "due before":
					return FilterType.DUEBEF;
				case "due after":
					return FilterType.DUEAFT;
				case "start before":
					return FilterType.STARTBEF;
				case "start after":
					return FilterType.STARTAFT;
				default:
					return FilterType.NULL;
				}
			} else {
				contents = filterInput.split(" ", 2);
				if (contents.length == 2 && isParseableDate(contents[1])) {
					switch (contents[0]) {
					case "due":
						return FilterType.DUEON;
					case "start":
						return FilterType.STARTON;
					default:
						return FilterType.NULL;
					}
				} else if (contents.length == 2) {
					switch (contents[0] + " " + contents[1]) {
					case "no timing":
						return FilterType.NOTIMING;
					case "has deadline":
					case "deadlined":
						return FilterType.DEADLINED;
					case "event":
					case "timed":
						return FilterType.EVENT;
					default:
						return FilterType.NULL;
					}
				} else {
					return FilterType.NULL;
				}
			}
		}
	}

	private void checkSortValidity(String[] contents)
			throws StreamParserException {
		String order = contents.length > 2 ? contents[PARAM_POS_SORTORDER] : "";
		
		if (contents.length < 2) {
			throw new StreamParserException(ERROR_INCOMPLETE_INPUT);
		} else if (!checkSort(contents[PARAM_POS_SORTTYPE], order)) {
			throw new StreamParserException(ERROR_INVALID_SORT);
		}
	}

	private boolean checkRanking(String rankInput) {
		RankType parsedRank = parseRanking(rankInput);
		switch (parsedRank) {
		case NULL:
			return false;
		default:
			return true;
		}
	}

	private boolean checkMarking(String markInput) {
		MarkType parsedMark = parseMarking(markInput);
		switch (parsedMark) {
		case NULL:
			return false;
		default:
			return true;
		}
	}

	private boolean checkFilter(String type) {
		FilterType parsedFilter = parseFilterType(type);
		switch (parsedFilter) {
		case NULL:
			return false;
		default:
			return true;
		}
	}

	private boolean checkSort(String sortBy, String order) {
		switch (parseSorting(sortBy)) {
		case START:
		case END:
		case ALPHA:
			switch (order) {
			case "":
			case "d":
			case "desc":
			case "descending":
			case "a":
			case "asc":
			case "ascending":
				return true;
			default:
				return false;
			}
		default:
			return false;
		}
	}

	private static boolean isParseableDate(String date) {
		try {
			Chronic.parse(date).getBeginCalendar();
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}

	// @author A0119401U

	private boolean isWithinRange(int index, int numOfTasks) {
		return index >= 1 && index <= numOfTasks;
	}

	/**
	 * @deprecated unnecessary since dismiss is not accessible by user
	 */
	@SuppressWarnings("unused")
	private void checkDismissValidity(String[] contents)
			throws StreamParserException {
		if (contents.length != 2 || !StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		}
	}

	/**
	 * @deprecated use checkTypeOneValidity
	 */
	@SuppressWarnings("unused")
	private void checkSearchValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException("Nothing to search!");
		}
	}

	/**
	 * @deprecated use checkTypeThreeValidity instead
	 */
	@SuppressWarnings("unused")
	private void checkUntaggingValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3 || !StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		} else if (!isWithinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException(
					"The index you entered is out of range!");
		}
	}

	/**
	 * @deprecated use checkTypeTwoValidity
	 */
	@SuppressWarnings("unused")
	private void checkViewValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length != 2) {
			throw new StreamParserException("Please enter a vlid command!");
		} else if (!isWithinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException(
					"The index you entered is out of range!");
		}
	}

}
