package br.com.softplan.security.zap.api.authentication;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.com.softplan.security.zap.api.exception.AuthenticationInfoValidationException;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.model.AuthenticationType;
import br.com.softplan.security.zap.api.util.LogbackTestAppender;
import ch.qos.logback.classic.Logger;

public class AuthenticationInfoValidatorTest {

	private final LogbackTestAppender appender = new LogbackTestAppender();
	
	@BeforeMethod
	public void setUp() {
		((Logger) LoggerFactory.getLogger(AuthenticationInfoValidator.class)).addAppender(appender);
	}
	
	@AfterMethod
	public void clearAppender() {
		appender.clearLog();
	}
	
	@AfterClass
	public void removeTestAppenderFromLogger() {
		((Logger) LoggerFactory.getLogger(AuthenticationInfoValidator.class)).detachAppender(appender);
	}
	
	@Test(expectedExceptions = AuthenticationInfoValidationException.class)
	public void infoMustNotBeNull() {
		AuthenticationInfoValidator.validate(null);
	}
	
	@Test(expectedExceptions = AuthenticationInfoValidationException.class)
	public void typeMustNotBeNull() {
		AuthenticationInfoValidator.validate(AuthenticationInfo.builder().build());
	}
	
	@Test(expectedExceptions = AuthenticationInfoValidationException.class)
	public void usernameMustNotBeNull() {
		AuthenticationInfo info = AuthenticationInfo.builder().type(AuthenticationType.FORM).build();
		AuthenticationInfoValidator.validate(info);
	}
	
	@Test(expectedExceptions = AuthenticationInfoValidationException.class)
	public void passwordMustNotBeNull() {
		AuthenticationInfo info = AuthenticationInfo.builder()
				.type(AuthenticationType.FORM)
				.username("username")
				.build();
		AuthenticationInfoValidator.validate(info);
	}
	
	@Test(expectedExceptions = AuthenticationInfoValidationException.class)
	public void loginUrlMustNotBeNull() {
		AuthenticationInfo info = AuthenticationInfo.builder()
				.type(AuthenticationType.FORM)
				.username("username")
				.username("password")
				.build();
		AuthenticationInfoValidator.validate(info);
	}
	
	@Test
	public void testSimpleFormAuthenticationInfo() {
		AuthenticationInfo simpleFormInfo = AuthenticationInfo.builder()
				.loggedInRegex("loggedInRegex")
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		AuthenticationInfoValidator.validate(simpleFormInfo);
		assertFalse(appender.hasWarn());
	}
	
	@Test
	public void warningShouldBeGeneratedIfProtectedPageIsNotNullInFormAuthentication() {
		AuthenticationInfo simpleFormInfo = AuthenticationInfo.builder()
				.protectedPages("protectedPage")
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		AuthenticationInfoValidator.validate(simpleFormInfo);
		assertTrue(appender.hasWarn());
	}
	
	@Test
	public void warningShouldBeGeneratedIfNoReauthenticationIsPossibleInFormAuthentication() {
		AuthenticationInfo info = AuthenticationInfo.builder()
				.buildFormAuthenticationInfo("loginUrl", "username", "password");
		AuthenticationInfoValidator.validate(info);
		assertTrue(appender.hasWarn());
	}
	
	@Test
	public void testSimpleCasAuthenticationInfo() {
		AuthenticationInfo simpleCasInfo = AuthenticationInfo.builder()
				.loggedOutRegex("loggedOutRegex")
				.buildCasAuthenticationInfo("loginUrl", "username", "password", "protectedPage");
		AuthenticationInfoValidator.validate(simpleCasInfo);
		assertFalse(appender.hasWarn());
	}
	
	@Test
	public void warningShouldBeGeneratedIfNoReauthenticationIsPossibleInCasAuthentication() {
		AuthenticationInfo simpleCasInfo = AuthenticationInfo.builder()
				.buildCasAuthenticationInfo("loginUrl", "username", "password", "protectedPage");
		AuthenticationInfoValidator.validate(simpleCasInfo);
		assertTrue(appender.hasWarn());
	}
	
	@Test(expectedExceptions=AuthenticationInfoValidationException.class)
	public void protectedPagesMustNotBeVoidInCasAuthentication() {
		AuthenticationInfo simpleCasInfo = AuthenticationInfo.builder()
				.loggedOutRegex("loggedOutRegex")
				.buildCasAuthenticationInfo("loginUrl", "username", "password");
		AuthenticationInfoValidator.validate(simpleCasInfo);
	}
	
}
