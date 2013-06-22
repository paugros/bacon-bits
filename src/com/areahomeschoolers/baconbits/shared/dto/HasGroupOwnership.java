package com.areahomeschoolers.baconbits.shared.dto;

public interface HasGroupOwnership {
	public Integer getGroupId();

	public Integer getOrganizationId();

	public void setGroupId(Integer groupId);

	public void setOrganizationId(Integer organizationId);
}
