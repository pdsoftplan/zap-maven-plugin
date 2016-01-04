package br.com.softplan.security.zap.api.exception;

/**
 * This will be thrown if the analysis timeout expires.
 * 
 * @author pdsec
 */
public class ZapAnalysisTimeoutException extends RuntimeException {

	private static final long serialVersionUID = -8195267976688210143L;

	public ZapAnalysisTimeoutException(String message) {
		super(message);
	}
	
	public ZapAnalysisTimeoutException(Throwable e) {
		super(e);
	}
	
	public ZapAnalysisTimeoutException(String message, Throwable e) {
		super(message, e);
	}
}