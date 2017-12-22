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
 * Class to handle CAS authentication via ZAP.
 * 
 * @author pdsec
 */
public class CasAuthenticationHandler extends AbstractAuthenticationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CasAuthenticationHandler.class);

	public  static final String SCRIPT_NAME = "cas-auth";
	private static final String SCRIPT_DESCRIPTION = "CAS authentication script";
	
	private static final String SCRIPT_AUTHENTICATION_TYPE = "scriptBasedAuthentication";
	
	private static final String SCRIPT_NAME_PARAM = "scriptName";
	private static final String LOGIN_PAGE_PARAM = "loginUrl";
	private static final String PROTECTED_PAGES_PARAM = "protectedPages";
	private static final String EXTRA_POST_DATA_PARAM = "extraPostData";
	
	private AuthenticationScriptLoader scriptLoader;

	protected CasAuthenticationHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		super(api, zapInfo, authenticationInfo);
		
		this.scriptLoader = new AuthenticationScriptLoader(api, zapInfo, SCRIPT_NAME, SCRIPT_DESCRIPTION);
	}

	@Override
	protected void setupAuthentication(String targetUrl) {
		scriptLoader.loadScript();
		
		setScriptAuthenticationMethod();
		createAndEnableUser();
		setupUserCredentials();
		enableForcedUserMode();
	}
	
	private void setScriptAuthenticationMethod() {
		try {
			String encodedScriptName = URLEncoder.encode(SCRIPT_NAME, UTF_8);
			String encodedLoginUrl = URLEncoder.encode(getAuthenticationInfo().getLoginUrl(), UTF_8);
			String encodedProtectedPages = URLEncoder.encode(getAuthenticationInfo().getProtectedPagesSeparatedByComma(), UTF_8);
			String authParams = SCRIPT_NAME_PARAM + "=" + encodedScriptName + 
					"&" + LOGIN_PAGE_PARAM        + "=" + encodedLoginUrl +
					"&" + PROTECTED_PAGES_PARAM   + "=" + encodedProtectedPages;
			
			String extraPostData = getAuthenticationInfo().getExtraPostData();
			if (extraPostData != null) {
				String encodedExtraPostData = URLEncoder.encode(extraPostData, UTF_8);
				authParams += "&" + EXTRA_POST_DATA_PARAM + "=" + encodedExtraPostData;
			}
			
			LOGGER.debug("Setting script authentication method with params: {}", authParams);
			ApiResponse response = getApi().authentication.setAuthenticationMethod(
					getApiKey(), ZAP_DEFAULT_CONTEXT_ID, SCRIPT_AUTHENTICATION_TYPE, authParams);
			ZapHelper.validateResponse(response, "Set script authentication method");
		} catch (ClientApiException | UnsupportedEncodingException e) {
			LOGGER.error("Error setting up script authentication method.", e);
			throw new ZapClientException(e);
		}
	}

}
