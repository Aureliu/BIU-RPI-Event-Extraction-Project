package edu.cuny.qc.scorer;

public class SignalMechanismRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4494408029262113244L;

	public SignalMechanismRuntimeException(String message) {
		super(message);
	}

	public SignalMechanismRuntimeException(Throwable cause) {
		super(cause);
	}

	public SignalMechanismRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
