package br.com.softplan.security.zap.api.analysis;

import static org.testng.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.com.softplan.security.zap.api.ZapClient;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.report.ZapReport;
import br.com.softplan.security.zap.api.util.ZapProperties;
import br.com.softplan.security.zap.commons.ZapInfo;

@Test(groups = "analysis")
public class BodgeitAnalysisIT {

	private ZapClient zapClient;

	private static final String BODGEIT_URL = "http://server17:8180/bodgeit";
	private static final String BODGEIT_LOGIN_URL = "http://server17:8180/bodgeit/login.jsp";
	private static final String BODGEIT_USERNAME = "zaptest@test.com";
	private static final String BODGEIT_PASSWORD = "zaptest@test.com";
	private static final String BODGEIT_LOGGED_IN_REGEX = "\\Q<a href=\"logout.jsp\">Logout</a>\\E";
	
	@BeforeClass
	public void setUpClass() {
		ZapInfo zapInfo = new ZapInfo.Builder().buildToUseRunningZap(ZapProperties.getHost(), ZapProperties.getPort());
		this.zapClient = new ZapClient(zapInfo, buildAuthenticationInfo());
	}

	private AuthenticationInfo buildAuthenticationInfo() {
		return AuthenticationInfo.builder()
				.loggedInRegex(BODGEIT_LOGGED_IN_REGEX)
				.buildFormAuthenticationInfo(BODGEIT_LOGIN_URL, BODGEIT_USERNAME, BODGEIT_PASSWORD);
	}
	
	@Test
	public void checkBodgeitIsUp() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(BODGEIT_URL).openConnection();
		conn.setRequestMethod("HEAD");
		int responseCode = conn.getResponseCode();

		assertEquals(responseCode, HttpURLConnection.HTTP_OK);
	}

	@Test
	public void testFullScanOnBodgeit() {
		ZapReport zapReport = zapClient.analyze(new AnalysisInfo(BODGEIT_URL, 480));
		assertTrue(protectedPageWasAccessed(zapReport));
	}

	private boolean protectedPageWasAccessed(ZapReport zapReport) {
		for (String url : zapReport.getSpiderResults()) {
			if (url.contains("bodgeit/password.jsp")) {
				return true;
			}
		}
		return false;
	}

}
