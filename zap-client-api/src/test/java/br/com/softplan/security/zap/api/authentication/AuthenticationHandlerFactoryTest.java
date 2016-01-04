package br.com.softplan.security.zap.api.authentication;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;

public class AuthenticationHandlerFactoryTest {

	@Test
	public void testCasHandlerCreation() {
		ZapInfo zapInfo = ZapInfo.builder().buildToUseRunningZap("localhost", 8080);
		
		AuthenticationInfo authInfo = AuthenticationInfo.builder()
				.buildCasAuthenticationInfo("loginUrl", "username", "password", "protectedPages");
		
		AuthenticationHandler handler = AuthenticationHandlerFactory.makeHandler(null, zapInfo, authInfo);
		assertTrue(handler instanceof CasAuthenticationHandler);
	}
	
	@Test
	public void testFormHandlerCreation() {
		ZapInfo zapInfo = ZapInfo.builder().buildToUseRunningZap("localhost", 8080);
		
		AuthenticationInfo authInfo = AuthenticationInfo.builder()
				.buildFormAuthenticationInfo("loginUrl", "username", "password");;
		
		AuthenticationHandler handler = AuthenticationHandlerFactory.makeHandler(null, zapInfo, authInfo);
		assertTrue(handler instanceof FormAuthenticationHandler);
	}
	
}
