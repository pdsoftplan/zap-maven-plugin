package br.com.softplan.security.zap.api.report;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ZapReportTest {

	private static final String HTML_REPORT_CONTENT = "htmlReport";
	private static final String XML_REPORT_CONTENT = "xmlReport";
	private static final String[] SPIDER_RESULTS = {"url1", "url2"};

	public ZapReport zapReport;
	
	@BeforeMethod
	public void setUp() {
		zapReport = new ZapReport(HTML_REPORT_CONTENT.getBytes(), XML_REPORT_CONTENT.getBytes(), new ArrayList<String>(Arrays.asList(SPIDER_RESULTS)));
	}
	
	@Test
	public void testReportContents() {
		assertEquals(zapReport.getHtmlReport(), HTML_REPORT_CONTENT.getBytes());
		assertEquals(zapReport.getXmlReport(), XML_REPORT_CONTENT.getBytes());
		assertEquals(zapReport.getHtmlReportAsString(), HTML_REPORT_CONTENT);
		assertEquals(zapReport.getXmlReportAsString(), XML_REPORT_CONTENT);
		
		assertTrue(zapReport.getSpiderResults().contains("url1"));
		assertTrue(zapReport.getSpiderResults().contains("url2"));
		assertTrue(zapReport.getHtmlSpiderResultsAsString().contains("url1"));
		assertTrue(zapReport.getHtmlSpiderResultsAsString().contains("url2"));
		assertTrue(zapReport.getXmlSpiderResultsAsString().contains("<url>url2</url>"));
		assertTrue(zapReport.getXmlSpiderResultsAsString().contains("<url>url2</url>"));
	}
	
	@Test
	public void testByteAndStringReportsEquality() {
		assertEquals(new String(zapReport.getXmlReport()), zapReport.getXmlReportAsString());
		assertEquals(new String(zapReport.getHtmlReport()), zapReport.getHtmlReportAsString());
		assertEquals(new String(zapReport.getHtmlSpiderResults()), zapReport.getHtmlSpiderResultsAsString());
		assertEquals(new String(zapReport.getXmlSpiderResults()), zapReport.getXmlSpiderResultsAsString());
	}
	
}
