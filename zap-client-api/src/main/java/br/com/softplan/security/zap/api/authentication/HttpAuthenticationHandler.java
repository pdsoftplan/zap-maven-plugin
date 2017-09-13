package br.com.softplan.security.zap.api.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.ZapHelper;
import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * Class to handle HTTP based authentication via ZAP.
 * 
 * @author pdsec
 */
public class HttpAuthenticationHandler extends AbstractAuthenticationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpAuthenticationHandler.class);
	
	private static final String HTTP_AUTHENTICATION_TYPE = "httpAuthentication";
	
	private static final String HOSTNAME_PARAM = "hostname";
	private static final String REALM_PARAM = "realm";
	private static final String PORT_PARAM = "port";
	
	protected HttpAuthenticationHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		super(api, zapInfo, authenticationInfo);
	}

	@Override
	protected void setupAuthentication(String targetUrl) {
		setHttpAuthenticationMethod();
		createAndEnableUser();
		setupUserCredentials();
		enableForcedUserMode();
	}
	
	private void setHttpAuthenticationMethod() {
		try {
			String encodedHostname = URLEncoder.encode(getAuthenticationInfo().getHostname(), UTF_8);
			String encodedRealm = URLEncoder.encode(getAuthenticationInfo().getRealm(), UTF_8);
			String authParams = HOSTNAME_PARAM + "=" + encodedHostname + 
					"&" + REALM_PARAM + "=" + encodedRealm +
					"&" + PORT_PARAM + "=" + getAuthenticationInfo().getPortAsString();
			
			LOGGER.debug("Setting HTTP authentication method with params: {}", authParams);
			ApiResponse response = getApi().authentication.setAuthenticationMethod(
							getApiKey(), ZAP_DEFAULT_CONTEXT_ID, HTTP_AUTHENTICATION_TYPE, authParams);
			ZapHelper.validateResponse(response, "Set HTTP authentication method.");
		} catch (ClientApiException | UnsupportedEncodingException e) {
			LOGGER.error("Error setting up HTTP authentication method.", e);
			throw new ZapClientException(e);
		}
	}
	
}
