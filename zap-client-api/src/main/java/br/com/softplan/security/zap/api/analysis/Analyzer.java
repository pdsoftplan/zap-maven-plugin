package br.com.softplan.security.zap.api.analysis;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.report.ZapReport;

/**
 * Implementations of this class represent different kinds of analysis that can be executed on ZAP.
 * 
 * @author pdsec
 */
public interface Analyzer {

	/**
	 * Runs an analysis of the given target and generates the report. 
	 * 
	 * @param analysisInfo the information about the analysis to be executed.
	 * @return the report of the analysis.
	 */
	ZapReport analyze(AnalysisInfo analysisInfo);
}
