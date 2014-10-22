package parser;

import exception.StreamParserException;

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
		INIT, ADD, DEL, DESC, DUE, VIEW, MODIFY, REMODIFY, MARK, TAG, UNTAG, SEARCH, CLRSRC, CLEAR, UNDO, EXIT, ERROR, RECOVER, DISMISS;
	}

	private CommandType commandKey;
	private String commandContent;

	public StreamParser() {
		this.commandKey = CommandType.INIT;
		this.commandContent = null;
	}

	public void interpretCommand(String input) throws StreamParserException {
		String[] contents = input.trim().split(" ", 2);
		String key = contents[0].toLowerCase();
		switch (key) {
			case "add":

				if (contents.length < 2) {
					throw new StreamParserException("Nothing to add!");
				}

				this.commandKey = CommandType.ADD;
				break;

			case "del":
			case "delete":

				if (contents.length != 2 || !isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
				}

				this.commandKey = CommandType.DEL;
				break;
			case "desc":
			case "describe":
				contents = input.trim().split(" ", 3);
				if (contents.length != 3 || !isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
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
			case "mod":
			case "modify":
				contents = input.trim().split(" ", 3);
				if (contents.length != 3 || !isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
				}
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.MODIFY;
				break;
			case "remodify":
				contents = input.trim().split(" ", 3);
				if (contents.length != 3 || !isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
				}
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.REMODIFY;
				break;
			case "mark":
			case "done":
			case "finished":
				contents = input.trim().split(" ", 3);
				if (!isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
				}
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.MARK;
				break;
			case "tag":
				contents = input.trim().split(" ", 3);
				if (contents.length < 3 || !isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
				}
				contents = input.trim().split(" ", 2);

				this.commandKey = CommandType.TAG;
				break;
			case "untag":
				contents = input.trim().split(" ", 3);
				if (contents.length < 3 || !isInteger(contents[1])) {
					throw new StreamParserException("Invalid index!");
				}

				this.commandKey = CommandType.UNTAG;
				break;
			case "search":
			case "find":
			case "filter":

				if (contents.length < 2) {
					throw new StreamParserException("Nothing to search!");
				}

				this.commandKey = CommandType.SEARCH;
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

}
