package com.areahomeschoolers.baconbits.shared.dto;

public interface HasGroupOwnership {
	public int getAddedById();

	public Integer getGroupId();

	public int getId();

	public int getOwningOrgId();

	public void setGroupId(Integer groupId);

	public void setOwningOrgId(int organizationId);
}
