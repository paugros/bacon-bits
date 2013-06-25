package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum PrivacyPreferenceType implements IsSerializable {
	EMAIL(4), PHONE(4), ADDRESS(4), EVENTS(2), FAMILY(2);

	private int defaultVisibilityLevelId;

	private PrivacyPreferenceType() {
	}

	private PrivacyPreferenceType(int defaultVisibilityLevelId) {
		this.defaultVisibilityLevelId = defaultVisibilityLevelId;
	}

	public int getDefaultVisibilityLevelId() {
		return defaultVisibilityLevelId;
	}
}
