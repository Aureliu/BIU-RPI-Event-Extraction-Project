package edu.cuny.qc.scorer;

public class DeriverException extends Exception {
	private static final long serialVersionUID = 8316606854864338751L;
	
	public DeriverException(String message) {
		super(message);
	}

	public DeriverException(Throwable cause) {
		super(cause);
	}

	public DeriverException(String message, Throwable cause) {
		super(message, cause);
	}
}
