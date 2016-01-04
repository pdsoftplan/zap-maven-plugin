package br.com.softplan.security.zap.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AnalysisType;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.model.AuthenticationType;
import br.com.softplan.security.zap.api.report.ZapReport;
import br.com.softplan.security.zap.api.report.ZapReportUtil;
import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Abstract Mojo used as a base for the other ZAP Mojos. 
 * 
 * @author pdsec
 */
public abstract class ZapMojo extends AbstractMojo {
	
	// Analysis
	@Parameter(required=true) private String target;
	@Parameter(defaultValue="480") private int analysisTimeoutInMinutes;
	@Parameter private boolean shouldRunAjaxSpider;
	
	// ZAP
	@Parameter(required=true) private Integer zapPort;
	@Parameter private String zapHost;
	@Parameter private String zapApiKey;
	@Parameter private String zapPath;
	@Parameter private String zapOptions;
	@Parameter private boolean shouldRunWithDocker;
	@Parameter(defaultValue="120000") private Integer initializationTimeoutInMillis;
	@Parameter private File reportPath;

	// Authentication
	@Parameter private String authenticationType;
	@Parameter private String loginUrl;
	@Parameter private String username;
	@Parameter private String password;
	@Parameter private String extraPostData;
	@Parameter private String loggedInRegex;
	@Parameter private String loggedOutRegex;
	@Parameter private String[] excludeFromScan;
	
	// CAS
	@Parameter private String[] protectedPages;

	// Form
	@Parameter(defaultValue="username") private String usernameParameter;
	@Parameter(defaultValue="password") private String passwordParameter;
	
	protected ZapInfo buildZapInfo() {
		return ZapInfo.builder()
				.host   (zapHost)
				.port   (zapPort)
				.apiKey (zapApiKey)
				.path   (zapPath)
				.options(zapOptions)
				.initializationTimeoutInMillis((long) initializationTimeoutInMillis)
				.shouldRunWithDocker(shouldRunWithDocker)
				.build();
	}
	
	protected AuthenticationInfo buildAuthenticationInfo() {
		if (authenticationType == null) {
			return null;
		}
		return AuthenticationInfo.builder()
				.type            (AuthenticationType.valueOf(authenticationType.toUpperCase()))
				.loginUrl        (loginUrl)
				.username        (username)
				.password        (password)
				.extraPostData   (extraPostData)
				.loggedInRegex   (loggedInRegex)
				.loggedOutRegex  (loggedOutRegex)
				.excludeFromScan (excludeFromScan)
				.protectedPages  (protectedPages)
				.loginRequestData(usernameParameter + "={%username%}&" + passwordParameter + "={%password%}")
				.build();
	}
	
	protected AnalysisInfo buildAnalysisInfo() {
		AnalysisType analysisType = AnalysisType.WITH_SPIDER;
		if (shouldRunAjaxSpider) {
			analysisType = AnalysisType.WITH_AJAX_SPIDER;
		}
		return new AnalysisInfo(target, analysisTimeoutInMinutes, analysisType);
	}
	
	protected AnalysisInfo buildAnalysisInfo(AnalysisType analysisType) {
		return new AnalysisInfo(target, analysisTimeoutInMinutes, analysisType);
	}
	
	protected void saveReport(ZapReport zapReport) {
		getLog().info("Saving Reports...");
		if (reportPath != null) {
			ZapReportUtil.saveAllReports(zapReport, reportPath.getAbsolutePath());
		} else {
			ZapReportUtil.saveAllReports(zapReport);
		}
	}
	
	protected String getTarget() {
		return this.target;
	}
	
}
