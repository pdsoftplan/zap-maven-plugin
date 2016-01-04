package br.com.softplan.security.zap.api.authentication;

import org.zaproxy.clientapi.core.ClientApi;

import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Simple extension of {@code AbstractAuthenticationHandler} to test its functionalities.
 * 
 * @see AbstractAuthenticationHandler
 * @author pdsec
 */
public class NilAuthenticationHandler extends AbstractAuthenticationHandler {

	protected NilAuthenticationHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		super(api, zapInfo, authenticationInfo);
	}

	@Override
	public void setupAuthentication(String targetUrl) {}

}
