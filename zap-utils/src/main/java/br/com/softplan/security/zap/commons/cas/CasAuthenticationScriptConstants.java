package br.com.softplan.security.zap.commons.cas;

public final class CasAuthenticationScriptConstants {

	public static final String JAVASCRIPT_ENGINE_IDENTIFIER = "ECMAScript";
	public static final String JAVASCRIPT_FILE_EXTENSION = ".js";
	
	public static final String SCRIPT_TYPE = "authentication";
	public static final String CAS_AUTH_SCRIPT_DESCRIPTION = "CAS authentication script";
	public static final String CAS_AUTH_SCRIPT_NAME = "cas-auth";
	public static final String CAS_AUTH_SCRIPT_FILE_NAME = CAS_AUTH_SCRIPT_NAME + JAVASCRIPT_FILE_EXTENSION;
	public static final String CAS_AUTH_SCRIPT_RELATIVE_PATH = "/scripts/cas-auth.js";
	public static final String CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH = "/zap/scripts/";
	
	private CasAuthenticationScriptConstants() {}
	
}
