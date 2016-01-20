package br.com.softplan.security.zap.api.authentication;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.exception.AuthenticationInfoValidationException;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.model.AuthenticationType;

/**
 * Class to validate {@link AuthenticationInfo} instances.
 * 
 * @author pdsec
 */
public final class AuthenticationInfoValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInfoValidator.class);
	
	public static void validate(AuthenticationInfo info) {
		LOGGER.info("--- Validating authentication information ---");
		if (info == null) {
			String message = "AuthenticationInfo cannot be null.";
			LOGGER.error(message);
			throw new AuthenticationInfoValidationException(message);
		}
		
		checkRequiredField(info.getType(), "type");
		checkRequiredField(info.getUsername(), "username");
		checkRequiredField(info.getPassword(), "password");
		if (info.getType() != AuthenticationType.HTTP) {
			checkRequiredField(info.getLoginUrl(), "loginUrl");
		}
		
		List<String> warnings = new ArrayList<>();
		switch (info.getType()) {
			case HTTP:
				validateHttpAuthenticationInfo(info, warnings);
				break;
			case FORM:
				validateFormAuthenticationInfo(info, warnings);
				break;
			case CAS:
				validateCasAuthenticationInfo(info, warnings);
				break;
			case SELENIUM:
		}
		
		if (warnings.isEmpty()) {
			LOGGER.info("The authentication information provided was successfully validated.");
		} else {
			LOGGER.warn("Some warnings were generated while validating the authentication information provided:");
			for (String warning : warnings) {
				LOGGER.warn("\t{}", warning);
			}
		}
		LOGGER.info("--- Finished validating authentication information ---\n");
	}

	private static void checkRequiredField(Object field, String fieldName) {
		if (field == null) {
			String message = "The field '" + fieldName + "' is required when working with authentication.";
			LOGGER.error(message);
			throw new AuthenticationInfoValidationException(message);
		}
	}
	
	private static void validateHttpAuthenticationInfo(AuthenticationInfo info, List<String> warnings) {
		if (info.getHostname() == null) {
			String message = "The field 'hostname' is required for HTTP authentication.";
			LOGGER.error(message);
			throw new AuthenticationInfoValidationException(message);
		}
		if (info.getRealm() == null) {
			String message = "The field 'realm' is required for HTTP authentication.";
			LOGGER.error(message);
			throw new AuthenticationInfoValidationException(message);
		}
	}
	
	private static void validateFormAuthenticationInfo(AuthenticationInfo info, List<String> warnings) {
		validateReauthenticationConfiguration(info, warnings);
		if (info.getProtectedPages() != null && info.getProtectedPages().length > 0) {
			warnings.add("The 'protectedPages' field is not used for form based authentication and is necessary only for CAS authentication.");
		}
	}

	private static void validateCasAuthenticationInfo(AuthenticationInfo info, List<String> warnings) {
		validateReauthenticationConfiguration(info, warnings);
		if (info.getProtectedPages() == null || info.getProtectedPages().length == 0) {
			String message = "The field 'protectedPages' is required for CAS authentication. "
					+ "A protected page of each context must be accessed prior to scanning to avoid later redirections.";
			LOGGER.error(message);
			throw new AuthenticationInfoValidationException(message);
		}
	}
	
	private static void validateReauthenticationConfiguration(AuthenticationInfo info, List<String> warnings) {
		if (info.getLoggedInRegex() == null && info.getLoggedOutRegex() == null && 
				(info.getExcludeFromScan() == null || info.getExcludeFromScan().length == 0)) {
			warnings.add("None of the fields 'loggedInRegex', 'loggedOutRegex' and 'excludeFromScan' were provided. "
					+ "Reauthentication will not be possible and there might be a chance that the Spider will log itself out during the scan.");
		}
	}
	
	private AuthenticationInfoValidator() {}
	
}
