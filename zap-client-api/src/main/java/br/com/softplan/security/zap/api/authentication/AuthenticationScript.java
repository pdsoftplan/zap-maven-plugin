package br.com.softplan.security.zap.api.authentication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;

public class AuthenticationScript {

	private static final String JAVASCRIPT_FILE_EXTENSION = ".js";
	
	public  static final String SCRIPT_RELATIVE_PATH = "/scripts/";
	public  static final String SCRIPT_DEFAULT_DOCKER_PATH = "/zap/scripts/";
	
	private String name;
	private String description;
	private String fileName;
	private String relativePath;
	private String path;
	
	private File scriptTempFile;
	
	public AuthenticationScript(String name, String description) {
		this.name = name;
		this.description = description;
		this.fileName = name + JAVASCRIPT_FILE_EXTENSION;
		this.relativePath = SCRIPT_RELATIVE_PATH + name + JAVASCRIPT_FILE_EXTENSION;
		
		this.path = AuthenticationScript.class.getResource(relativePath).getPath();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getFileName() {
		return fileName;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public String getPath(boolean isZapRunningOnDocker) throws IOException, URISyntaxException {
		if (isZapRunningOnDocker) {
			return SCRIPT_DEFAULT_DOCKER_PATH + fileName;
		}
		if (scriptFileIsNotAccessible()) {
			return getCasAuthScriptTempFile().getAbsolutePath();
		}
		return path;
	}
	
	private boolean scriptFileIsNotAccessible() throws UnsupportedEncodingException {
		return !new File(path).exists();
	}
	
	private File getCasAuthScriptTempFile() throws IOException {
		if (scriptTempFile == null) {
			scriptTempFile = createCasAuthScriptTempFile();
		}
		return scriptTempFile;
	}
	
	private File createCasAuthScriptTempFile() throws IOException {
		File tempFile = File.createTempFile(name, JAVASCRIPT_FILE_EXTENSION);
		tempFile.deleteOnExit();
		
		InputStream casAuthScriptInputStream = AuthenticationScript.class.getResourceAsStream(relativePath);
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
			IOUtils.copy(casAuthScriptInputStream, fileOutputStream);
		}
		return tempFile;
	}
	
}
