package parser;

import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;
import exception.StreamParserException;

// @author A0119401U
/**
 * Parser is used to interpret the user input to a pack of 
 * information and later on pass it to the Logic part
 * 
 * @version V0.4
 * @author Jiang Shenhao
 */
public class StreamParser {

	public enum CommandType {
		INIT, ADD, DEL, DESC, DUE, START, VIEW, RANK, MODIFY, NAME, MARK, TAG, UNTAG, SEARCH, SORT, 
		UNSORT, FILTER, CLRSRC, CLEAR, UNDO, EXIT, ERROR, RECOVER, DISMISS, FIRST, PREV, NEXT, LAST;
	}

	private CommandType commandKey;
	private String commandContent;

	private static final StreamLogger logger = StreamLogger
			.init(StreamConstants.ComponentTag.STREAMPARSER);

	public StreamParser() {
		this.commandKey = CommandType.INIT;
		this.commandContent = null;
	}

	public void interpretCommand(String input, int numOfTasks)
			throws StreamParserException {
		if (input.isEmpty()) {
			throw new StreamParserException("Empty Input");
		}
		String[] contents = input.trim().split(" ", 2);
		String key = contents[0].toLowerCase();
		switch (key) {
			case "add":

				checkAddValidity(contents);

				this.commandKey = CommandType.ADD;
				break;

			case "del":
			case "delete":

				checkDeleteValidity(contents, numOfTasks);

				this.commandKey = CommandType.DEL;
				break;
			case "desc":
			case "describe":
				contents = input.trim().split(" ", 3);

				/*
				 * Check Multi Validity is a method which can be used to check
				 * the validity of a user input for several kinds of commands
				 * 
				 * Supported commands can be seen below
				 */
				checkMultiValidity(contents, numOfTasks);
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.DESC;
				break;
			case "due":
				
				
				contents = input.trim().split(" ");
				
				checkDateValidity(contents, numOfTasks);
				
				contents = input.trim().split(" ",2);

				this.commandKey = CommandType.DUE;
				break;
			case "start":
				
				contents = input.trim().split(" ");
				
				checkDateValidity(contents, numOfTasks);
				
				contents = input.trim().split(" ",2);
				
				this.commandKey = CommandType.START;
				break;
			case "view":
				
				contents = input.split(" ");
				
				checkViewValidity(contents, numOfTasks);
				
				contents = input.split(" ",2);
				
				this.commandKey = CommandType.VIEW;
				break;
			case "rank":

				contents = input.trim().split(" ", 3);

				checkRankValidity(contents, numOfTasks);
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.RANK;
				break;
			case "mod":
			case "modify":
				contents = input.trim().split(" ", 3);

				checkMultiValidity(contents, numOfTasks);

				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.MODIFY;
				break;
			case "name":
				contents = input.trim().split(" ", 3);

				checkMultiValidity(contents, numOfTasks);

				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.NAME;
				break;
			case "mark":
			case "done":
			case "finished":
				contents = input.trim().split(" ");
				checkFinishedValidity(contents, numOfTasks);
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.MARK;
				break;
			case "tag":
				contents = input.trim().split(" ", 3);

				checkMultiValidity(contents, numOfTasks);

				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.TAG;
				break;
			case "untag":
				contents = input.trim().split(" ", 3);
				checkUntaggingValidity(contents, numOfTasks);
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.UNTAG;
				break;
			case "search":
			case "find":
				validateLength(contents);

				this.commandKey = CommandType.SEARCH;
				break;
			case "sort":
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

				checkDismissValidity(contents);

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
			default:
				logger.log(LogLevel.DEBUG, "Input cannot be interpreted.");
				throw new StreamParserException("Unknown command type.");

		}
		this.commandContent = executeCommand(contents);
	}

	private void checkDismissValidity(String[] contents)
			throws StreamParserException {
		if (contents.length != 2 || !StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		}
	}

	private void checkFilterValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException(
					"Please specify filter criteria!");
		}
		if (!isValidFilterType(contents[1].trim())) {
			throw new StreamParserException("Please enter a valid filter type!");
		}
	}

	private void validateLength(String[] contents) throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException("Nothing to search!");
		}
	}

	private void checkUntaggingValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3 || !StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		}

		else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException("The index you entered is out of range!");
		}
	}

	private void checkFinishedValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (!StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		}

		else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException("The index you entered is out of range!");
		}
	}

	private void checkRankValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length != 3) {
			throw new StreamParserException("Please provide more information!");
		}

		else if (!StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		}

		else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException("The index you entered is out of range!");
		}

		else if (!checkRanking(contents[2])) {
			throw new StreamParserException("Please enter a valid input rank!");
		}
	}
	
	private void checkDateValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3) {
			throw new StreamParserException("Please provide more information!");
		}
		
		else if (!StreamUtil.isInteger(contents[1])) {
			throw new StreamParserException("Please enter a valid index!");
		}
		
		else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException("The index you entered is out of range!");
		}
	}
	
	private void checkViewValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length != 2) {
			throw new StreamParserException("Please enter a vlid command!");
		}
		else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
			throw new StreamParserException("The index you entered is out of range!");
		}
	}
	
	
	
	
	/*
	 * This multi validity checking is able to validate 
	 * the following input commands:
	 * 1. Describe
	 * 2. Modify
	 * 3. Renaming
	 * 4. Tagging
	 */

	private void checkMultiValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length < 3) {
			throw new StreamParserException("Please provide more information!");
		} else
			checkFinishedValidity(contents, numOfTasks);
	}

	private void checkDeleteValidity(String[] contents, int numOfTasks)
			throws StreamParserException {
		if (contents.length != 2) {
			throw new StreamParserException("Please enter a valid command!");
		} else
			checkFinishedValidity(contents, numOfTasks);
	}

	private void checkAddValidity(String[] contents)
			throws StreamParserException {
		if (contents.length < 2) {
			throw new StreamParserException("Nothing to add!");
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


	private boolean withinRange(int index, int numOfTasks) {
		return index >= 1 && index <= numOfTasks;
	}

	private boolean checkRanking(String rankInput) {
		String stdRank = rankInput.toLowerCase();
		return stdRank.equals("high") || stdRank.equals("medium")
				|| stdRank.equals("low") || stdRank.equals("h")
				|| stdRank.equals("m") || stdRank.equals("l");
	}

	// @author A0093874N

	private boolean isValidFilterType(String type) {
		if ((type.equals("done")) || (type.equals("ongoing"))
				|| (type.equals("notime"))) {
			return true;
		} else {
			String[] types = type.split(" ");
			if ((types[0].equals("before")) || (types[0].equals("after"))) {
				return types.length > 1;
			} else {
				return false;
			}
		}
	}

}
