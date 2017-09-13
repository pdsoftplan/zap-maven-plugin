package br.com.softplan.security.zap.api;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * Helper to be used by classes that make calls to the ZAP API.
 * 
 * @author pdsec
 */
public class ZapHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZapHelper.class);
	
	private static final String ZAP_SUCCESS_RESPONSE = "OK";
	private static final String ZAP_DEFAULT_CONTEXT_NAME = "Default Context";
	
	public static String extractResponse(ApiResponse response) {
		return ((ApiResponseElement) response).getValue();
	}
	
	public static void validateResponse(ApiResponse response, String operationDescription) {
		String responseValue = extractResponse(response);
		if (!responseValue.equals(ZAP_SUCCESS_RESPONSE)) {
			String message = "ZAP API did not respond '" + ZAP_SUCCESS_RESPONSE + "' during the following operation: "
					 + operationDescription + ". Actual response: " + responseValue;
			LOGGER.error(message);
			throw new ZapClientException(message);
		}
	}
	
	public static void includeInContext(ClientApi api, String apiKey, AnalysisInfo analysisInfo) {
		String[] context = analysisInfo.getContext();
		LOGGER.debug("Including target '{}' in context.", Arrays.toString(context));
		
		try {
			for (String contextUrl : context) {
				if (isContextUrlRelative(contextUrl)) {
					contextUrl = resolveContextUrl(contextUrl, analysisInfo);
				}
				ApiResponse response = api.context.includeInContext(apiKey, ZAP_DEFAULT_CONTEXT_NAME, "\\Q" + contextUrl + "\\E.*");
				validateResponse(response, "Include target in context.");
			}
		} catch (ClientApiException e) {
			LOGGER.error("Error including target in context.", e);
			throw new ZapClientException(e);
		}
	}
	
	private static boolean isContextUrlRelative(String contextUrl) {
		return !contextUrl.startsWith("http");
	}
	
	private static String resolveContextUrl(String contextUrl, AnalysisInfo analysisInfo) {
		String activeScanStartingPointUrl = analysisInfo.getActiveScanStartingPointUrl();
		if (activeScanStartingPointUrl.endsWith("/")) {
			activeScanStartingPointUrl = activeScanStartingPointUrl.substring(0, activeScanStartingPointUrl.length()-1);
		}
		if (contextUrl.startsWith("/")) {
			contextUrl = contextUrl.substring(1);
		}
		return activeScanStartingPointUrl + "/" + contextUrl;
	}
	
	public static void setTechnologiesInContext(ClientApi api, String apiKey, AnalysisInfo analysisInfo) {
		String technologies = analysisInfo.getTechnologiesSeparatedByComma();
		if (technologies == null) {
			return;
		}
		LOGGER.debug("Setting technologies in context: {}.", technologies);
		
		try {
			ApiResponse responseFromExcludingTechnologies = api.context.excludeAllContextTechnologies(apiKey, ZAP_DEFAULT_CONTEXT_NAME);
			validateResponse(responseFromExcludingTechnologies, "Exclude all context technologies.");
			
			ApiResponse responseFromIncludingTechnologies = api.context.includeContextTechnologies(apiKey, ZAP_DEFAULT_CONTEXT_NAME, technologies);
			validateResponse(responseFromIncludingTechnologies, "Exclude all context technologies.");
		} catch (ClientApiException e) {
			LOGGER.error("Error setting technologies in context.", e);
			throw new ZapClientException(e);
		}
	}
	
	private ZapHelper() {}

}
