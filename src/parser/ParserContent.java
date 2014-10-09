package parser;

import parser.Parser.CommandType;

/*
 * Content of the parser to be used by the Logic Part
 * CommandKey is the identifier like "add" or "delete"
 * while commandContent is the additional info
 * 
 * @author: Jiang Shenhao
 */
public class ParserContent {
	private CommandType commandKey;
	private String commandContent;
	
	public ParserContent(CommandType commandKey, String[] command){
		this.commandKey=commandKey;
		if(command.length>1){
			for(int i=1;i<command.length;i++){
				this.commandContent+=command[i];
				this.commandContent+=" ";
			}
			this.commandContent.trim();
		}
		else{
			this.commandContent="";
		}
	}
	
	public CommandType getCommandKey(){
		return this.commandKey;
	}
	
	public String getCommandContent(){
		return this.commandContent;
	}

}
