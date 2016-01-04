package br.com.softplan.security.zap.commons.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Utility class to help with ZAP related tasks (start and stop ZAP, run ZAP Docker image).
 * 
 * @author pdsec
 */
public final class Zap {

	private static final Logger LOGGER = LoggerFactory.getLogger(Zap.class);
	
	private static ZapBoot zap;
	
	public static void startZap(ZapInfo zapInfo) {
		zap = ZapBootFactory.makeZapBoot(zapInfo);
		LOGGER.debug("ZAP will be started by: [{}].", zap.getClass().getSimpleName());
		
		zap.startZap(zapInfo);
	}
	
	public static void stopZap() {
		if (zap != null) {
			zap.stopZap();
		}
	}
	
	private Zap() {}
	
}
