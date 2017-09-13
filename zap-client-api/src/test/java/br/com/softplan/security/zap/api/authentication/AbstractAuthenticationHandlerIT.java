package br.com.softplan.security.zap.api.authentication;

import static br.com.softplan.security.zap.api.authentication.AbstractAuthenticationHandler.ZAP_DEFAULT_CONTEXT_ID;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import br.com.softplan.security.zap.api.ZapHelper;
import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.util.BaseIT;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApiException;

public class AbstractAuthenticationHandlerIT extends BaseIT {

	@Test
	public void includeTargetInContextTest() throws ClientApiException {
		String targetUrl = "http://targetUrl";
		ZapHelper.includeInContext(api, zapInfo.getApiKey(), AnalysisInfo.builder().targetUrl(targetUrl).build());
		
		ApiResponseElement response = (ApiResponseElement) api.context.includeRegexs("Default Context");
		String includedInContext = response.getValue();
		assertTrue(includedInContext.contains("\\Q" + targetUrl + "\\E"));
	}
	
	@Test
	public void excludeUrlsFromScannerTest() throws ClientApiException {
		String apiKey = "";
		api.spider.clearExcludedFromScan(apiKey);
		api.ascan.clearExcludedFromScan(apiKey);
		
		AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
				.excludeFromScan(new String[]{"excludeFromScan1", "excludeFromScan2"})
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		
		AbstractAuthenticationHandler nilHandler = new NilAuthenticationHandler(api, zapInfo, authenticationInfo);
		
		nilHandler.excludeUrlsFromScanners();
		
		checkExcludedUrlsFromResponse(authenticationInfo, (ApiResponseList) api.spider.excludedFromScan());
		checkExcludedUrlsFromResponse(authenticationInfo, (ApiResponseList) api.ascan.excludedFromScan());
	}

	private void checkExcludedUrlsFromResponse(AuthenticationInfo authenticationInfo, ApiResponseList response) {
		assertTrue(response.getItems().size() == authenticationInfo.getExcludeFromScan().length);
		
		String excludedUrl1 = ((ApiResponseElement) response.getItems().get(0)).getValue();
		String excludedUrl2 = ((ApiResponseElement) response.getItems().get(1)).getValue();
		
		assertEquals(excludedUrl1, "\\Q" + authenticationInfo.getExcludeFromScan()[0] + "\\E");
		assertEquals(excludedUrl2, "\\Q" + authenticationInfo.getExcludeFromScan()[1] + "\\E");
	}
	
	@Test
	public void setupLoggedInAndOutRegexTest() throws ClientApiException {
		AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
					.loggedInRegex("loggedInRegex")
					.loggedOutRegex("loggedOutRegex")
					.buildFormAuthenticationInfo("loginUrl", "username", "password");
		AbstractAuthenticationHandler nilHandler = new NilAuthenticationHandler(api, zapInfo, authenticationInfo);
		
		nilHandler.setupLoggedInAndOutRegex();
		
		ApiResponseElement responseIn = (ApiResponseElement) api.authentication.getLoggedInIndicator(ZAP_DEFAULT_CONTEXT_ID);
		ApiResponseElement responseOut = (ApiResponseElement) api.authentication.getLoggedOutIndicator(ZAP_DEFAULT_CONTEXT_ID);
		
		String loggedInRegex = responseIn.getValue();
		String loggedOutRegex = responseOut.getValue();
		
		assertEquals(loggedInRegex, authenticationInfo.getLoggedInRegex());
		assertEquals(loggedOutRegex, authenticationInfo.getLoggedOutRegex());
	}
	
	@Test
	public void createAndEnableUserTest() throws ClientApiException {
		AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		
		AbstractAuthenticationHandler nilHandler = new NilAuthenticationHandler(api, zapInfo, authenticationInfo);
		
		String userId = nilHandler.createAndEnableUser();
		
		ApiResponseSet response = (ApiResponseSet) api.users.getUserById(ZAP_DEFAULT_CONTEXT_ID, userId);
		assertEquals(response.getAttribute("id"), userId);
		assertEquals(response.getAttribute("contextId"), ZAP_DEFAULT_CONTEXT_ID);
		assertEquals(response.getAttribute("name"), authenticationInfo.getUsername());
		assertEquals(Boolean.parseBoolean(response.getAttribute("enabled")), true);
	}
	
	@Test
	public void setupUserCredentialsTest() throws ClientApiException {
		// Set form authentication so the user can have its credentials set up
		setFormAuthenticationMethod();
		
		AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		AbstractAuthenticationHandler nilHandler = new NilAuthenticationHandler(api, zapInfo, authenticationInfo);
		
		String userId = nilHandler.createAndEnableUser();
		nilHandler.setupUserCredentials();
		
		ApiResponseSet response = (ApiResponseSet) api.users.getAuthenticationCredentials(ZAP_DEFAULT_CONTEXT_ID, userId);
		assertEquals(response.getAttribute("username"), authenticationInfo.getUsername());
		assertEquals(response.getAttribute("password"), authenticationInfo.getPassword());
	}

	@Test
	public void forcedUserModeTest() throws ClientApiException {
		// Set form authentication so Forced User Mode can be used
		setFormAuthenticationMethod();
		
		AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		AbstractAuthenticationHandler nilHandler = new NilAuthenticationHandler(api, zapInfo, authenticationInfo);
		
		String userId = nilHandler.createAndEnableUser();
		nilHandler.setupUserCredentials();
		
		nilHandler.enableForcedUserMode();
		
		ApiResponseElement enabledResponse = (ApiResponseElement) api.forcedUser.isForcedUserModeEnabled();
		assertTrue(Boolean.parseBoolean(enabledResponse.getValue()));
		
		ApiResponseElement userResponse = (ApiResponseElement) api.forcedUser.getForcedUser(ZAP_DEFAULT_CONTEXT_ID);
		assertEquals(userResponse.getValue(), userId);
		
		nilHandler.disableForcedUserMode();
		
		ApiResponseElement disabledResponse = (ApiResponseElement) api.forcedUser.isForcedUserModeEnabled();
		assertFalse(Boolean.parseBoolean(disabledResponse.getValue()));
	}
	
	private void setFormAuthenticationMethod() throws ClientApiException {
		String apiKey = "";
		api.authentication.setAuthenticationMethod(apiKey, ZAP_DEFAULT_CONTEXT_ID, "formBasedAuthentication", "loginUrl=http://localhost");
	}
	
	@Test
	public void validateResponseTestWithSuccessResponse() {
		ApiResponse response = new ApiResponseElement("test", "OK");
		ZapHelper.validateResponse(response, "test");
	}
	
	@Test(expectedExceptions=ZapClientException.class)
	public void validateResponseTestWithFailureResponse() {
		ApiResponse response = new ApiResponseElement("test", "not OK");
		ZapHelper.validateResponse(response, "test");
	}
	
	@Test
	public void extractResponseTest() {
		String value = "value";
		ApiResponse response = new ApiResponseElement("name", value);
		assertEquals(value, ZapHelper.extractResponse(response));
	}
	
}
