package br.com.softplan.security.zap.api.analysis;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.report.ZapReport;
import org.zaproxy.clientapi.core.ClientApi;

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
