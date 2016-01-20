package br.com.softplan.security.zap.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ApiResponse;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ApiResponseElement;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ClientApi;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ClientApiException;

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
	
	public static void includeInContext(ClientApi api, String apiKey, String targetUrl) {
		LOGGER.debug("Including target '{}' in context.", targetUrl);
		
		try {
			ApiResponse response = api.context.includeInContext(apiKey, ZAP_DEFAULT_CONTEXT_NAME, "\\Q" + targetUrl + "\\E.*");
			validateResponse(response, "Include target in context.");
		} catch (ClientApiException e) {
			LOGGER.error("Error including target in context.", e);
			throw new ZapClientException(e);
		}
	}
	
	private ZapHelper() {}
	
}
