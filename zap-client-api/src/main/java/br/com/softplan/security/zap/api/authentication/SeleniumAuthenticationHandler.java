package br.com.softplan.security.zap.api.authentication;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;
import org.zaproxy.clientapi.core.ClientApi;

/**
 * Class to handle authentication via Selenium.
 * <p>
 * This will try to mimic the regular authentication process with Selenium.
 * It is particularly useful for more complex cases where it's easier to 
 * just open the browser and perform the authentication. 
 * 
 * @author pdsec
 */
public class SeleniumAuthenticationHandler extends AbstractAuthenticationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumAuthenticationHandler.class);
	
	protected SeleniumAuthenticationHandler(ClientApi api, ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		super(api, zapInfo, authenticationInfo);
	}
	
	@Override
	protected void setupAuthentication(String targetUrl) {
		addHttpSessionTokens(targetUrl);
		triggerAuthenticationViaWebDriver();
		setHttpSessionAsActive(targetUrl);
	}
	
	private void triggerAuthenticationViaWebDriver() {
		AuthenticationInfo authenticationInfo = getAuthenticationInfo();
		LOGGER.info("--- Performing authentication via Selenium ({}) ---", authenticationInfo.getSeleniumDriver());
		
		WebDriver driver = WebDriverFactory.makeWebDriver(getZapInfo(), authenticationInfo);
		driver.get(authenticationInfo.getLoginUrl());
		
		WebElement usernameField = driver.findElement(By.id(authenticationInfo.getUsernameParameter()));
		usernameField.sendKeys(authenticationInfo.getUsername());
		
		WebElement passwordField = driver.findElement(By.id(authenticationInfo.getPasswordParameter()));
		passwordField.sendKeys(authenticationInfo.getPassword());
		
		passwordField.submit();
		
		driver.quit();
		LOGGER.info("--- Finished performing authentication via Selenium ---\n");
	}
	
}
