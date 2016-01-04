package br.com.softplan.security.zap.api.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.exception.ZapClientException;

/**
 * Class responsible to save ZAP reports.
 * 
 * @author pdsec
 */
public final class ZapReportUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZapReportUtil.class);
	
	private static final String DEFAULT_REPORTS_PATH = 
			System.getProperty("user.dir") + File.separator + "target" + File.separator + "zap-reports";
	
	/**
	 * Saves all available reports to the default path (target/zap-reports/). 
	 * 
	 * @param report the {@link ZapReport} holding the reports to be saved.
	 */
	public static void saveAllReports(ZapReport report) {
		saveHtmlReport(report);
		saveXmlReport(report);
		saveHtmlSpiderResults(report);
		saveXmlSpiderResults(report);
	}
	
	/**
	 * Saves all available reports to the given path. 
	 * 
	 * @param report the {@link ZapReport} holding the reports to be saved.
	 * @param path the path to save the reports.
	 */
	public static void saveAllReports(ZapReport report, String path) {
		saveHtmlReport(report, path);
		saveXmlReport(report, path);
		saveHtmlSpiderResults(report, path);
		saveXmlSpiderResults(report, path);
	}
	
	/**
	 * Saves the HTML report to the default path (target/zap-reports/).
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @return the saved HTML report as a {@code File} instance.
	 */
	public static File saveHtmlReport(ZapReport report) {
		return saveHtmlReport(report, DEFAULT_REPORTS_PATH);
	}

	/**
	 * Saves the HTML report to the given path.
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @param path the path to save the HTML report.
	 * @return the saved HTML report as a {@code File} instance.
	 */
	public static File saveHtmlReport(ZapReport report, String path) {
		return saveReport(report.getHtmlReport(), "zapReport.html", path);
	}

	/**
	 * Saves the XML report to the default path (target/zap-reports/).
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @return the saved XML report as a {@code File} instance.
	 */
	public static File saveXmlReport(ZapReport report) {
		return saveXmlReport(report, DEFAULT_REPORTS_PATH);
	}

	/**
	 * Saves the XML report to the given path.
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @param path the path to save the XML report.
	 * @return the saved XML report as a {@code File} instance.
	 */
	public static File saveXmlReport(ZapReport report, String path) {
		return saveReport(report.getXmlReport(), "zapReport.xml", path);
	}

	/**
	 * Saves the HTML Spider results report to the default path (target/zap-reports/).
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @return the saved HTML Spider results report as a {@code File} instance.
	 */
	public static File saveHtmlSpiderResults(ZapReport report) {
		return saveHtmlSpiderResults(report, DEFAULT_REPORTS_PATH);
	}
	
	/**
	 * Saves the HTML Spider results report to the given path.
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @param path the path to save the HTML Spider results report.
	 * @return the saved HTML Spider results report as a {@code File} instance.
	 */
	public static File saveHtmlSpiderResults(ZapReport report, String path) {
		return saveReport(report.getHtmlSpiderResults(), "zapSpiderResults.html", path);
	}
	
	/**
	 * Saves the XML Spider results report to the default path (target/zap-reports/).
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @return the saved XML Spider results report as a {@code File} instance.
	 */
	public static File saveXmlSpiderResults(ZapReport report) {
		return saveXmlSpiderResults(report, DEFAULT_REPORTS_PATH);
	}
	
	/**
	 * Saves the XML Spider results report to the given path.
	 * 
	 * @param report the {@link ZapReport} holding the report to be saved.
	 * @param path the path to save the XML Spider results report.
	 * @return the saved XML Spider results report as a {@code File} instance.
	 */
	public static File saveXmlSpiderResults(ZapReport report, String path) {
		return saveReport(report.getXmlSpiderResults(), "zapSpiderResults.xml", path);
	}
	
	private static File saveReport(byte[] report, String name, String path) {
		createReportsFolder(path);
		File reportFile = new File(path, name);
		
		try (OutputStream fos = new FileOutputStream(reportFile)) {
			fos.write(report);
			LOGGER.info("{} saved to {}", name, path);
		} catch(IOException e) {
			String message = "Error saving reports.";
			LOGGER.error(message, e);
			throw new ZapClientException(message, e);
		}
		
		return reportFile;
	}
	
	private static void createReportsFolder(String path) {
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e) {
			String message = "Error creating reports folder.";
			LOGGER.error(message, e);
			throw new ZapClientException(message, e);
		}
	}

	private ZapReportUtil() {}
	
}
