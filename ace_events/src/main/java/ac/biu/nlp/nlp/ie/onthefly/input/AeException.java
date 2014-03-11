package ac.biu.nlp.nlp.ie.onthefly.input;

public class AeException extends Exception {

	public AeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AeException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -3673947029197363900L;

	public AeException(String message) {
		super(message);
	}

}
