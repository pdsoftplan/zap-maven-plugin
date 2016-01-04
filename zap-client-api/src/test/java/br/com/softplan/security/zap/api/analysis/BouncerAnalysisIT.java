package br.com.softplan.security.zap.api.analysis;

import static org.testng.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.com.softplan.security.zap.api.ZapClient;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.report.ZapReport;
import br.com.softplan.security.zap.api.util.BaseIT;

@Test(groups = "analysis")
public class BouncerAnalysisIT extends BaseIT {

	private static final String BOUNCER_HTTP_URL = "http://server119:6969/bouncer-mock-saj";
	private static final String BOUNCER_URL = "https://server119.softplan.com.br:8443/bouncer-mock-saj";
	
	private static final String BOUNCER_LOGIN_URL = "https://server119.softplan.com.br:8443/bouncer-server/login";
	private static final String BOUNCER_USERNAME = "bob";
	private static final String BOUNCER_PASSWORD = "foo";
	private static final String BOUNCER_PROTECTED_PAGE = "https://server119.softplan.com.br:8443/bouncer-mock-saj/protected/index.jsp";
	private static final String BOUNCER_LOGGED_OUT_REGEX = "\\QLocation: https://server119.softplan.com.br:8443/bouncer-server/\\E.*";
//	private static final String BOUNCER_LOGOUT_URL = "https://server119.softplan.com.br:8443/bouncer-mock-saj/logout";
	
	@BeforeClass
	public void setUpClass() {
		this.zapClient = new ZapClient(zapInfo, buildAuthenticationInfo());
	}

	private AuthenticationInfo buildAuthenticationInfo() {
		return AuthenticationInfo.builder()
				.loggedOutRegex(BOUNCER_LOGGED_OUT_REGEX)
//				.excludeFromScan(BOUNCER_LOGOUT_URL)
				.buildCasAuthenticationInfo(BOUNCER_LOGIN_URL, BOUNCER_USERNAME, BOUNCER_PASSWORD, BOUNCER_PROTECTED_PAGE);
	}
	
	public void checkBouncerIsUp() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(BOUNCER_HTTP_URL).openConnection();
		conn.setRequestMethod("HEAD");
		int responseCode = conn.getResponseCode();
		
		assertEquals(responseCode, HttpURLConnection.HTTP_OK);
		
		Number d = null;
		
		System.out.println(d instanceof Integer);
		
	}
	
	class Test {
		private int a = x;
	}
	
	private int x = 3;
	public static void main(String[] args) {

		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(Long.MAX_VALUE));
		System.out.println(new Date(Long.MAX_VALUE));
		System.out.println(DateFormat.getInstance().format(c.getTime()));
		
		
//		System.out.println(c.getTime().getTime());
//		
//		c.set(Calendar.YEAR, 1500);
//		System.out.println(c.getTime().getTime());
//		
//		c.set(Calendar.YEAR, 1);
//		System.out.println(c.getTime().getTime());
//		
//		c.set(1970, 0, 1);
//		System.out.println(c.getTime().getTime());
//		
//		System.out.println(Long.MAX_VALUE);
//		System.out.println(Long.MIN_VALUE);
	}
	
	public static <T,E extends Number> T m(T t, E e) {
		return t;
	}

	
	public void testFullScanOnBouncerWithAuthentication() {
		ZapReport zapReport = zapClient.analyze(new AnalysisInfo(BOUNCER_URL, 480));
		assertTrue(protectedPageWasAccessed(zapReport));
	}
	
	private boolean protectedPageWasAccessed(ZapReport zapReport) {
		for (String url : zapReport.getSpiderResults()) {
			if (url.contains("bouncer-mock-saj/protected/main.jsp")) {
				return true;
			}
		}
		return false;
	}
	
}

