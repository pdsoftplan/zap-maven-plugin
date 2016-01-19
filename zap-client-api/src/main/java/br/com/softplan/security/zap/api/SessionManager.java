package br.com.softplan.security.zap.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ApiResponse;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ClientApi;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ClientApiException;

/**
 * Class to manage ZAP sessions. 
 * 
 * @author pdsec
 */
public class SessionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
	
	private static final String SESSION_PREFIX = "zap-session-";
	
	private int sessionId;
	
	public void createNewSession(ClientApi api, String apiKey) {
		createNewSession(api, "zap-session-" + sessionId++, true);
	}
	
	public void createNewSession(ClientApi api, String apiKey, boolean overwrite) {
		LOGGER.debug("Creating a new ZAP session.");
		
		try {
			ApiResponse response = api.core.newSession(apiKey, SESSION_PREFIX + sessionId++, Boolean.toString(overwrite));
			ZapHelper.validateResponse(response, "Create a new ZAP session.");
		} catch (ClientApiException e) {
			LOGGER.error("Error creating a new ZAP session.", e);
			throw new ZapClientException(e);
		}
	}
	
}
