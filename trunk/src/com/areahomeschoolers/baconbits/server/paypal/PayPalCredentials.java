package com.areahomeschoolers.baconbits.server.paypal;

import com.paypal.adaptive.core.APICredential;

public class PayPalCredentials {
	private static APICredential credentials = new APICredential();

	public PayPalCredentials() {
		credentials.setAPIUsername("paul.a_1343673136_biz_api1.gmail.com");
		credentials.setAPIPassword("1343673159");
		credentials.setSignature("AirQtU1053N9JvGp9xtKZtxvbZojAv39XGRp8tQ8KFG.CaL5sQWMC8F6");
		credentials.setAccountEmail("paul.a_1343673136_biz@gmail.com");
		credentials.setAppId("APP-80W284485P519543T");

	}

	public APICredential getCredentials() {
		return credentials;
	}
}
