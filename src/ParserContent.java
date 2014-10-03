
/*
 * Content of the parser to be used by the Logic Part
 * CommandKey is the identifier like "add" or "delete"
 * while commandContent is the additional info
 * 
 * @author: A0119401U
 */
public class ParserContent {
	private String commandKey;
	private String commandContent;
	
	public ParserContent(String[] command){
		this.commandKey=command[0];
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
	
	public String getCommandKey(){
		return this.commandKey;
	}
	
	public String getCommandContent(){
		return this.commandKey;
	}

}
