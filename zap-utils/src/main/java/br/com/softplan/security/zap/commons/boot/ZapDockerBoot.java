package br.com.softplan.security.zap.commons.boot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.commons.ZapInfo;
import br.com.softplan.security.zap.commons.authentication.AuthenticationScripts;

/**
 * Class responsible to start and stop ZAP by running ZAP's Docker image.
 * <p>
 * <b>Docker must be installed locally for this to work.</b>
 * <p>
 * This will be used when {@code zapInfo.shouldRunWithDocker} is {@code true}.
 * 
 * @author pdsec
 */
public class ZapDockerBoot extends AbstractZapBoot {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZapDockerBoot.class);
	
	private static final String DEFAULT_DOCKER_COMMAND = "docker run --rm";
	private static final String ZAP_IMAGE_OPTION = " -i owasp/zap2docker-stable zap.sh ";
	
	public  static final String CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH = "/zap/scripts/";
	private static final String CAS_AUTH_SCRIPT_FILE_NAME = "cas-auth.js";
	
	private static Process zap;
	
	@Override
	public void startZap(ZapInfo zapInfo) {
		int port = zapInfo.getPort();

		if (isZapRunning(port)) {
			LOGGER.info("ZAP is already up and running! No attempts will be made to start ZAP.");
			return;
		}
		
		try {
			copyCasAuthScriptFileToMappedFolder();
			startZap(zapInfo.getPath(), buildStartCommand(zapInfo));
			waitForZapInitialization(port, zapInfo.getInitializationTimeoutInMillis());
		} catch (IOException e) {
			LOGGER.error("Error starting ZAP.", e);
		}
	}

	private static void copyCasAuthScriptFileToMappedFolder() {
		new File(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH).mkdirs();
		
		File scriptFile = new File(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH, CAS_AUTH_SCRIPT_FILE_NAME);
		
		InputStream casAuthScriptInputStream = ZapDockerBoot.class.getResourceAsStream(AuthenticationScripts.RELATIVE_PATH + CAS_AUTH_SCRIPT_FILE_NAME);
		try (FileOutputStream fileOutputStream = new FileOutputStream(scriptFile)) {
			IOUtils.copy(casAuthScriptInputStream, fileOutputStream);
		} catch (IOException e) {
			LOGGER.error("Error while trying to create the script file for CAS authentication in " + CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH + ". "
					+ "The analysis will continue but CAS authentication will work only if the script file can be accessed by ZAP's Docker image "
					+ "(a default volume is created in " + CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH + ").", e);
		}
	}
	
	@Override
	public void stopZap() {
		if (zap != null) {
			LOGGER.info("Stopping ZAP.");
			zap.destroy();
		}
	}

	private static String buildStartCommand(ZapInfo zapInfo) {
		StringBuilder startCommand = new StringBuilder(DEFAULT_DOCKER_COMMAND);
		appendVolumeOption(startCommand);
		appendPortOption(zapInfo, startCommand);
		startCommand.append(ZAP_IMAGE_OPTION);
		
		String options = zapInfo.getOptions();
		startCommand.append(options != null ? options : DEFAULT_ZAP_OPTIONS);
		startCommand.append(" -port ").append(zapInfo.getPort());
		
		return startCommand.toString();
	}
	
	private static void appendVolumeOption(StringBuilder startCommand) {
		startCommand.append(" -v ");
		startCommand.append(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH);
		startCommand.append(":");
		startCommand.append(CAS_AUTH_SCRIPT_DEFAULT_DOCKER_PATH);
		startCommand.append(":ro");
	}

	private static void appendPortOption(ZapInfo zapInfo, StringBuilder startCommand) {
		startCommand.append(" -p ");
		startCommand.append(zapInfo.getPort());
		startCommand.append(":");
		startCommand.append(zapInfo.getPort());
	}
	
	private static void startZap(String path, String startCommand) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(startCommand.split(" +"));
//		processBuilder.directory(new File(path));
		
		Files.createDirectories(Paths.get(DEFAULT_ZAP_LOG_PATH));
		processBuilder.redirectOutput(new File(DEFAULT_ZAP_LOG_PATH, DEFAULT_ZAP_LOG_FILE_NAME));
		
		LOGGER.info("Starting ZAP with command: {}", startCommand);
		zap = processBuilder.start();
	}
	
}
