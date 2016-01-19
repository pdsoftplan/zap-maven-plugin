package br.com.softplan.security.zap.api.authentication;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.commons.ZapInfo;

public class WebDriverFactory {

	private static final int FIREFOX_MANUAL_PROXY_CONFIGURATION_OPTION = 1;
	
	public static WebDriver makeWebDriver(ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		String host = zapInfo.getHost();
		int port = zapInfo.getPort();
		
		switch (authenticationInfo.getSeleniumDriver()) {
			case HTML_UNIT: return makeHtmlUnitDriver(host, port);
			case FIREFOX:   return makeFirefoxDriver(host, port);
			case CHROME:    return makeChromeDriver(host, port);
			default:        return makeFirefoxDriver(host, port);
		}
	}
	
	public static HtmlUnitDriver makeHtmlUnitDriver(String host, int port) {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setProxy(host, port);
		return driver;
	}
	
	public static FirefoxDriver makeFirefoxDriver(String host, int port) {
		FirefoxProfile profile = new FirefoxProfile();
	    profile.setPreference("network.proxy.type", FIREFOX_MANUAL_PROXY_CONFIGURATION_OPTION);
	    profile.setPreference("network.proxy.share_proxy_settings", true);
	    profile.setPreference("network.proxy.no_proxies_on", "");
	    profile.setPreference("network.proxy.http", host);
	    profile.setPreference("network.proxy.http_port", port);
	    
	    return new FirefoxDriver(profile);
	}
	
	public static ChromeDriver makeChromeDriver(String host, int port) {
		Proxy proxy = new Proxy();
	    proxy.setHttpProxy(host + ":" + port);
	    
	    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
	    capabilities.setCapability("proxy", proxy);
	    
	    return new ChromeDriver(capabilities);
	}
	
	private WebDriverFactory() {}
	
}
