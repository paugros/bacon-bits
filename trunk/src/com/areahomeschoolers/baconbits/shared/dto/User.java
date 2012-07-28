package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

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
	// only for setting
	private String password;

	private String mobilePhone;
	private boolean active;
	private boolean systemAdministrator;
	private boolean resetPassword;
	private HashMap<Integer, Boolean> groups;
	private Date startDate, endDate, addedDate, lastLoginDate, birthDate;
	private Integer parentId;

	// aux
	private boolean generatePassword;
	private HashSet<AccessLevel> accessLevels;

	public User() {

	}

	public boolean administratorOf(Integer groupId) {
		if (systemAdministrator) {
			return true;
		}

		return groups.get(groupId) == Boolean.TRUE;
	}

	public HashSet<AccessLevel> getAccessLevels() {
		return accessLevels;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public Date getBirthDate() {
		return birthDate;
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

	public boolean getGeneratePassword() {
		return generatePassword;
	}

	public HashMap<Integer, Boolean> getGroups() {
		return groups;
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

	public Integer getParentId() {
		if (parentId == null || parentId == 0) {
			return null;
		}
		return parentId;
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

	public boolean getSystemAdministrator() {
		return systemAdministrator;
	}

	public String getUserName() {
		return email;
	}

	public boolean hasRole(AccessLevel level) {
		return accessLevels != null && accessLevels.contains(level);
	}

	public boolean isActive() {
		return active;
	}

	public boolean memberOf(int groupId) {
		return groups.keySet().contains(groupId);
	}

	public void setAccessLevels(HashSet<AccessLevel> accessLevels) {
		this.accessLevels = accessLevels;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
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

	public void setGeneratePassword(boolean generatePassword) {
		this.generatePassword = generatePassword;
		if (generatePassword) {
			resetPassword = true;
		}
	}

	public void setGroups(HashMap<Integer, Boolean> groups) {
		this.groups = groups;
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

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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

	public void setSystemAdministrator(boolean systemAdministrator) {
		this.systemAdministrator = systemAdministrator;
	}

}
