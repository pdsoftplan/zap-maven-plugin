package br.com.softplan.security.zap.api.util;

import org.testng.annotations.BeforeClass;

import br.com.softplan.security.zap.api.ZapClient;
import br.com.softplan.security.zap.commons.ZapInfo;
import org.zaproxy.clientapi.core.ClientApi;

/**
 * Base class for integration tests. 
 * 
 * @author pdsec
 */
public class BaseIT {

	protected ZapInfo zapInfo;
	protected ZapClient zapClient;
	protected ClientApi api;
	
	@BeforeClass
	public void init() {
		zapInfo   = ZapInfo.builder().buildToUseRunningZap(ZapProperties.getHost(), ZapProperties.getPort());
		zapClient = new ZapClient(zapInfo);
		api       = new ClientApi(zapInfo.getHost(), zapInfo.getPort());
	}
	
}
