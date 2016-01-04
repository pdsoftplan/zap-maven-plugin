package br.com.softplan.security.zap.api.analysis;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import br.com.softplan.security.zap.api.exception.ZapAnalysisTimeoutException;
import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AnalysisType;
import br.com.softplan.security.zap.api.util.BaseIT;

public class AnalyzerIT extends BaseIT {

	private static final String TARGET_404_URL = "http://server17:8180/404";
	
	@Test(expectedExceptions = ZapAnalysisTimeoutException.class)
	public void shouldReachAnalysisTimeout() {
		zapClient.analyze(new AnalysisInfo(TARGET_404_URL, 0));
	}
	
	@Test
	public void ajaxSpiderTest() {
		zapClient.analyze(new AnalysisInfo(TARGET_404_URL, 480, AnalysisType.WITH_AJAX_SPIDER));
		// TODO: depois de integrar os resultados do AJAX Spider com os do Spider padrão, validar aqui que esses resultados foram computados
	}
	
	@Test
	public void shouldFailWithASpecificMessageWhenNavigationOnAScanTargetIsMissing() {
		try {
			this.zapClient.analyze(new AnalysisInfo(TARGET_404_URL, 480, AnalysisType.ACTIVE_SCAN_ONLY));
		} catch (ZapClientException e) {
			assertTrue(e.getMessage().equals("Error running Active Scan. "
						+ "One possible cause to this problem is that ZAP crashes when you try to run the Active Scan without any prior navigation made on the target "
						+ "(i.e. Spidering or proxied navigation were not done before on the Active Scan target)."));
		}
	}
	
}
