package parser;

import parser.StreamParser.CommandType;

/*
 * Content of the parser to be used by the Logic Part
 * CommandKey is the identifier like "add" or "delete"
 * while commandContent is the additional info
 * 
 * @author: Jiang Shenhao
 */
public class StreamParserContent {
	private CommandType commandKey;
	private String commandContent;

	public StreamParserContent(CommandType commandKey, String[] command) {
		this.commandKey = commandKey;
		if (command.length > 1) {
			this.commandContent = command[1].trim();
		} else {
			this.commandContent = "";
		}
	}

	public CommandType getCommandKey() {
		return this.commandKey;
	}

	public String getCommandContent() {
		return this.commandContent;
	}

}
