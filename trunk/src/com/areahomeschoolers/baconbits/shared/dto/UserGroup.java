package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public class UserGroup extends EntityDto<UserGroup> {

	private static final long serialVersionUID = 1L;

	private String groupName;
	private String description;
	private Date startDate, endDate;

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

	public Date getEndDate() {
		return endDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
