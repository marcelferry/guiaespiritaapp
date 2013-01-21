package br.com.mythe.droid.gelib.exception;

public class GuiaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GuiaException(String message) {
		super(message);
	}

	public GuiaException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
