package br.com.softplan.security.zap.api.analysis;

import org.zaproxy.clientapi.core.ClientApi;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.report.ZapReport;

public class ActiveScanOnlyAnalyzer extends BaseAnalyzer {

	public ActiveScanOnlyAnalyzer(String apiKey, ClientApi api) {
		super(apiKey, api);
	}

	public ZapReport analyze(AnalysisInfo analysisInfo) {
		init(analysisInfo.getAnalysisTimeoutInMillis());
		
		runActiveScan(analysisInfo);

		return generateReport();
	}
	
}
