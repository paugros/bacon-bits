package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum PrivacyPreferenceType implements IsSerializable {
	EMAIL, PHONE, ADDRESS, EVENTS, FAMILY;

	private PrivacyPreferenceType() {
	}
}
