package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.shared.HasAddress;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

public final class User extends EntityDto<User> implements HasAddress {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String email;
	private String firstName;
	private String lastName;
	private String passwordDigest;
	private String homePhone;
	private String facebookUrl;
	private String guid;
	private boolean receiveNews;

	// address
	private String address;
	private String street;
	private String city;
	private String state;
	private String zip;
	private double lat;
	private double lng;
	private boolean addressChanged;

	private boolean directoryOptOut = true;

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
	private boolean showUserAgreement;
	private String sex;
	private Integer imageId;
	private Integer smallImageId;
	private String imageExtension;
	// aux
	private boolean isSwitched;
	private int commonInterestCount;
	private String groupsText;
	private boolean generatePassword;
	private HashSet<AccessLevel> accessLevels;
	private boolean isChild;
	private int age;
	// special flag to indicate incoming user should be auto-added to a group
	private int autoAddToGroupId;
	// only for group membership listings
	private boolean userApproved;
	private boolean groupApproved;

	private HashMap<Integer, GroupData> groups = new HashMap<Integer, GroupData>();
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
		if (systemAdministrator) {
			return true;
		}

		if (!u.isSaved()) {
			return true;
		}

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
		return systemAdministrator || isSwitched();
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

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public boolean getAddressChanged() {
		return addressChanged;
	}

	public int getAge() {
		return age;
	}

	public int getAutoAddToGroupId() {
		return autoAddToGroupId;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	@Override
	public String getCity() {
		return city;
	}

	public int getCommonInterestCount() {
		return commonInterestCount;
	}

	@Override
	public String getDescriptor() {
		return firstName + " " + lastName;
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

	public String getFacebookUrl() {
		return facebookUrl;
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

	public boolean getGroupApproved() {
		return groupApproved;
	}

	public HashMap<Integer, GroupData> getGroups() {
		return groups;
	}

	public String getGroupsText() {
		return groupsText;
	}

	public String getGuid() {
		return guid;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public String getImageExtension() {
		return imageExtension;
	}

	public Integer getImageId() {
		if (imageId == null || imageId == 0) {
			return null;
		}
		return imageId;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public String getLastName() {
		return lastName;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public ArrayList<Integer> getOrganizationIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (GroupData g : groups.values()) {
			if (!ids.contains(g.getOrganizationId())) {
				ids.add(g.getOrganizationId());
			}
		}

		return ids;
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

	public boolean getReceiveNews() {
		return receiveNews;
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

	public boolean getShowUserAgreement() {
		return showUserAgreement;
	}

	public Integer getSmallImageId() {
		if (smallImageId == null || smallImageId == 0) {
			return null;
		}
		return smallImageId;
	}

	public Date getStartDate() {
		return startDate;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public String getStreet() {
		return street;
	}

	public boolean getSystemAdministrator() {
		return systemAdministrator;
	}

	public boolean getUserApproved() {
		return userApproved;
	}

	public String getUserName() {
		return email;
	}

	@Override
	public String getZip() {
		return zip;
	}

	public boolean hasRole(AccessLevel level) {
		return accessLevels != null && accessLevels.contains(level);
	}

	public boolean isActive() {
		return active;
	}

	public boolean isChild() {
		return isChild;
	}

	public boolean isSwitched() {
		return isSwitched;
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

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setAddressChanged(boolean addressChanged) {
		this.addressChanged = addressChanged;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setAutoAddToGroupId(int autoAddToGroupId) {
		this.autoAddToGroupId = autoAddToGroupId;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	@Override
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

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
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

	public void setGroupApproved(boolean groupApproved) {
		this.groupApproved = groupApproved;
	}

	public void setGroups(HashMap<Integer, GroupData> groups) {
		this.groups = groups;
	}

	public void setGroupsText(String groupsText) {
		this.groupsText = groupsText;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
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

	public void setPrivacyPreference(PrivacyPreference pref) {
		privacyPreferences.put(pref.getPreferenceType(), pref);
	}

	public void setPrivacyPreferences(HashMap<PrivacyPreferenceType, PrivacyPreference> privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}

	public void setReceiveNews(boolean receiveNews) {
		this.receiveNews = receiveNews;
	}

	public void setResetPassword(boolean resetPassword) {
		this.resetPassword = resetPassword;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setShowUserAgreement(boolean showUserAgreement) {
		this.showUserAgreement = showUserAgreement;
	}

	public void setSmallImageId(Integer smallImageId) {
		this.smallImageId = smallImageId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setState(String state) {
		this.state = state;
	}

	@Override
	public void setStreet(String street) {
		this.street = street;
	}

	public void setSwitched(boolean switched) {
		isSwitched = switched;
	}

	public void setSystemAdministrator(boolean systemAdministrator) {
		this.systemAdministrator = systemAdministrator;
	}

	public void setUserApproved(boolean userApproved) {
		this.userApproved = userApproved;
	}

	@Override
	public void setZip(String zip) {
		this.zip = zip;
	}

	public boolean userCanSee(User u, PrivacyPreferenceType type) {
		if (!isSaved()) {
			return true;
		}

		PrivacyPreference p = getPrivacyPreference(type);

		VisibilityLevel level = VisibilityLevel.getById(p.getVisibilityLevelId());
		if (directoryOptOut) {
			level = VisibilityLevel.PRIVATE;
		}

		// user null, but public
		if (u == null) {
			return level == VisibilityLevel.PUBLIC;
		}

		// parents
		if (u.parentOf(this)) {
			return true;
		}

		// sys admin
		if (u.getSystemAdministrator()) {
			return true;
		}

		// self
		if (u.getId() == getId() || (u.getEmail() != null && u.getEmail().equals(email))) {
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
			if (u.administratorOf(this) || u.parentOf(this)) {
				return true;
			}
			break;
		default:
			return false;
		}

		return false;
	}

}
