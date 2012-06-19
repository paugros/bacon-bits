package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
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
	private ArrayList<Integer> groupIds;
	// only for setting
	private String password;
	private String mobilePhone;
	private int userTypeId;
	private boolean active;
	private boolean resetPassword;

	// aux
	private String userType;

	private Date startDate, endDate, addedDate, lastLoginDate;

	public Date getAddedDate() {
		return addedDate;
	}

	public String getEmail() {
		return email;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public ArrayList<Integer> getGroupIds() {
		return groupIds;
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

	public String getPassword() {
		return password;
	}

	public String getPasswordDigest() {
		return passwordDigest;
	}

	public boolean getResetPassword() {
		return resetPassword;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getUserType() {
		return userType;
	}

	public int getUserTypeId() {
		return userTypeId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setGroupIds(ArrayList<Integer> groupIds) {
		this.groupIds = groupIds;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	public void setResetPassword(boolean resetPassword) {
		this.resetPassword = resetPassword;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void setUserTypeId(int userTypeId) {
		this.userTypeId = userTypeId;
	}

}
