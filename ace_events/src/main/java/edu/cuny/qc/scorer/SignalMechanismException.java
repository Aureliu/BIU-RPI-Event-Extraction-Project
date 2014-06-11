package edu.cuny.qc.scorer;

public class SignalMechanismException extends Exception {

	private static final long serialVersionUID = 4494408029262113244L;

	public SignalMechanismException(String message) {
		super(message);
	}

	public SignalMechanismException(Throwable cause) {
		super(cause);
	}

	public SignalMechanismException(String message, Throwable cause) {
		super(message, cause);
	}
}
