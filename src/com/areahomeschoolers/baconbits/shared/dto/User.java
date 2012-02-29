package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class User extends EntityDto<User> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String email;
	private String firstName;
	private String lastName;
	private String passwordDigest;
	private String homePhone;
	private String mobilePhone;
	private int userTypeId;

	private Date startDate, endDate, addedDate, lastLoginDate;

	public int getAddedById() {
		return userTypeId;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getDescription() {
		return firstName;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public String getPasswordDigest() {
		return passwordDigest;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTitle() {
		return email;
	}

	public void setAddedById(int addedById) {
		this.userTypeId = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setDescription(String description) {
		this.firstName = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTitle(String title) {
		this.email = title;
	}

}
