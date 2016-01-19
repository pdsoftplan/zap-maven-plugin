package br.com.softplan.security.zap.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ApiResponse;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ApiResponseElement;

/**
 * Helper to be used by classes that make calls to the ZAP API.
 * 
 * @author pdsec
 */
public class ZapHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZapHelper.class);
	
	protected static final String ZAP_SUCCESS_RESPONSE = "OK";
	
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
	
	private ZapHelper() {}
	
}
