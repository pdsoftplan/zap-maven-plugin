package br.com.softplan.security.zap.api.analysis;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.report.ZapReport;
import org.zaproxy.clientapi.core.ClientApi;

public class WithAjaxSpiderAnalyzer extends BaseAnalyzer {
	
	public WithAjaxSpiderAnalyzer(String apiKey, ClientApi api) {
		super(apiKey, api);
	}
	
	public ZapReport analyze(AnalysisInfo analysisInfo) {
		init(analysisInfo.getAnalysisTimeoutInMillis());
		
		runSpider(analysisInfo);
		runAjaxSpider(analysisInfo);
		runActiveScan(analysisInfo);

		return generateReport();
	}
	
}
