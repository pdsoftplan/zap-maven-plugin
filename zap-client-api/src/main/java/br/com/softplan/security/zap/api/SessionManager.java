package br.com.softplan.security.zap.api;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

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
        createNewSession(api, apiKey, true);
    }

    public void createNewSession(ClientApi api, String apiKey, boolean overwrite) {
        LOGGER.debug("Creating a new ZAP session.");

        try {
            ApiResponse response = api.core.newSession(SESSION_PREFIX + sessionId++, Boolean.toString(overwrite));
            ZapHelper.validateResponse(response, "Create a new ZAP session.");
        } catch (ClientApiException e) {
            LOGGER.error("Error creating a new ZAP session.", e);
            throw new ZapClientException(e);
        }
    }

}
