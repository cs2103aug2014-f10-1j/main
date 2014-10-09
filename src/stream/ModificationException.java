package stream;

public class ModificationException extends Exception {
	private static final long serialVersionUID = 5826059852221730368L;

	public ModificationException() {
		super();
	}
	
	public ModificationException(String message) {
		super(message);
	}
	
	public ModificationException(String message, Throwable cause) {
		super(message, cause);
	}

}
