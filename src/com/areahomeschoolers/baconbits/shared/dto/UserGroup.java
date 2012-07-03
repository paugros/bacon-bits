package com.areahomeschoolers.baconbits.shared.dto;

public class UserGroup extends EntityDto<UserGroup> {

	private static final long serialVersionUID = 1L;

	private String groupName;
	private String description;
	// for membership records
	private boolean isAdministrator;

	public UserGroup() {

	}

	public boolean getAdministrator() {
		return isAdministrator;
	}

	public String getDescription() {
		return description;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
