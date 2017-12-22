package br.com.softplan.security.zap.api.analysis;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import org.zaproxy.clientapi.core.ClientApi;

/**
 * Factory to create the correct {@link Analyzer} implementation based on the given {@link AnalysisInfo} instance.
 * 
 * @author pdsec
 */
public final class AnalyzerFactory {

	public static Analyzer makeAnalyzer(String apiKey, ClientApi api, AnalysisInfo analysisInfo) {
		switch (analysisInfo.getAnalysisType()) {
			case WITH_SPIDER:                 return new WithSpiderAnalyzer(apiKey, api);
			case WITH_AJAX_SPIDER:            return new WithAjaxSpiderAnalyzer(apiKey, api);
			case ACTIVE_SCAN_ONLY:            return new ActiveScanOnlyAnalyzer(apiKey, api);
			case SPIDER_ONLY:                 return new SpiderOnlyAnalyzer(apiKey, api);
			case SPIDER_AND_AJAX_SPIDER_ONLY: return new SpiderAndAjaxSpiderOnlyAnalyzer(apiKey, api);
			default:               return null;
		}
	}
	
	private AnalyzerFactory() {}
	
}
