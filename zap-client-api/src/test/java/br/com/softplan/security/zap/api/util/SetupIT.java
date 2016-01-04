package br.com.softplan.security.zap.api.util;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import br.com.softplan.security.zap.commons.ZapInfo;
import br.com.softplan.security.zap.commons.boot.Zap;

/**
 * Class responsible for starting ZAP before the integration tests 
 * and stopping it after the tests are finished.
 * 
 * @author pdsec
 */
public class SetupIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SetupIT.class);
	
	@BeforeSuite
	public void setUp() throws IOException, ConfigurationException {
		ZapInfo zapHostInfo = new ZapInfo.Builder()
				.host   (ZapProperties.getHost())
				.port   (ZapProperties.getPort())
				.path   (ZapProperties.getPath())
				.options(ZapProperties.getOptions())
				.initializationTimeoutInMillis(ZapProperties.getInitializationTimeout())
				.build();
		
		LOGGER.info("Starting ZAP before integration tests.");
		Zap.startZap(zapHostInfo);
	}

	@AfterSuite
	public void tearUp() {
		LOGGER.info("Stopping ZAP after integration tests.");
		Zap.stopZap();
	}

}
