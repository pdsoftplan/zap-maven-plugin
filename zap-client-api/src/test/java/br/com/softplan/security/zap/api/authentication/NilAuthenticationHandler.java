package br.com.softplan.security.zap.api.authentication;

import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;
import org.zaproxy.clientapi.core.ClientApi;

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
