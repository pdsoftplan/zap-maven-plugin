package br.com.softplan.security.zap.api.report;

import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ZapReportUtilTest {

	private static final String TEST_DIR = System.getProperty("user.dir") + File.separator + "test";
	private ZapReport zapReport;
	
	private byte[] fakeReportBytes = {1,  2,  3, 4};
	private String[] fakeSpiderResults = {"url1", "url2"};
	
	@BeforeMethod
	public void setUp() {
		zapReport = new ZapReport(fakeReportBytes, fakeReportBytes, new ArrayList<String>(Arrays.asList(fakeSpiderResults)));
	}
	
	@Test
	public void saveHtmlReportTest() throws IOException {
		File report = ZapReportUtil.saveHtmlReport(zapReport, TEST_DIR);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapReport.html");
		assertEquals(Files.readAllBytes(report.toPath()), fakeReportBytes);
		
		report.delete();
	}
	
	@Test
	public void saveHtmlReportInDefaultLocationTest() throws IOException {
		File report = ZapReportUtil.saveHtmlReport(zapReport);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapReport.html");
		assertEquals(Files.readAllBytes(report.toPath()), fakeReportBytes);
		
		report.delete();
	}
	
	@Test
	public void saveXmlReportTest() throws IOException {
		File report = ZapReportUtil.saveXmlReport(zapReport, TEST_DIR);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapReport.xml");
		assertEquals(Files.readAllBytes(report.toPath()), fakeReportBytes);
		
		report.delete();
	}
	
	@Test
	public void saveXmlReportInDefaultLocationTest() throws IOException {
		File report = ZapReportUtil.saveXmlReport(zapReport);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapReport.xml");
		assertEquals(Files.readAllBytes(report.toPath()), fakeReportBytes);
		
		report.delete();
	}

	@Test
	public void saveHtmlSpiderResultsTest() throws IOException {
		File report = ZapReportUtil.saveHtmlSpiderResults(zapReport, TEST_DIR);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapSpiderResults.html");
		String content = new String(Files.readAllBytes(report.toPath()), StandardCharsets.UTF_8);
		assertTrue(content.contains("<html>"));
		assertTrue(content.contains("url1"));
		assertTrue(content.contains("url2"));
		
		report.delete();
	}
	
	@Test
	public void saveHtmlSpiderResultsInDefaultLocationTest() throws IOException {
		File report = ZapReportUtil.saveHtmlSpiderResults(zapReport);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapSpiderResults.html");
		String content = new String(Files.readAllBytes(report.toPath()), StandardCharsets.UTF_8);
		assertTrue(content.contains("<html>"));
		assertTrue(content.contains("url1"));
		assertTrue(content.contains("url2"));
		
		report.delete();
	}
	
	@Test
	public void saveXmlSpiderResultsTest() throws IOException {
		File report = ZapReportUtil.saveXmlSpiderResults(zapReport, TEST_DIR);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapSpiderResults.xml");
		String content = new String(Files.readAllBytes(report.toPath()), StandardCharsets.UTF_8);
		assertTrue(content.contains("<url>url1</url>"));
		assertTrue(content.contains("<url>url2</url>"));
		
		report.delete();
	}
	
	@Test
	public void saveXmlSpiderResultsInDefaultLocationTest() throws IOException {
		File report = ZapReportUtil.saveXmlSpiderResults(zapReport);
		
		assertTrue(report.isFile());
		assertEquals(report.getName(), "zapSpiderResults.xml");
		String content = new String(Files.readAllBytes(report.toPath()), StandardCharsets.UTF_8);
		assertTrue(content.contains("<url>url1</url>"));
		assertTrue(content.contains("<url>url2</url>"));
		report.delete();
	}
	
}
