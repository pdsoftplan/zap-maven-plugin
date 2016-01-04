package br.com.softplan.security.zap.commons.boot;

import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Class to be used as the ZAP booter when ZAP is supposedly up and running.
 * 
 * @author pdsec
 */
public class ZapNilBoot extends AbstractZapBoot {

	@Override
	public void startZap(ZapInfo zapInfo) {}

	@Override
	public void stopZap() {}

}
