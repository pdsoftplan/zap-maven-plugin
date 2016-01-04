package br.com.softplan.security.zap.api.exception;

/**
 * General runtime exception for the ZAP Client API.
 * 
 * @author pdsec
 */
public class ZapClientException extends RuntimeException {

	private static final long serialVersionUID = -4867749606526224619L;
	
	public ZapClientException(String message) {
		super(message);
	}
	
	public ZapClientException(Throwable e) {
		super(e);
	}
	
	public ZapClientException(String message, Throwable e) {
		super(message, e);
	}
	
}
