package br.com.softplan.security.zap.api.authentication;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.ZapHelper;
import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.commons.ZapInfo;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

public class AuthenticationScriptLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationScriptLoader.class);
	
	private static final String JAVASCRIPT_ENGINE_IDENTIFIER = "ECMAScript";
	private static final String ZAP_SCRIPT_TYPE = "authentication";
	
	private ClientApi api;
	private String apiKey;
	private final boolean isZapRunningOnDocker;

	private AuthenticationScript script;
	
	public AuthenticationScriptLoader(ClientApi api, ZapInfo zapInfo, String scriptName, String scriptDescription) {
		this.api = api;
		this.apiKey = zapInfo.getApiKey();
		this.isZapRunningOnDocker = zapInfo.shouldRunWithDocker();
		this.script = new AuthenticationScript(scriptName, scriptDescription);
	}

	public void loadScript() {
		try {
			clearPreviouslyLoadedCasAuthScript();
			
			String scriptEngine = pickAvailableJavaScriptEngine();
			String scriptPath = script.getPath(isZapRunningOnDocker);
			
			LOGGER.debug("Loading authentication script from file: {}.", scriptPath);
			ApiResponse response = api.script.load(
					apiKey, script.getName(), ZAP_SCRIPT_TYPE, scriptEngine, scriptPath, script.getDescription());
			ZapHelper.validateResponse(response, "Load authentication script");
			
		} catch (ClientApiException | IOException | URISyntaxException e) {
			LOGGER.error("Error loading authentication script.", e);
			throw new ZapClientException(e);
		}
	}
	
	private void clearPreviouslyLoadedCasAuthScript() {
		try {
			ApiResponseList listScriptsResponse = (ApiResponseList) api.script.listScripts();
			for (ApiResponse currentScript : listScriptsResponse.getItems()) {
				if (((ApiResponseSet) currentScript).getAttribute("name").equals(script.getName())) {
					ApiResponse response = api.script.remove(apiKey, script.getName());
					ZapHelper.validateResponse(response, "Clear previously loaded authentication script");
				}
			}
		} catch (ClientApiException e) {
			LOGGER.error("Error clearing previously loaded authentication script.", e);
			throw new ZapClientException(e);
		}
	}
	
	private String pickAvailableJavaScriptEngine() throws ClientApiException {
		ApiResponseList response = (ApiResponseList) api.script.listEngines();
		for (ApiResponse engine : response.getItems()) {
			String engineName = ((ApiResponseElement) engine).getValue();
			if (isJavaScriptEngine(engineName)) {
				return engineName;
			}
		}
		throw new ZapClientException("Could not find a JavaScript engine within ZAP.");
	}

	private boolean isJavaScriptEngine(String engineName) {
		return engineName.contains(JAVASCRIPT_ENGINE_IDENTIFIER);
	}
	
}
