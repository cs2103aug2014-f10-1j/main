package parser;
//import java.util.*;
/*
 * Parser is used to interpret the user input to a pack of 
 * information and later on pass it to the Logic part
 * 
 * Need to be modified later, since I just found out that the COMMAND_TYPE
 * may not be useful here (don't need to specify it)
 * 
 * @author: Jiang Shenhao
 * 
 */
public class Parser {

	public enum CommandType {
		ADD, DEL, DESC, VIEW, MODIFY, ERROR;
	}
	
	public static ParserContent interpretCommand(String command){
		String[] content=command.split(" ", 2);
		return executeCommand(content);
	}
	
	private static ParserContent executeCommand(String[] contents){
		CommandType commandKey;
		String key = contents[0];
		switch(key){
			case "add":
				commandKey = CommandType.ADD;
				return newContent(commandKey, contents);
			case "del":
			case "delete":
				commandKey = CommandType.DEL;
				return newContent(commandKey, contents);
			case "desc":
			case "describe":
				commandKey = CommandType.DESC;
				return newContent(commandKey, contents);
			case "view":
				commandKey = CommandType.VIEW;
				return newContent(commandKey, contents);
			case "mod":
			case "modify":
				commandKey = CommandType.MODIFY;
				return newContent(commandKey, contents);
			default:
				commandKey = CommandType.ERROR;
				return wrongContent();
		}
	}
	
	
	
	private static ParserContent newContent(CommandType key, String[] contents){
		ParserContent command = new ParserContent(key, contents);
		return command;
	}
	
	
	
	private static ParserContent wrongContent(){
		String[] invalidInput={"invalid"};
		ParserContent wrongMessage=new ParserContent(CommandType.ERROR, invalidInput);
		return wrongMessage;
	}
}
