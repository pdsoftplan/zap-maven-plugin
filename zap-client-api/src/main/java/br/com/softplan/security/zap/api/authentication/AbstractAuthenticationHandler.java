package br.com.softplan.security.zap.api.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
 * Base class to handle authentication.
 * <p>
 * It handles all the common ZAP API calls needed for different types of authentication.
 * 
 * @author pdsec
 */
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuthenticationHandler.class);
	
	protected static final String UTF_8 = StandardCharsets.UTF_8.name();
	
	protected static final String ZAP_DEFAULT_CONTEXT_ID = "1";
	protected static final String ZAP_DEFAULT_SESSION_NAME = "Session 0";
	
	private ClientApi api;
	
	private ZapInfo zapInfo;
	
	private String apiKey;
	
	private AuthenticationInfo authenticationInfo;
	
	private String userId;
	
	protected AbstractAuthenticationHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		this.api = api;
		this.zapInfo = zapInfo;
		this.apiKey = zapInfo.getApiKey();
		this.authenticationInfo = authenticationInfo;
	}
	
	protected ClientApi getApi() {
		return api;
	}

	protected ZapInfo getZapInfo() {
		return zapInfo;
	}
	
	protected String getApiKey() {
		return apiKey;
	}

	protected AuthenticationInfo getAuthenticationInfo() {
		return authenticationInfo;
	}
	
	@Override
	public void handleAuthentication(String targetUrl) {
		LOGGER.debug("--- Starting authentication handling ---");
		
		setupAuthentication(targetUrl);
		excludeUrlsFromScanners();
		setupLoggedInAndOutRegex();
		
		LOGGER.debug("--- Finished authentication handling ---\n");
	}

	protected abstract void setupAuthentication(String targetUrl);
	
	protected void excludeUrlsFromScanners() {
		try {
			String[] excludeFromScan = authenticationInfo.getExcludeFromScan();
			if (excludeFromScan != null) {
				for (String url : excludeFromScan) {
					LOGGER.debug("Excluding URL '{}' from scanners.", url);
					
					ApiResponse responseFromSpider = api.spider.excludeFromScan(apiKey, "\\Q" + url + "\\E");
					ZapHelper.validateResponse(responseFromSpider, "Exclude '" + url + "' from Spider.");
					
					ApiResponse responseFromAScan = api.ascan.excludeFromScan(apiKey, "\\Q" + url + "\\E");
					ZapHelper.validateResponse(responseFromAScan, "Exclude '" + url + "' from Active Scan.");
				}
			}
		} catch (ClientApiException e) {
			LOGGER.error("Error excluding URLs from scanners.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected void setupLoggedInAndOutRegex() {
		String loggedInRegex = authenticationInfo.getLoggedInRegex();
		String loggedOutRegex = authenticationInfo.getLoggedOutRegex();
		try {
			if (loggedInRegex != null) {
				LOGGER.debug("Setting '{}' as the logged in regex.", loggedInRegex);
				
				ApiResponse response = api.authentication.setLoggedInIndicator(
						apiKey, ZAP_DEFAULT_CONTEXT_ID, loggedInRegex);
				ZapHelper.validateResponse(response, "Set logged in regex");
			}
			if (loggedOutRegex != null) {
				LOGGER.debug("Setting '{}' as the logged out regex.", loggedOutRegex);
				
				ApiResponse response = api.authentication.setLoggedOutIndicator(
						apiKey, ZAP_DEFAULT_CONTEXT_ID, loggedOutRegex);
				ZapHelper.validateResponse(response, "Set logged out regex");
			}
		} catch (ClientApiException e) {
			LOGGER.error("Error setting up logged in and/or logged out regex for authentication.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected String createAndEnableUser() {
		LOGGER.debug("Creating and enabling user '{}'.", authenticationInfo.getUsername());
		
		try {
			ApiResponse responseFromCreating = api.users.newUser(
					apiKey, ZAP_DEFAULT_CONTEXT_ID, authenticationInfo.getUsername());
			userId = ZapHelper.extractResponse(responseFromCreating);
			
			ApiResponse responseFromEnabling = api.users.setUserEnabled(
					apiKey, ZAP_DEFAULT_CONTEXT_ID, userId, Boolean.TRUE.toString());
			ZapHelper.validateResponse(responseFromEnabling, "Enable the user");
		} catch (ClientApiException e) {
			LOGGER.error("Error creating and enabling user for authentication.", e);
			throw new ZapClientException(e);
		}
		
		return userId;
	}
	
	protected void setupUserCredentials() {
		LOGGER.debug("Setting up credentials for user '{}'.", authenticationInfo.getUsername());
		
		try {
			String encodedUsername = URLEncoder.encode(authenticationInfo.getUsername(), UTF_8);
			String encodedPassword = URLEncoder.encode(authenticationInfo.getPassword(), UTF_8);
			String credentials = "username=" + encodedUsername + "&password=" + encodedPassword;
			ApiResponse responseFromSettingCredentials = api.users.setAuthenticationCredentials(
					apiKey, ZAP_DEFAULT_CONTEXT_ID, userId, credentials);
			ZapHelper.validateResponse(responseFromSettingCredentials, "Set the user's credentials");
		} catch (ClientApiException | UnsupportedEncodingException e) {
			LOGGER.error("Error setting up user's credential for authentication.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected void enableForcedUserMode() {
		LOGGER.debug("Setting up Forced User Mode.");
		
		try {
			ApiResponse responseFromSettingForcedUser = api.forcedUser.setForcedUser(
					apiKey, ZAP_DEFAULT_CONTEXT_ID, userId);
			ZapHelper.validateResponse(responseFromSettingForcedUser, "Set forced user.");
			
			ApiResponse responseFromEnabling = api.forcedUser.setForcedUserModeEnabled(apiKey, true);
			ZapHelper.validateResponse(responseFromEnabling, "Enable Forced User Mode.");
		} catch (ClientApiException e) {
			LOGGER.error("Error setting up Forced User Mode.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected void disableForcedUserMode() {
		LOGGER.debug("Disabling Forced User Mode.");
		
		try {
			ApiResponse response = api.forcedUser.setForcedUserModeEnabled(apiKey, false);
			ZapHelper.validateResponse(response, "Disable Forced User Mode.");
		} catch (ClientApiException e) {
			LOGGER.error("Error disabling Forced User Mode.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected void addHttpSessionTokens(String site) {
		LOGGER.debug("Adding session tokens: {}.", Arrays.toString(authenticationInfo.getHttpSessionTokens()));
		
		try {
			for (String sessionToken : authenticationInfo.getHttpSessionTokens()) {
				ApiResponse response = api.httpSessions.addSessionToken(apiKey, site, sessionToken);
				ZapHelper.validateResponse(response, "Add session tokens.");
			}
		} catch (ClientApiException e) {
			LOGGER.error("Error adding session tokens.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected void setHttpSessionAsActive(String site) {
		LOGGER.debug("Setting session as active.");
		
		try {
			ApiResponse response = api.httpSessions.setActiveSession(apiKey, site, ZAP_DEFAULT_SESSION_NAME);
			ZapHelper.validateResponse(response, "Set session as active.");
		} catch (ClientApiException e) {
			LOGGER.error("Error setting session as active.", e);
			throw new ZapClientException(e);
		}
	}
	
}
