package br.com.softplan.security.zap.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.api.analysis.Analyzer;
import br.com.softplan.security.zap.api.analysis.AnalyzerFactory;
import br.com.softplan.security.zap.api.authentication.AuthenticationHandler;
import br.com.softplan.security.zap.api.authentication.AuthenticationHandlerFactory;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.report.ZapReport;
import br.com.softplan.security.zap.commons.ZapInfo;
import br.com.softplan.security.zap.zaproxy.clientapi.core.ClientApi;

/**
 * The main client for starting a ZAP analysis.
 * 
 * @author pdsec
 */
public class ZapClient {

	private String apiKey;
	private ClientApi api;
	
	private AuthenticationHandler authenticationHandler;
	private SessionManager sessionManager;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZapClient.class);

	/**
	 * Constructs the client providing information about ZAP.
	 * 
	 * @param zapInfo required information about the ZAP instance.
	 */
	public ZapClient(ZapInfo zapInfo) {
		this.apiKey = zapInfo.getApiKey();
		this.api = new ClientApi(zapInfo.getHost(), zapInfo.getPort());
		this.sessionManager = new SessionManager();
	}

	/**
	 * Constructs the client providing information about ZAP and the authentication to be done.
	 * 
	 * @param zapInfo required information about the ZAP instance.
	 * @param authenticationInfo information about the authentication to be done.
	 */
	public ZapClient(ZapInfo zapInfo, AuthenticationInfo authenticationInfo) {
		this(zapInfo);
		this.authenticationHandler = AuthenticationHandlerFactory.makeHandler(api, zapInfo, authenticationInfo);
	}

	public ZapReport analyze(AnalysisInfo analysisInfo) {
		if (analysisInfo.shouldStartNewSession()) {
			sessionManager.createNewSession(api, apiKey);
		}
		
		if (authenticationHandler != null) {
			authenticationHandler.handleAuthentication(analysisInfo.getTargetUrl());
		}

		LOGGER.info("--- Starting analysis ---");
		
		Analyzer analyzer = AnalyzerFactory.makeAnalyzer(apiKey, api, analysisInfo);
		ZapReport zapReport = analyzer.analyze(analysisInfo);
		
		LOGGER.info("--- Finished analysis ---\n");
		
		return zapReport;
	}

}
