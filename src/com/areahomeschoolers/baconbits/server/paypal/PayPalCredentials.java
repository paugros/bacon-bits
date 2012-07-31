package com.areahomeschoolers.baconbits.server.paypal;

import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.paypal.adaptive.api.requests.fnapi.SimplePay;
import com.paypal.adaptive.core.APICredential;
import com.paypal.adaptive.core.Receiver;
import com.paypal.adaptive.exceptions.MissingParameterException;
import com.paypal.adaptive.exceptions.RequestAlreadyMadeException;

public class PayPalCredentials {
	private static APICredential credentials = new APICredential();

	public PayPalCredentials() {
		if (ServerContext.isLive()) {
			credentials.setAPIUsername("weare.home.educators_api1.gmail.com");
			credentials.setAPIPassword("BZFADA4CXJSCHYW8");
			credentials.setSignature("AFcWxV21C7fd0v3bYYYRCpSSRl31AIiJDk-qOGV.J6VfFcbztTiAOVoS");
			credentials.setAccountEmail("weare.home.educators@gmail.com");
			credentials.setAppId("b9374d996b19e1a5a34abcc3f070b04f");
		} else {
			credentials.setAPIUsername("paul.a_1343673136_biz_api1.gmail.com");
			credentials.setAPIPassword("1343673159");
			credentials.setSignature("AirQtU1053N9JvGp9xtKZtxvbZojAv39XGRp8tQ8KFG.CaL5sQWMC8F6");
			credentials.setAccountEmail("paul.a_1343673136_biz@gmail.com");
			credentials.setAppId("APP-80W284485P519543T");
		}

		SimplePay p = new SimplePay();

		p.setCredentialObj(credentials);
		p.setMemo("A test");
		Receiver r = new Receiver();
		r.setAmount(1.34);
		p.setReceiver(r);
		p.setLanguage("");
		try {
			p.validate();
		} catch (MissingParameterException e) {
			System.out.println(e.getParameterName());
		} catch (RequestAlreadyMadeException e) {
			e.printStackTrace();
		}
	}

	public APICredential getCredentials() {
		return credentials;
	}
}
