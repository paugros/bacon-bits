package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PaypalData implements IsSerializable {
	private String authorizationUrl;
	private String paymentExecStatus;
	private String payKey;

	public PaypalData() {

	}

	public String getAuthorizationUrl() {
		return authorizationUrl;
	}

	public String getPayKey() {
		return payKey;
	}

	public String getPaymentExecStatus() {
		return paymentExecStatus;
	}

	public void setAuthorizationUrl(String authorizationUrl) {
		this.authorizationUrl = authorizationUrl;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public void setPaymentExecStatus(String paymentExecStatus) {
		this.paymentExecStatus = paymentExecStatus;
	}

}
