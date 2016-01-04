package br.com.softplan.security.zap.api.authentication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Class responsible to load the CAS authentication script to ZAP.
 * 
 * @author pdsec
 */
public class CasAuthenticationScriptLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(CasAuthenticationScriptLoader.class);
	
	private static final String JAVASCRIPT_ENGINE_IDENTIFIER = "ECMAScript";
	private static final String JAVASCRIPT_FILE_EXTENSION = ".js";
	
	private static final String SCRIPT_TYPE = "authentication";
	private static final String CAS_AUTH_SCRIPT_DESCRIPTION = "CAS authentication script";
	public  static final String CAS_AUTH_SCRIPT_NAME = "cas-auth";
	public  static final String CAS_AUTH_SCRIPT_FILE_NAME = CAS_AUTH_SCRIPT_NAME + JAVASCRIPT_FILE_EXTENSION;
	public  static final String CAS_AUTH_SCRIPT_RELATIVE_PATH = "/scripts/cas-auth.js";
	public  static final String CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH = "/zap/scripts/";
	
	private String casAuthScriptPath = CasAuthenticationHandler.class.getResource(CAS_AUTH_SCRIPT_RELATIVE_PATH).getPath();
	private File casAuthScriptTempFile;
	
	private ClientApi api;
	private String apiKey;
	private final boolean isZapRunningOnDocker;
	
	public CasAuthenticationScriptLoader(ClientApi api, ZapInfo zapInfo) {
		this.api = api;
		this.apiKey = zapInfo.getApiKey();
		this.isZapRunningOnDocker = zapInfo.shouldRunWithDocker();
	}

	public void loadScript() {
		try {
			clearPreviouslyLoadedCasAuthScript();
			
			String scriptEngine = pickAvailableJavaScriptEngine();
			if (isZapRunningOnDocker) {
				casAuthScriptPath = CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH + CAS_AUTH_SCRIPT_FILE_NAME;
			} else if (scriptFileIsNotAccessible()) {
				casAuthScriptPath = getCasAuthScriptTempFile().getAbsolutePath();
			}
			
			LOGGER.debug("Loading CAS authentication script from file: {}.", casAuthScriptPath);
			ApiResponse response = api.script.load(
					apiKey, CAS_AUTH_SCRIPT_NAME, SCRIPT_TYPE, scriptEngine, casAuthScriptPath, CAS_AUTH_SCRIPT_DESCRIPTION);
			AbstractAuthenticationHandler.validateResponse(response, "Load CAS authentication script");
			
		} catch (ClientApiException | IOException e) {
			LOGGER.error("Error loading CAS authentication script.", e);
			throw new ZapClientException(e);
		}
	}
	
	private void clearPreviouslyLoadedCasAuthScript() {
		try {
			ApiResponseList listScriptsResponse = (ApiResponseList) api.script.listScripts();
			for (ApiResponse script : listScriptsResponse.getItems()) {
				if (((ApiResponseSet) script).getAttribute("name").equals(CAS_AUTH_SCRIPT_NAME)) {
					ApiResponse response = api.script.remove(apiKey, CAS_AUTH_SCRIPT_NAME);
					AbstractAuthenticationHandler.validateResponse(response, "Clear previously loaded authentication script");
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
		return null;
	}

	private boolean isJavaScriptEngine(String engineName) {
		return engineName.contains(JAVASCRIPT_ENGINE_IDENTIFIER);
	}
	
	private boolean scriptFileIsNotAccessible() {
		return !new File(casAuthScriptPath).exists();
	}

	private File getCasAuthScriptTempFile() throws IOException {
		if (casAuthScriptTempFile == null) {
			casAuthScriptTempFile = createCasAuthScriptTempFile();
		}
		return casAuthScriptTempFile;
	}

	public File createCasAuthScriptTempFile() throws IOException {
		File tempFile = File.createTempFile(CAS_AUTH_SCRIPT_NAME, JAVASCRIPT_FILE_EXTENSION);
		tempFile.deleteOnExit();

		InputStream casAuthScriptInputStream = CasAuthenticationHandler.class.getResourceAsStream(CAS_AUTH_SCRIPT_RELATIVE_PATH);
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
			IOUtils.copy(casAuthScriptInputStream, fileOutputStream);
		}
		return tempFile;
	}

}
