package com.areahomeschoolers.baconbits.shared.dto;

public interface HasGroupOwnership {
	public Integer getGroupId();

	public int getOwningOrgId();

	public void setGroupId(Integer groupId);

	public void setOwningOrgId(int organizationId);
}
