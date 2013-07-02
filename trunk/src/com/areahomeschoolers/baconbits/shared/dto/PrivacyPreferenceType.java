package com.areahomeschoolers.baconbits.shared.dto;

import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum PrivacyPreferenceType implements IsSerializable {
	EMAIL(VisibilityLevel.PRIVATE), HOME_PHONE(VisibilityLevel.PRIVATE), MOBILE_PHONE(VisibilityLevel.PRIVATE), ADDRESS(VisibilityLevel.PRIVATE), EVENTS(
			VisibilityLevel.SITE_MEMBERS), FAMILY(VisibilityLevel.MY_GROUPS);

	private VisibilityLevel defaultVisibilityLevel;

	private PrivacyPreferenceType() {
	}

	private PrivacyPreferenceType(VisibilityLevel defaultVisibilityLevel) {
		this.defaultVisibilityLevel = defaultVisibilityLevel;
	}

	public int getDefaultVisibilityLevelId() {
		return defaultVisibilityLevel.getId();
	}
}