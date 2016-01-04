package br.com.softplan.security.zap.api.model;

/**
 * Enumumerates the possible types of scans.
 * 
 * @author pdsec
 */
public enum ScanType {
	
	SPIDER("Spider"),
	AJAX_SPIDER("AJAX Spider"),
	ACTIVE_SCAN("Active Scan");
	
	private final String name;
	
	private ScanType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
