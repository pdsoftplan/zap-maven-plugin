package br.com.softplan.security.zap.commons.boot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.commons.ZapInfo;
import br.com.softplan.security.zap.commons.exception.ZapInitializationException;

/**
 * Class responsible to start and stop ZAP locally.
 * <p>
 * <b>ZAP must be installed locally for this to work.</b>
 * <p>
 * This will normally be used when ZAP's {@code zapInfo.path} was provided and {@code shouldRunWithDocker} is false.
 * 
 * @author pdsec
 */
class ZapLocalBoot extends AbstractZapBoot {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZapLocalBoot.class);
	
	private static final String DEFAULT_ZAP_START_COMMAND = "java -Xmx512m -jar ";
	
	private static Process zap;
	
	@Override
	public void startZap(ZapInfo zapInfo) {
		int port = zapInfo.getPort();
		
		if (isZapRunning(port)) {
			LOGGER.info("ZAP is already up and running! No attempts will be made to start ZAP.");
			return;
		}
		
		try {
			start(zapInfo);
			waitForZapInitialization(port, zapInfo.getInitializationTimeoutInMillis());
		} catch (IOException e) {
			LOGGER.error("Error starting ZAP.", e);
		}
	}
	
	@Override
	public void stopZap() {
		if (zap != null) {
			LOGGER.info("Stopping ZAP.");
			zap.destroy();
		}
	}

	private static void start(ZapInfo zapInfo) throws IOException {
		String startCommand = buildStartCommand(zapInfo);
		ProcessBuilder processBuilder = new ProcessBuilder(startCommand.split(" +"));
		processBuilder.directory(new File(zapInfo.getPath()));
		
		Files.createDirectories(Paths.get(DEFAULT_ZAP_LOG_PATH));
		processBuilder.redirectOutput(new File(DEFAULT_ZAP_LOG_PATH, DEFAULT_ZAP_LOG_FILE_NAME));
		
		LOGGER.info("Starting ZAP with command: {}", startCommand);
		zap = processBuilder.start();
	}
	
	private static String buildStartCommand(ZapInfo zapInfo) {
		StringBuilder startCommand = new StringBuilder(DEFAULT_ZAP_START_COMMAND);
		
		try {
			String zapJarName = retrieveZapJarName(zapInfo.getPath());
			startCommand.append(zapJarName).append(" ");
		} catch (IOException e) {
			LOGGER.error("Error retrieving ZAP's JAR file.");
		}
		
		String options = zapInfo.getOptions();
		startCommand.append(options != null ? options : DEFAULT_ZAP_OPTIONS);
		startCommand.append(" -port ").append(zapInfo.getPort());
		
		return startCommand.toString();
	}

	private static String retrieveZapJarName(String path) throws IOException {
		Path zapPath = Paths.get(path);
		for (Path p : Files.newDirectoryStream(zapPath)) {
			Path fileName = p.getFileName();
			if (fileName == null) {
				continue;
			}
			if (Files.isRegularFile(p) && fileName.toString().endsWith(".jar")) {
				return fileName.toString();
			}
		}
		throw new ZapInitializationException("ZAP's JAR file was not found.");
	}
	
}
