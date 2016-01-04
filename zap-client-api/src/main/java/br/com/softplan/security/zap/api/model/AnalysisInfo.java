package br.com.softplan.security.zap.api.model;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class that represents the information about the analysis that will be executed by ZAP.
 * 
 * @author pdsec
 */
public class AnalysisInfo {

	private String targetUrl;
	private long analysisTimeoutInMinutes;
	private AnalysisType analysisType = AnalysisType.WITH_SPIDER;
	
	/**
	 * Creates an {@code AnalysisInfo} instance.
	 * 
	 * @param targetUrl the address of the application that will be analyzed (i.e. {@code http://myapp.com}).
	 * @param analysisTimeoutInMinutes the timeout in minutes for the analysis.
	 */
	public AnalysisInfo(String targetUrl, long analysisTimeoutInMinutes) {
		this.targetUrl = targetUrl;
		this.analysisTimeoutInMinutes = analysisTimeoutInMinutes;
	}

	/**
	 * Creates an {@code AnalysisInfo} instance.
	 * 
	 * @param targetUrl the address of the application that will be analyzed (i.e. {@code http://myapp.com}).
	 * @param analysisTimeoutInMinutes the timeout in minutes for the analysis.
	 * @param analysisType indicates which analysis should be done:
	 * <ul>
	 *     <li><b>WITH_SPIDER</b>: default analysis, which runs the Spider before runnning the Active Scan.</li>
	 *     <li><b>WITH_AJAX_SPIDER</b>: after running the default Spider, the AJAX Spider is executed before the Active Scan.
	 *                                  This is useful for applications that rely on AJAX.</li>
	 *     <li><b>ACTIVE_SCAN_ONLY</b>: no Spider is executed before the Active Scan. This is useful in case the application
	 *                                  navigation is done via proxy (proxied Selenium tests, for instance).</li>
	 * </ul>
	 */
	public AnalysisInfo(String targetUrl, long analysisTimeoutInMinutes, AnalysisType analysisType) {
		this.targetUrl = targetUrl;
		this.analysisTimeoutInMinutes = analysisTimeoutInMinutes;
		this.analysisType = analysisType;
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public long getAnalysisTimeoutInMillis() {
		return TimeUnit.MILLISECONDS.convert(analysisTimeoutInMinutes, TimeUnit.MINUTES);
	}
	
	public long getAnalysisTimeoutInMinutes() {
		return analysisTimeoutInMinutes;
	}

	public AnalysisType getAnalysisType() {
		return analysisType;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("targetUrl", targetUrl)
				.append("analysisTimeoutInMinutes", analysisTimeoutInMinutes)
				.append("analysisType", analysisType)
				.toString();
	}
	
}
