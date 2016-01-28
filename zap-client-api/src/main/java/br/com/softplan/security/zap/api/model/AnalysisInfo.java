package br.com.softplan.security.zap.api.model;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class that represents the information about the analysis that will be executed by ZAP.
 * 
 * @author pdsec
 */
public class AnalysisInfo {

	private static final long DEFAULT_ANALYSIS_TIMEOUT_IN_MINUTES = 480;
	private static final AnalysisType DEFAULT_ANALYSIS_TYPE = AnalysisType.WITH_SPIDER;
	private static final boolean DEFAULT_SHOULD_START_NEW_SESSION = true;
	
	private String targetUrl;
	private String spiderStartingPointUrl;
	private String activeScanStartingPointUrl;
	private String[] context;
	private String[] technologies;
	private String technologiesSeparatedByComma;
	
	private long analysisTimeoutInMinutes;
	private AnalysisType analysisType;
	private boolean shouldStartNewSession;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public String getSpiderStartingPointUrl() {
		if (spiderStartingPointUrl != null) {
			return spiderStartingPointUrl;
		}
		return targetUrl;
	}

	public String getActiveScanStartingPointUrl() {
		if (activeScanStartingPointUrl != null) {
			return activeScanStartingPointUrl;
		}
		return targetUrl;
	}
	
	public String[] getContext() {
		if (context != null && context.length > 0) {
			return context;
		}
		return new String[]{targetUrl};
	}

	public String[] getTechnologies() {
		return technologies;
	}
	
	public String getTechnologiesSeparatedByComma() {
		return technologiesSeparatedByComma;
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
	
	public boolean shouldStartNewSession() {
		return shouldStartNewSession;
	}
	
	public static class Builder {
		
		private String targetUrl;
		private String spiderStartingPointUrl;
		private String activeScanStartingPointUrl;
		private String[] context;
		private String[] technologies;
		private String technologiesSeparatedByComma;
		private long analysisTimeoutInMinutes = DEFAULT_ANALYSIS_TIMEOUT_IN_MINUTES;
		private AnalysisType analysisType = DEFAULT_ANALYSIS_TYPE;
		private boolean shouldStartNewSession = DEFAULT_SHOULD_START_NEW_SESSION;
		
		/**
		 * Sets the target URL.
		 * 
		 * @param targetUrl URL of the application that will be analyzed (e.g. {@code http://myapp.com}).
		 * @return this {@code Builder} instance.
		 */
		public Builder targetUrl(String targetUrl) {
			this.targetUrl = targetUrl;
			return this;
		}
		
		/**
		 * Sets the starting point URL for the Spider (and AJAX Spider, in case it runs).
		 * 
		 * @param spiderStartingPointUrl the starting point URL for the Spiders (default: {@code targetUrl}). 
		 * @return this {@code Builder} instance.
		 */
		public Builder spiderStartingPointUrl(String spiderStartingPointUrl) {
			this.spiderStartingPointUrl = spiderStartingPointUrl;
			return this;
		}
		
		/**
		 * Sets the starting point URL for the Active Scan.
		 * 
		 * @param activeScanStartingPointUrl the starting point URL for the Active Scan (default: {@code targetUrl}). 
		 * @return this {@code Builder} instance.
		 */
		public Builder activeScanStartingPointUrl(String activeScanStartingPointUrl) {
			this.activeScanStartingPointUrl = activeScanStartingPointUrl;
			return this;
		}
		
		/**
		 * Sets the URLs to be set as the context of ZAP.
		 * 
		 * @param context an array of URLs (absolute or relative) to be set on ZAP's context.
		 * @return this {@code Builder} instance.
		 */
		public Builder context(String... context) {
			this.context = context;
			return this;
		}
		
		/**
		 * Sets the technologies that will be considered during the scan.
		 * The default behavior is to consider all the technologies.
		 * 
		 * @param technologies an array of technologies to be considered during the scan.
		 * @return this {@code Builder} instance.
		 */
		public Builder technologies(String... technologies) {
			this.technologies = technologies;
			if (technologies != null) {
				technologiesSeparatedByComma = StringUtils.join(technologies, ","); 
			} else {
				technologiesSeparatedByComma = null;
			}
			return this;
		}
		
		/**
		 * Sets the analysis timeout in minutes.
		 * 
		 * @param analysisTimeoutInMinutes the timeout in minutes for the analysis (default: {@code 480}). 
		 * @return this {@code Builder} instance.
		 */
		public Builder analysisTimeoutInMinutes(long analysisTimeoutInMinutes) {
			this.analysisTimeoutInMinutes = analysisTimeoutInMinutes;
			return this;
		}
		
		/**
		 * Sets the analysis type.
		 * 
		 * @param analysisType the analysis type indicating which analysis should be done:
		 * <ul>
		 *     <li><b>WITH_SPIDER</b>: default analysis, which runs the Spider before runnning the Active Scan.</li>
	  	 *     <li><b>WITH_AJAX_SPIDER</b>: after running the default Spider, the AJAX Spider is executed before the Active Scan.
	 	 *                                  This is useful for applications that rely on AJAX.</li>
	 	 *     <li><b>ACTIVE_SCAN_ONLY</b>: no Spider is executed before the Active Scan. This is useful in case the application
	 	 *                                  navigation is done via proxy (proxied Selenium tests, for instance).</li>
	 	 *     <li><b>SPIDER_ONLY</b>: </li> no Active Scan is executed after the Spider. This is useful when on wants to run the
	 	 *                                   passive scan only.
	 	 *     <li><b>SPIDER_AND_AJAX_SPIDER_ONLY</b>: Just like the previous, but including the AJAX Spider.</li>
	 	 * </ul> 
		 * @return this {@code Builder} instance.
		 */
		public Builder analysisType(AnalysisType analysisType) {
			this.analysisType = analysisType;
			return this;
		}
		
		/**
		 * Sets the analysis type.
		 * 
		 * @param analysisType the analysis type as a string (case-insensitive) indicating which analysis should be done:
		 * <ul>
		 *     <li><b>WITH_SPIDER</b>: default analysis, which runs the Spider before runnning the Active Scan.</li>
	  	 *     <li><b>WITH_AJAX_SPIDER</b>: after running the default Spider, the AJAX Spider is executed before the Active Scan.
	 	 *                                  This is useful for applications that rely on AJAX.</li>
	 	 *     <li><b>ACTIVE_SCAN_ONLY</b>: no Spider is executed before the Active Scan. This is useful in case the application
	 	 *                                  navigation is done via proxy (proxied Selenium tests, for instance).</li>
	 	 *     <li><b>SPIDER_ONLY</b>: </li> no Active Scan is executed after the Spider. This is useful when on wants to run the
	 	 *                                   passive scan only.
	 	 *     <li><b>SPIDER_AND_AJAX_SPIDER_ONLY</b>: Just like the previous, but including the AJAX Spider.</li>
	 	 * </ul> 
		 * @return this {@code Builder} instance.
		 */
		public Builder analysisType(String analysisType) {
			if (analysisType != null) {
				this.analysisType = AnalysisType.valueOf(analysisType.toUpperCase());
			}
			return this;
		}
		
		/**
		 * Sets whether a new session should be started on ZAP before the analysis.
		 * 
		 * @param shouldStartNewSession {@code true} if a new session on ZAP should be started before the analysis,
		 *                              {@code false} otherwise (default: {@code true}). 
		 * @return this {@code Builder} instance.
		 */
		public Builder shouldStartNewSession(boolean shouldStartNewSession) {
			this.shouldStartNewSession = shouldStartNewSession;
			return this;
		}
		
		/**
		 * Builds a {@link AnalysisInfo} instance based on the builder parameters.
		 * 
		 * @return a {@link AnalysisInfo} instance.
		 */
		public AnalysisInfo build() {
			return new AnalysisInfo(this);
		}
		
	}
	
	private AnalysisInfo(Builder builder) {
		this.targetUrl                    = builder.targetUrl;
		this.spiderStartingPointUrl       = builder.spiderStartingPointUrl;
		this.activeScanStartingPointUrl   = builder.activeScanStartingPointUrl;
		this.context                      = builder.context;
		this.technologies                 = builder.technologies;
		this.technologiesSeparatedByComma = builder.technologiesSeparatedByComma;
		this.analysisTimeoutInMinutes     = builder.analysisTimeoutInMinutes;
		this.analysisType                 = builder.analysisType;
		this.shouldStartNewSession        = builder.shouldStartNewSession;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("targetUrl", targetUrl)
				.append("spiderStartingPointUrl", spiderStartingPointUrl)
				.append("activeScanStartingPointUrl", activeScanStartingPointUrl)
				.append("context", Arrays.toString(context))
				.append("technologies", Arrays.toString(technologies))
				.append("analysisTimeoutInMinutes", analysisTimeoutInMinutes)
				.append("analysisType", analysisType)
				.append("shouldStartNewSession", shouldStartNewSession)
				.toString();
	}
	
}
