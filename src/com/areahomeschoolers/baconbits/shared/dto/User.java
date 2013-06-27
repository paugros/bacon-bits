package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

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
	private String street;
	private String city;
	private String state;
	private String zip;
	private double lat;
	private double lng;
	private boolean directoryOptOut;

	// password is only for setting
	private String password;

	private String mobilePhone;
	private boolean active;
	private boolean systemAdministrator;
	private boolean resetPassword;
	private String parentFirstName;
	private String parentLastName;
	private Date startDate, endDate, addedDate, lastLoginDate, birthDate;
	private Integer parentId;
	private boolean canSwitch;
	private String sex;
	private int imageId;
	private int smallImageId;
	// aux
	// these two keep track of your original user when switching
	private int originalUserId;
	private String originalEmail;

	private int commonInterestCount;
	private String groupsText;
	private boolean generatePassword;
	private HashSet<AccessLevel> accessLevels;
	private boolean isChild;
	private int age;
	private boolean addressChanged;

	private HashMap<Integer, GroupData> groups;
	private HashMap<PrivacyPreferenceType, PrivacyPreference> privacyPreferences = new HashMap<PrivacyPreferenceType, PrivacyPreference>();

	public User() {

	}

	public boolean administratorOf(Integer groupId) {
		if (systemAdministrator) {
			return true;
		}

		GroupData d = groups.get(groupId);
		if (d == null) {
			return false;
		}
		return d.isAdministrator();
	}

	public boolean administratorOf(User u) {
		if (u.getGroups() == null) {
			return false;
		}

		for (GroupData gd : u.getGroups().values()) {
			if (administratorOf(gd.getOrganizationId())) {
				return true;
			}
		}

		return false;
	}

	public boolean administratorOfAny(Integer... groupIds) {
		for (Integer i : groupIds) {
			if (administratorOf(i)) {
				return true;
			}
		}

		return false;
	}

	public boolean canSwitch() {
		return canSwitch;
	}

	public boolean childOf(User parent) {
		return parentId != null && parentId == parent.getId();
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

	public boolean getAddressChanged() {
		return addressChanged;
	}

	public int getAge() {
		return age;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public String getCity() {
		return city;
	}

	public int getCommonInterestCount() {
		return commonInterestCount;
	}

	public boolean getDirectoryOptOut() {
		return directoryOptOut;
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

	public HashMap<Integer, GroupData> getGroups() {
		return groups;
	}

	public String getGroupsText() {
		return groupsText;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public int getImageId() {
		return imageId;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public String getLastName() {
		return lastName;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
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

	public PrivacyPreference getPrivacyPreference(PrivacyPreferenceType type) {
		PrivacyPreference pp = privacyPreferences.get(type);

		if (pp == null) {
			pp = new PrivacyPreference();
			pp.setVisibilityLevelId(type.getDefaultVisibilityLevelId());
			pp.setUserId(getId());
			pp.setPreferenceType(type.toString());
		}

		return pp;
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

	public int getSmallImageId() {
		return smallImageId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getState() {
		return state;
	}

	public String getStreet() {
		return street;
	}

	public boolean getSystemAdministrator() {
		return systemAdministrator;
	}

	public String getUserName() {
		return email;
	}

	public String getZip() {
		return zip;
	}

	public boolean hasRole(AccessLevel level) {
		return accessLevels != null && accessLevels.contains(level);
	}

	public boolean isActive() {
		return active;
	}

	public boolean isCanSwitch() {
		return canSwitch;
	}

	public boolean isChild() {
		return isChild;
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

	public boolean parentOf(User child) {
		return child.getParentId() != null && child.getParentId() == getId();
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

	public void setAddressChanged(boolean addressChanged) {
		this.addressChanged = addressChanged;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setCanSwitch(boolean canSwitch) {
		this.canSwitch = canSwitch;
	}

	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCommonInterestCount(int commonInterestCount) {
		this.commonInterestCount = commonInterestCount;
	}

	public void setDirectoryOptOut(boolean directoryOptOut) {
		this.directoryOptOut = directoryOptOut;
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

	public void setGroups(HashMap<Integer, GroupData> groups) {
		this.groups = groups;
	}

	public void setGroupsText(String groupsText) {
		this.groupsText = groupsText;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
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

	public void setPrivacyPreferences(HashMap<PrivacyPreferenceType, PrivacyPreference> privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}

	public void setResetPassword(boolean resetPassword) {
		this.resetPassword = resetPassword;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setSmallImageId(int smallImageId) {
		this.smallImageId = smallImageId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setSystemAdministrator(boolean systemAdministrator) {
		this.systemAdministrator = systemAdministrator;
		canSwitch = systemAdministrator;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public boolean userCanSee(User u, PrivacyPreferenceType type) {
		PrivacyPreference p = getPrivacyPreference(type);

		VisibilityLevel level = VisibilityLevel.getById(p.getVisibilityLevelId());

		// user null, but public
		if (u == null) {
			return level == VisibilityLevel.PUBLIC;
		}

		// sys admin
		if (u.getSystemAdministrator()) {
			return true;
		}

		// self
		if (u.getId() == getId()) {
			return true;
		}

		switch (level) {
		case PUBLIC:
			// public
			return true;
		case SITE_MEMBERS:
			// user not null, visible to all site members
			return true;
		case MY_GROUPS:
			return !Collections.disjoint(groups.keySet(), u.getGroups().keySet());
		case GROUP_MEMBERS:
			if (p.getGroupId() == null || p.getOrganizationId() == null) {
				return false;
			}

			if (u.memberOf(p.getGroupId()) || u.administratorOf(p.getOrganizationId())) {
				return true;
			}
			break;
		case PRIVATE:
			// self and sys admin already taken care of
			if (u.administratorOf(this)) {
				return true;
			}
			break;
		default:
			return false;
		}

		return false;
	}

}
