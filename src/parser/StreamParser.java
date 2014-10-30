package parser;

import exception.StreamParserException;
import model.StreamObject;

/*
 * Parser is used to interpret the user input to a pack of 
 * information and later on pass it to the Logic part
 * 
 * Need to be modified later, since I just found out that the COMMAND_TYPE
 * may not be useful here (don't need to specify it)
 * 
 * @author: A0119401U
 * 
 */
public class StreamParser {

	public enum CommandType {
		INIT, ADD, DEL, DESC, DUE, VIEW, RANK, MODIFY, NAME, MARK, TAG, UNTAG, SEARCH, SORT, FILTER, CLRSRC, CLEAR, UNDO, EXIT, ERROR, RECOVER, DISMISS;
	}

	private CommandType commandKey;
	private String commandContent;

	public StreamParser() {
		this.commandKey = CommandType.INIT;
		this.commandContent = null;
	}

	public void interpretCommand(String input, StreamObject stobj)
			throws StreamParserException {
		String[] contents = input.trim().split(" ", 2);
		String key = contents[0].toLowerCase();
		int numOfTasks = stobj.getNumberOfTasks();
		switch (key) {
		case "add":

			if (contents.length < 2) {
				throw new StreamParserException("Nothing to add!");
			}

			this.commandKey = CommandType.ADD;
			break;

		case "del":
		case "delete":

			if (contents.length != 2) {
				throw new StreamParserException("Invalid input!");
			}

			else if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}

			this.commandKey = CommandType.DEL;
			break;
		case "desc":
		case "describe":
			contents = input.trim().split(" ", 3);

			if (contents.length < 3) {
				throw new StreamParserException("Not enough information!");
			}

			else if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}
			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.DESC;
			break;
		case "due":
		case "by":

			/*
			 * The exception for this one will be implemented after the
			 * 'multiple-commands-in-one-line' feature.
			 */

			this.commandKey = CommandType.DUE;
			break;
		case "view":
			this.commandKey = CommandType.VIEW;
			break;
		case "rank":

			contents = input.trim().split(" ", 3);

			if (contents.length != 3) {
				throw new StreamParserException("Not enough information!");
			}

			else if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}

			else if (!checkRanking(contents[2])) {
				throw new StreamParserException("Invalid input rank!");
			}
			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.RANK;
			break;
		case "mod":
		case "modify":
			contents = input.trim().split(" ", 3);

			if (contents.length < 3) {
				throw new StreamParserException("Not enough information!");
			}

			else if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}

			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.MODIFY;
			break;
		case "name":
			contents = input.trim().split(" ", 3);

			if (contents.length < 3) {
				throw new StreamParserException("Not enough information!");
			}

			else if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}

			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.NAME;
			break;
		case "mark":
		case "done":
		case "finished":
			contents = input.trim().split(" ");
			if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}
			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.MARK;
			break;
		case "tag":
			contents = input.trim().split(" ", 3);

			if (contents.length < 3) {
				throw new StreamParserException("Not enough information!");
			}

			else if (!isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}

			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.TAG;
			break;
		case "untag":
			contents = input.trim().split(" ", 3);
			if (contents.length < 3 || !isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			else if (!withinRange(Integer.parseInt(contents[1]), numOfTasks)) {
				throw new StreamParserException("Out of range!");
			}
			contents = input.trim().split(" ", 2);

			this.commandKey = CommandType.UNTAG;
			break;
		case "search":
		case "find":
			if (contents.length < 2) {
				throw new StreamParserException("Nothing to search!");
			}

			this.commandKey = CommandType.SEARCH;
			break;
		case "sort":
			this.commandKey = CommandType.SORT;
			break;
		case "filter":
			if (contents.length < 2) {
				throw new StreamParserException(
						"Please specify filter criteria!");
			}
			if (!isValidFilterType(contents[1].trim())) {
				throw new StreamParserException("Invalid filter type!");
			}

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

			if (contents.length != 2 || !isInteger(contents[1])) {
				throw new StreamParserException("Invalid index!");
			}

			this.commandKey = CommandType.DISMISS;
			break;
		case "exit":
			this.commandKey = CommandType.EXIT;
			break;
		default:
			throw new StreamParserException("Unknown command type");
		}
		this.commandContent = executeCommand(contents);
	}

	private static String executeCommand(String[] contents) {
		String content = null;
		if (contents.length > 1) {
			content = contents[1];
		} else {
			content = "";
		}

		return content;
	}

	public CommandType getCommandType() {
		return this.commandKey;
	}

	public String getCommandContent() {
		return this.commandContent;
	}

	private boolean isInteger(String str) {
		int size = str.length();
		for (int i = 0; i < size; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return size > 0;
	}

	private boolean withinRange(int index, int numOfTasks) {
		return index >= 1 && index <= numOfTasks;
	}

	private boolean checkRanking(String rankInput) {
		String stdRank = rankInput.toLowerCase();
		return stdRank.equals("high") || stdRank.equals("medium")
				|| stdRank.equals("low");
	}

	// @author A0093874N

	private boolean isValidFilterType(String type) {
		if ((type.equals("done")) || (type.equals("ongoing")) || (type.equals("notime"))) {
			return true;
		} else {
			String[] types = type.split(" ");
			if ((types[0].equals("before")) || (types[0].equals("after"))){
				return types.length > 1;				
			} else {
				return false;
			}
		}
	}

}
