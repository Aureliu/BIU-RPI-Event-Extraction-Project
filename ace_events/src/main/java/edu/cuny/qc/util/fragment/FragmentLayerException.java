package edu.cuny.qc.util.fragment;

public class FragmentLayerException extends Exception {
	private static final long serialVersionUID = 3854518774537300756L;

	public FragmentLayerException(String message) {
		super(message);
	}

	public FragmentLayerException(Throwable cause) {
		super(cause);
	}

	public FragmentLayerException(String message, Throwable cause) {
		super(message, cause);
	}
}
