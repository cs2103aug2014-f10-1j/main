package exception;

//@author A0119401U

public class StreamParserException extends Exception{

	private static final long serialVersionUID = 2454506083919846547L;
	
	public StreamParserException(String message){
		super(message);
	}
	
	public StreamParserException(String message, Throwable cause){
		super(message,cause);
	}

}
