package fileio;

//@author A0096529N

/**
 * @author Steven
 *
 */
public class StreamIOException extends Exception {
	private static final long serialVersionUID = -824071103853366825L;

	public StreamIOException(String message) {
		super(message);
	}
	public StreamIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
