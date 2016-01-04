package br.com.softplan.security.zap.api.authentication;

/**
 * The implementations of this interface should be able to handle ZAP's
 * authentication to ensure the scans will be authenticated. 
 * 
 * @author pdsec
 */
public interface AuthenticationHandler {

	/**
	 * Handles all the necessary configuration and procedures to
	 * make sure ZAP's scan will be authenticated. 
	 * 
	 * @param targetUrl the URL of the target application.
	 */
	void handleAuthentication(String targetUrl);
	
}
