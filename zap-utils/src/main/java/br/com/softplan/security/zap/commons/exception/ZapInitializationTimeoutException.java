package br.com.softplan.security.zap.commons.exception;

/**
 * This exception should be thrown if ZAP is not started before the specified timeout.
 * 
 * @author pdsec
 */
public class ZapInitializationTimeoutException extends RuntimeException {

	private static final long serialVersionUID = -5283245793671447701L;

	public ZapInitializationTimeoutException(String message) {
		super(message);
	}
	
	public ZapInitializationTimeoutException(Throwable e) {
		super(e);
	}
	
	public ZapInitializationTimeoutException(String message, Throwable e) {
		super(message, e);
	}
	
}
