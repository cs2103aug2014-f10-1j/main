package parser;

//@author A0119401U
/*
 * Once initialized, a new Parser object is created
 * and it is used to interpret the user input to pack into
 * the attributes of the class and later on pass it to the Logic part
 * 
 * 
 * @author: A0119401U
 * 
 */
public class Parser {

	public enum CommandType {
		INIT, ADD, DEL, DESC, DUE, VIEW, MODIFY, MARK, CLEAR, UNDO, EXIT, ERROR, RECOVER;
	}

	private CommandType commandKey;
	private String commandContent;
	
	public Parser(){
		this.commandKey = CommandType.INIT;
		this.commandContent = null;
	}
	
	public void interpretCommand(String input) {
		String[] contents = input.trim().toLowerCase().split(" ",2);
		String key = contents[0];
		switch(key) {
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
			case "exit":
				this.commandKey = CommandType.EXIT;
				break;
			default:
				this.commandKey = CommandType.ERROR;
				break;
		}
		this.commandContent = executeCommand(contents);
	}
	
	private static String executeCommand(String[] contents){
		String content = null;
		if(contents.length>1){
			content = contents[1];
		}
		else{
			content = "";
		}
		
		return content;
	}
	
	public CommandType getCommandType(){
		return this.commandKey;
	}
	
	public String getCommandContent(){
		return this.commandContent;
	}
	
}
