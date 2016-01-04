package br.com.softplan.security.zap.api.exception;

/**
 * Exception that will be thrown in case authentication info provided is not valid.
 * 
 * @author pdsec
 */
public class AuthenticationInfoValidationException extends RuntimeException {

	private static final long serialVersionUID = -2619404350476128144L;

	public AuthenticationInfoValidationException(String message) {
		super(message);
	}
	
	public AuthenticationInfoValidationException(Throwable e) {
		super(e);
	}
	
	public AuthenticationInfoValidationException(String message, Throwable e) {
		super(message, e);
	}
	
}
