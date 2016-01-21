package br.com.softplan.security.zap.commons.boot;

import br.com.softplan.security.zap.commons.ZapInfo;

/**
 * Factory to create the correct {@link ZapBoot} implementation
 * based on the given {@link ZapInfo} instance.
 * 
 * @author pdsec
 */
public final class ZapBootFactory {

	static ZapBoot makeZapBoot(ZapInfo zapInfo) {
		if (zapInfo.shouldRunWithDocker()) {
			return new ZapDockerBoot();
		}
		if (zapInfo.getPath() != null && !zapInfo.getPath().isEmpty()) {
			return new ZapLocalBoot();
		}
		return new ZapNilBoot();
	}
	
	private ZapBootFactory() {}
	
}
