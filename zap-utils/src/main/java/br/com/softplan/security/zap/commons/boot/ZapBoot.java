package br.com.softplan.security.zap.commons.boot;

import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * This interface should be implemented by any class capable 
 * of starting and stopping ZAP, no matter how.
 * 
 * @author pdsec
 */
public interface ZapBoot {

	/**
	 * Starts ZAP.
	 * <p>
	 * It should throw {@link br.com.softplan.security.zap.commons.exception.ZapInitializationTimeoutException ZapInitializationTimeoutException} 
	 * in case ZAP is not started before a timeout, defined by {@code zapInfo.initializationTimeout}
	 * (the default value is {@code 120000}).
	 * 
	 * @param zapInfo an object with all the information needed to start ZAP.
	 */
	void startZap(ZapInfo zapInfo);
	
	/**
	 * Stops ZAP.
	 */
	void stopZap();
	
}
