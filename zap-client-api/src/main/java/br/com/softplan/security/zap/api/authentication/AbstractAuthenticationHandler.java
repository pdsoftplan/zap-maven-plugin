package br.com.softplan.security.zap.api.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;

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
	
	protected static final String ZAP_SUCCESS_RESPONSE = "OK";
	protected static final String ZAP_DEFAULT_CONTEXT_NAME = "Default Context";
	protected static final String ZAP_DEFAULT_CONTEXT_ID = "1";
	
	private ClientApi api;
	
	private String apiKey;
	
	private AuthenticationInfo authenticationInfo;
	
	private String userId;
	
	protected AbstractAuthenticationHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		this.api = api;
		this.apiKey = zapInfo.getApiKey();
		this.authenticationInfo = authenticationInfo;
	}
	
	protected ClientApi getApi() {
		return api;
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
		
		includeTargetInContext(targetUrl);
		setupAuthentication(targetUrl);
		excludeUrlsFromScanners();
		setupLoggedInAndOutRegex();
		
		LOGGER.debug("--- Finished authentication handling ---\n");
	}

	protected void includeTargetInContext(String targetUrl) {
		LOGGER.debug("Including target '{}' in context.", targetUrl);
		
		try {
			ApiResponse response = api.context.includeInContext(
					apiKey, ZAP_DEFAULT_CONTEXT_NAME, "\\Q" + targetUrl + "\\E.*");
			validateResponse(response, "Include target in context.");
			
		} catch (ClientApiException e) {
			LOGGER.error("Error including target in context.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected abstract void setupAuthentication(String targetUrl);
	
	protected void excludeUrlsFromScanners() {
		try {
			String[] excludeFromScan = authenticationInfo.getExcludeFromScan();
			if (excludeFromScan != null) {
				for (String url : excludeFromScan) {
					LOGGER.debug("Excluding URL '{}' from scanners.", url);
					
					ApiResponse responseFromSpider = api.spider.excludeFromScan(apiKey, "\\Q" + url + "\\E");
					validateResponse(responseFromSpider, "Exclude '" + url + "' from Spider.");
					
					ApiResponse responseFromAScan = api.ascan.excludeFromScan(apiKey, "\\Q" + url + "\\E");
					validateResponse(responseFromAScan, "Exclude '" + url + "' from Active Scan.");
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
				validateResponse(response, "Set logged in regex");
			}
			if (loggedOutRegex != null) {
				LOGGER.debug("Setting '{}' as the logged out regex.", loggedOutRegex);
				
				ApiResponse response = api.authentication.setLoggedOutIndicator(
						apiKey, ZAP_DEFAULT_CONTEXT_ID, loggedOutRegex);
				validateResponse(response, "Set logged out regex");
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
			userId = extractResponse(responseFromCreating);
			
			ApiResponse responseFromEnabling = api.users.setUserEnabled(
					apiKey, ZAP_DEFAULT_CONTEXT_ID, userId, Boolean.TRUE.toString());
			validateResponse(responseFromEnabling, "Enable the user");
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
			validateResponse(responseFromSettingCredentials, "Set the user's credentials");
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
			validateResponse(responseFromSettingForcedUser, "Set forced user.");
			
			ApiResponse responseFromEnabling = api.forcedUser.setForcedUserModeEnabled(apiKey, true);
			validateResponse(responseFromEnabling, "Enable Forced User Mode.");
		} catch (ClientApiException e) {
			LOGGER.error("Error setting up Forced User Mode.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected void disableForcedUserMode() {
		LOGGER.debug("Disabling Forced User Mode.");
		
		try {
			ApiResponse responseFromEnabling = api.forcedUser.setForcedUserModeEnabled(apiKey, false);
			validateResponse(responseFromEnabling, "Disable Forced User Mode.");
		} catch (ClientApiException e) {
			LOGGER.error("Error disabling Forced User Mode.", e);
			throw new ZapClientException(e);
		}
	}
	
	protected static void validateResponse(ApiResponse response, String operationDescription) {
		String responseValue = extractResponse(response);
		if (!responseValue.equals(ZAP_SUCCESS_RESPONSE)) {
			String message = "ZAP API did not respond '" + ZAP_SUCCESS_RESPONSE + "' during the following operation: "
					 + operationDescription + ". Actual response: " + responseValue;
			LOGGER.error(message);
			throw new ZapClientException(message);
		}
	}
	
	protected static String extractResponse(ApiResponse response) {
		return ((ApiResponseElement) response).getValue();
	}
	
}
