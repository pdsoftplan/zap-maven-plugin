package br.com.softplan.security.zap.api.report;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Class that encapsulates the results of a ZAP analysis.
 * <p>
 * Reports are available in XML and HTML formats.
 * 
 * @author pdsec
 */
public class ZapReport {
	
	private byte[] htmlReport;
	private byte[] xmlReport;
	
	private List<String> spiderResults;
	private String htmlSpiderResults;
	private String xmlSpiderResults;
	
	/**
	 * Creates a new {@code ZapReport} instance based on the ZAP reports and the Spider results.
	 * 
	 * @param htmlReport byte representation of the HTML Report.
	 * @param xmlReport byte representation of the XML Report.
	 * @param spiderResults the list with the URLs visited by the Spider.
	 */
	public ZapReport(byte[] htmlReport, byte[] xmlReport, List<String> spiderResults) {
		this.htmlReport = htmlReport;
		this.xmlReport = xmlReport;
		this.spiderResults = spiderResults;
		
		buildSpiderResultsReports(spiderResults);
	}

	private void buildSpiderResultsReports(List<String> spiderResults) {
		this.htmlSpiderResults = SpiderResultsReportBuilder.buildHtmlReport(spiderResults);
		this.xmlSpiderResults = SpiderResultsReportBuilder.buildXmlReport(spiderResults);
	}
	
	public String getHtmlReportAsString() {
		return new String(this.htmlReport, StandardCharsets.UTF_8);
	}
	
	public byte[] getHtmlReport() {
		return this.htmlReport;
	}

	public String getXmlReportAsString() {
		return new String(this.xmlReport, StandardCharsets.UTF_8);
	}
	
	public byte[] getXmlReport() {
		return this.xmlReport;
	}
	
	public List<String> getSpiderResults() {
		return Collections.unmodifiableList(spiderResults);
	}
	
	public String getHtmlSpiderResultsAsString() {
		return htmlSpiderResults;
	}
	
	public byte[] getHtmlSpiderResults() {
		return htmlSpiderResults.getBytes(StandardCharsets.UTF_8);
	}
	
	public String getXmlSpiderResultsAsString() {
		return xmlSpiderResults;
	}
	
	public byte[] getXmlSpiderResults() {
		return xmlSpiderResults.getBytes();
	}
	
}
