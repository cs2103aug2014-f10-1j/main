package parser;

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
		INIT, ADD, DEL, DESC, DUE, VIEW, MODIFY, MARK, TAG, UNTAG, CLEAR, UNDO, EXIT, ERROR, RECOVER, DISMISS;
	}

	private CommandType commandKey;
	private String commandContent;

	public StreamParser() {
		this.commandKey = CommandType.INIT;
		this.commandContent = null;
	}

	public void interpretCommand(String input) {
		String[] contents = input.trim().split(" ", 2);
		String key = contents[0].toLowerCase();
		switch (key) {
			case "add":
				this.commandKey = CommandType.ADD;
				break;
			case "del":
			case "delete":
				this.commandKey = CommandType.DEL;
				break;
			case "desc":
			case "describe":
				this.commandKey = CommandType.DESC;
				break;
			case "due":
			case "by":
				this.commandKey = CommandType.DUE;
				break;
			case "view":
				this.commandKey = CommandType.VIEW;
				break;
			case "mod":
			case "modify":
				this.commandKey = CommandType.MODIFY;
				break;
			case "mark":
			case "done":
			case "finished":
				this.commandKey = CommandType.MARK;
				break;
			case "tag":
				this.commandKey = CommandType.TAG;
				break;
			case "untag":
				this.commandKey = CommandType.UNTAG;
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
			default:
				this.commandKey = CommandType.ERROR;
				break;
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

}
