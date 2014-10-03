//import java.util.*;
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
public class Parser {

	enum COMMAND_TYPE{
		ADD, UPDATE, DESC, DUE, DELETE, FIND, VIEW, MARK, UNDO,
		
		//if the input has no meaning to the software
		WRONG;
	}
	
	public static ParserContent interpretCommand(String command){
		String[] content=command.split(" ");
		return  executeCommand(content);
	}
	
	private static ParserContent executeCommand(String[] content){
		COMMAND_TYPE commandKey=determineKey(content[0].toLowerCase());
		
		switch(commandKey){
			case ADD:
			case UPDATE:
			case DESC:
			case DUE:
			case FIND:
			case MARK:
				return packedContent(content);
			case DELETE:
			case VIEW:
			case UNDO:
				return singleContent(content);
			default:
				return wrongContent();
		}
	}
	
	private static COMMAND_TYPE determineKey(String key){
		switch(key){
			case "add":
				return COMMAND_TYPE.ADD;
			case "update":
				return COMMAND_TYPE.UPDATE;
			case "desc":
			case "describe":
				return COMMAND_TYPE.DESC;
			case "due":
				return COMMAND_TYPE.DUE;
			case "delete":
				return COMMAND_TYPE.DELETE;
			case "find":
				return COMMAND_TYPE.FIND;
			case "view":
				return COMMAND_TYPE.VIEW;
			case "mark":
				return COMMAND_TYPE.MARK;
			case "undo":
				return COMMAND_TYPE.UNDO;
			default:
				return COMMAND_TYPE.WRONG;
		}
	}
	
	private static ParserContent packedContent(String[] content){
		ParserContent command=new ParserContent(content);
		return command;
	}
	
	private static ParserContent singleContent(String[] content){
		ParserContent command=new ParserContent(content);
		return command;
	}
	
	private static ParserContent wrongContent(){
		String[] invalidInput={"invalid"};
		ParserContent wrongMessage=new ParserContent(invalidInput);
		return wrongMessage;
	}
}
