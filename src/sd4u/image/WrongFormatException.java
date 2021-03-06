package sd4u.image;

/**
 * This class detects wrong image format
 * @author H. Cetiner & Y.H. Kalayci
 */
public class WrongFormatException extends Exception {


	private static final long serialVersionUID = 1L;
	
	public WrongFormatException() { super(); }
	public WrongFormatException(String message) { super(message); }
	public WrongFormatException(String message, Throwable cause) { super(message, cause); }
	public WrongFormatException(Throwable cause) { super(cause); }

}
