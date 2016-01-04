package br.com.softplan.security.zap.api.authentication;

import org.zaproxy.clientapi.core.ClientApi;

import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Factory to create the correct {@link AuthenticationHandler} based on the given {@link AuthenticationInfo} instance.
 * 
 * @author pdsec
 */
public final class AuthenticationHandlerFactory {

	public static AuthenticationHandler makeHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		if (authenticationInfo == null) {
			return null;
		}
		
		switch (authenticationInfo.getType()) {
			case FORM: return new FormAuthenticationHandler(api, zapInfo, authenticationInfo);
			case CAS:  return new CasAuthenticationHandler(api, zapInfo, authenticationInfo);
			default:   return null;
		}
	}

	private AuthenticationHandlerFactory() {}
	
}
