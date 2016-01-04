package br.com.softplan.security.zap.api.report;

import java.util.List;

/**
 * Class that builds the HTML and XML reports with the Spider results.
 * 
 * @author pdsec
 */
public final class SpiderResultsReportBuilder {

	private static final String TITLE = "ZAP Spider Results";
	private static final String HEADER = "Visited URLs";
	
	public static String buildHtmlReport(List<String> urls) {
		StringBuilder builder = new StringBuilder();
		builder.append("<html>\n");
		
		builder.append("  <head>\n");
		builder.append("    <META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
		builder.append("    <title>").append(TITLE).append("</title>\n");
		builder.append("  </head>\n");
		
		builder.append("  <body text=\"#000000\">\n");
		builder.append("    <p><strong>").append(TITLE).append("</strong></p>\n");
		
		builder.append("    <table width=\"100%\" border=\"0\">\n");
		builder.append("      <tr bgcolor=\"#666666\" height=\"24\">\n");
		builder.append("      <td><strong><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">")
			   .append(HEADER).append("</font></strong></td>\n");
		
		for (String url : urls) {
			builder.append("      <tr bgcolor=\"#e8e8e8\"><td><blockquote><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">")
				   .append(url).append("</font></blockquote></td></tr>\n");
		}
		
		builder.append("    </table>\n");
		builder.append("  </body>\n");
		builder.append("</html>\n");
		
		return builder.toString();
	}
	
	public static String buildXmlReport(List<String> urls) {
		StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		builder.append("<urls>\n");
		for (String url : urls) {
			builder.append("  <url>").append(url).append("</url>\n");
		}
		builder.append("</urls>\n");
		
		return builder.toString();
	}
	
	private SpiderResultsReportBuilder() {}
	
}
