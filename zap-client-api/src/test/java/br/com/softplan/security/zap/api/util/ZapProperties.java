package br.com.softplan.security.zap.api.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for loading the properties from the zap.properties file,
 * with the properties needed to start ZAP for the integration tests.
 * 
 * @author pdsec
 */
public class ZapProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZapProperties.class);
	
	private static final String ZAP_PROPERTIES_FILE_PATH = "/zap.properties";
	private static final String LOCAL_ZAP_PROPERTIES_FILE_PATH = "/local-zap.properties";
	
	private static final String ZAP_HOST_PROPERTY = "zap.host";
	private static final String ZAP_PORT_PROPERTY = "zap.port";
	private static final String ZAP_PATH_PROPERTY = "zap.path";
	private static final String ZAP_OPTIONS_PROPERTY = "zap.options";
	private static final String ZAP_INITIALIZATION_TIMEOUT_PROPERTY = "zap.initializationTimeout";
	
	private static PropertiesConfiguration properties;
	
	private static String host;
	
	private static Integer port;
	
	private static String path;
	
	private static Long initializationTimeout;
	
	private static String options;

	private static Configuration getProperties() {
		if (properties == null) {
			loadProperties();
		}
		return properties;
	}

	private static void loadProperties() {
		LOGGER.info("Loading ZAP properties files.");
		properties = new PropertiesConfiguration();
		
		try (
			InputStream localZapPropertiesStream = ZapProperties.class.getResourceAsStream(LOCAL_ZAP_PROPERTIES_FILE_PATH);
			InputStream zapPropertiesStream = ZapProperties.class.getResourceAsStream(ZAP_PROPERTIES_FILE_PATH);
		) {
			if (localZapPropertiesStream != null) {
				LOGGER.info("Loading 'local-zap.properties' file.");
				properties.load(localZapPropertiesStream);
			}
			
			LOGGER.info("Loading 'zap.properties' file.");
			properties.load(zapPropertiesStream);
		} catch (IOException | ConfigurationException e) {
			LOGGER.error("Error loading ZAP properties files.", e);
		}
	}
	
	public static String getHost() {
		if (host == null) {
			host = getProperties().getString(ZAP_HOST_PROPERTY);
			LOGGER.info("Loaded '{}' value: {}", ZAP_HOST_PROPERTY, host);
		}
		return host;
	}

	public static int getPort() {
		if (port == null) {
			port = getProperties().getInt(ZAP_PORT_PROPERTY);
			LOGGER.info("Loaded '{}' value: {}", ZAP_PORT_PROPERTY, port);
		}
		return port;
	}

	public static String getPath() {
		if (path == null) {
			path = getProperties().getString(ZAP_PATH_PROPERTY);
			LOGGER.info("Loaded '{}' value: {}", ZAP_PATH_PROPERTY, path);
		}
		return path;
	}

	public static Long getInitializationTimeout() {
		if (initializationTimeout == null) {
			initializationTimeout = getProperties().getLong(ZAP_INITIALIZATION_TIMEOUT_PROPERTY);
			LOGGER.info("Loaded '{}' value: {}", ZAP_INITIALIZATION_TIMEOUT_PROPERTY, initializationTimeout);
		}
		return initializationTimeout;
	}

	public static String getOptions() {
		if (options == null) {
			options = getProperties().getString(ZAP_OPTIONS_PROPERTY);
			LOGGER.info("Loaded '{}' value: {}", ZAP_OPTIONS_PROPERTY, options);
		}
		return options;
	}

}
