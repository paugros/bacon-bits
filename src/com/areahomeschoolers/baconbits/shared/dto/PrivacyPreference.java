package com.areahomeschoolers.baconbits.shared.dto;

import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

public class PrivacyPreference extends EntityDto<PrivacyPreference> {

	private static final long serialVersionUID = 1L;
	private String preferenceType;
	private int userId;
	private int visibilityLevelId;
	private Integer groupId;
	private Integer organizationId;

	public PrivacyPreference() {

	}

	public Integer getGroupId() {
		if (groupId == null || groupId == 0) {
			return null;
		}
		return groupId;
	}

	public Integer getOrganizationId() {
		if (organizationId == null || organizationId == 0) {
			return null;
		}
		return organizationId;
	}

	public PrivacyPreferenceType getPreferenceType() {
		return PrivacyPreferenceType.valueOf(preferenceType);
	}

	public String getPreferenceTypeString() {
		return preferenceType;
	}

	public int getUserId() {
		return userId;
	}

	public VisibilityLevel getVisibilityLevel() {
		return VisibilityLevel.getById(visibilityLevelId);
	}

	public int getVisibilityLevelId() {
		return visibilityLevelId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public void setPreferenceType(String preferenceType) {
		this.preferenceType = preferenceType;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setVisibilityLevelId(int visibilityLevelId) {
		this.visibilityLevelId = visibilityLevelId;
	}

}
