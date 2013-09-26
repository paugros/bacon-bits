package com.areahomeschoolers.baconbits.shared.dto;

import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum PrivacyPreferenceType implements IsSerializable {
	EMAIL(VisibilityLevel.MY_GROUPS), HOME_PHONE(VisibilityLevel.MY_GROUPS), MOBILE_PHONE(VisibilityLevel.MY_GROUPS), ADDRESS(VisibilityLevel.MY_GROUPS), EVENTS(
			VisibilityLevel.SITE_MEMBERS), FAMILY(VisibilityLevel.SITE_MEMBERS);

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
