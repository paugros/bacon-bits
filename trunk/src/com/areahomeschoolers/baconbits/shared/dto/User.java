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
	private String address;
	// only for setting
	private String password;

	private String mobilePhone;
	private boolean active;
	private boolean systemAdministrator;
	private boolean resetPassword;
	private String parentFirstName;
	private String parentLastName;
	private HashMap<Integer, Boolean> groups;
	private Date startDate, endDate, addedDate, lastLoginDate, birthDate;
	private Integer parentId;
	private boolean canSwitch;
	private String sex;

	// aux
	// these two keep track of your original user when switching
	private int originalUserId;
	private String originalEmail;
	private String groupsText;
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

	public boolean canSwitch() {
		return canSwitch;
	}

	public HashSet<AccessLevel> getAccessLevels() {
		return accessLevels;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getAddress() {
		return address;
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

	public String getGroupsText() {
		return groupsText;
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

	public String getOriginalEmail() {
		return originalEmail;
	}

	public int getOriginalUserId() {
		return originalUserId;
	}

	public String getParentFirstName() {
		return parentFirstName;
	}

	public Integer getParentId() {
		if (parentId == null || parentId == 0) {
			return null;
		}
		return parentId;
	}

	public String getParentLastName() {
		return parentLastName;
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

	public String getSex() {
		return sex;
	}

	public String getSexyText() {
		if ("f".equals(sex)) {
			return "Female";
		} else if ("m".equals(sex)) {
			return "Male";
		}

		return "";
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

	public boolean isChild() {
		return parentId != null;
	}

	public boolean isSwitched() {
		return originalUserId > 0 && getId() != originalUserId;
	}

	public boolean memberOf(Integer groupId) {
		if (systemAdministrator || groupId == null || groupId == 0) {
			return true;
		}
		return groups.keySet().contains(groupId);
	}

	public boolean memberOfAny(int... groupIds) {
		for (int id : groupIds) {
			if (memberOf(id)) {
				return true;
			}
		}
		return false;
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

	public void setAddress(String address) {
		this.address = address;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setCanSwitch(boolean canSwitch) {
		this.canSwitch = canSwitch;
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

	public void setGroupsText(String groupsText) {
		this.groupsText = groupsText;
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

	public void setOriginalEmail(String originalEmail) {
		this.originalEmail = originalEmail;
	}

	public void setOriginalUserId(int originalUserId) {
		this.originalUserId = originalUserId;
	}

	public void setParentFirstName(String parentFirstName) {
		this.parentFirstName = parentFirstName;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public void setParentLastName(String parentLastName) {
		this.parentLastName = parentLastName;
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

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setSystemAdministrator(boolean systemAdministrator) {
		this.systemAdministrator = systemAdministrator;
		canSwitch = systemAdministrator;
	}

}
