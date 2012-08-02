package com.areahomeschoolers.baconbits.server.paypal;

import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.paypal.adaptive.core.APICredential;

public class PayPalCredentials extends APICredential {

	public PayPalCredentials() {
		if (ServerContext.isLive()) {
			setAPIUsername("weare.home.educators_api1.gmail.com");
			setAPIPassword("BZFADA4CXJSCHYW8");
			setSignature("AFcWxV21C7fd0v3bYYYRCpSSRl31AIiJDk-qOGV.J6VfFcbztTiAOVoS");
			setAccountEmail("weare.home.educators@gmail.com");
			setAppId("c5b65d85d40716b42b8cbc6055fdca0b");
		} else {
			setAPIUsername("paul.a_1343673136_biz_api1.gmail.com");
			setAPIPassword("1343673159");
			setSignature("AirQtU1053N9JvGp9xtKZtxvbZojAv39XGRp8tQ8KFG.CaL5sQWMC8F6");
			setAccountEmail("paul.a_1343673136_biz@gmail.com");
			setAppId("APP-80W284485P519543T");
		}

	}
}
