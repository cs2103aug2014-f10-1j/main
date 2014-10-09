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
		ADD, UPDATE, DESC, DUE, DELETE, FIND, VIEW, MARK, UNDO,
		
		//if the input has no meaning to the software
		WRONG;
	}
	
	public static ParserContent interpretCommand(String command){
		String[] content=command.split(" ");
		return  executeCommand(content);
	}
	
	private static ParserContent executeCommand(String[] content){
		CommandType commandKey=determineKey(content[0].toLowerCase());
		
		switch(commandKey){
			case ADD:
			case UPDATE:
			case DESC:
			case DUE:
			case FIND:
			case MARK:
				return packedContent(commandKey, content);
			case DELETE:
			case VIEW:
			case UNDO:
				return singleContent(commandKey, content);
			default:
				return wrongContent();
		}
	}
	
	private static CommandType determineKey(String key){
		switch(key){
			case "add":
				return CommandType.ADD;
			case "update":
				return CommandType.UPDATE;
			case "desc":
			case "describe":
				return CommandType.DESC;
			case "due":
				return CommandType.DUE;
			case "delete":
				return CommandType.DELETE;
			case "find":
				return CommandType.FIND;
			case "view":
				return CommandType.VIEW;
			case "mark":
				return CommandType.MARK;
			case "undo":
				return CommandType.UNDO;
			default:
				return CommandType.WRONG;
		}
	}
	
	private static ParserContent packedContent(CommandType key, String[] content){
		ParserContent command=new ParserContent(key, content);
		return command;
	}
	
	private static ParserContent singleContent(CommandType key, String[] content){
		ParserContent command=new ParserContent(key, content);
		return command;
	}
	
	private static ParserContent wrongContent(){
		String[] invalidInput={"invalid"};
		ParserContent wrongMessage=new ParserContent(CommandType.WRONG, invalidInput);
		return wrongMessage;
	}
}
