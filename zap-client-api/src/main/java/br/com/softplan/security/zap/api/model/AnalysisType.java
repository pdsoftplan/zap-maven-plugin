package br.com.softplan.security.zap.api.model;

/**
 * Enum to classify the types of possible analysis.
 * 
 * @author pdsec
 */
public enum AnalysisType {
	
	WITH_SPIDER, 
	WITH_AJAX_SPIDER, 
	ACTIVE_SCAN_ONLY, 
	SPIDER_ONLY, 
	SPIDER_AND_AJAX_SPIDER_ONLY;
	
}
