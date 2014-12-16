package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.HasAddress;

public class Resource extends EntityDto<Resource> implements HasAddress {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int addedById;
	private Date addedDate;

	private String url;
	private String description;
	private String phone;
	private String contactEmail;
	private String name;
	private int addressScopeId;
	private String adDescription;

	private int clickCount;
	private Date lastClickDate;
	private Integer imageId;
	private Integer smallImageId;
	private String imageExtension;

	private String contactName;
	private String facilityName;
	private String facebookUrl;

	private Date startDate;
	private Date endDate;

	private boolean showInAds;
	private boolean directoryPriority;

	// address
	private String address;
	private String street;
	private String city;
	private String state;
	private String zip;
	private double lat;
	private double lng;
	private boolean addressChanged;

	// auxiliary
	private int tagCount;
	private String addressScope;

	public Resource() {

	}

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getAdDescription() {
		return adDescription;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public boolean getAddressChanged() {
		return addressChanged;
	}

	public String getAddressScope() {
		return addressScope;
	}

	public int getAddressScopeId() {
		return addressScopeId;
	}

	@Override
	public String getCity() {
		return city;
	}

	public int getClickCount() {
		return clickCount;
	}

	public String getContactName() {
		return contactName;
	}

	public String getDescription() {
		return description;
	}

	public boolean getDirectoryPriority() {
		return directoryPriority;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public String getFacilityName() {
		return facilityName;
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

	public Date getLastClickDate() {
		return lastClickDate;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public boolean getShowInAds() {
		return showInAds;
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

	public int getTagCount() {
		return tagCount;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String getZip() {
		return zip;
	}

	public boolean hasTags() {
		return tagCount > 0;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setAdDescription(String adDescription) {
		this.adDescription = adDescription;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setAddressChanged(boolean addressChanged) {
		this.addressChanged = addressChanged;
	}

	public void setAddressScope(String addressScope) {
		this.addressScope = addressScope;
	}

	public void setAddressScopeId(int addressScopeId) {
		this.addressScopeId = addressScopeId;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDirectoryPriority(boolean directoryPriority) {
		this.directoryPriority = directoryPriority;
	}

	public void setContactEmail(String email) {
		this.contactEmail = email;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setLastClickDate(Date lastClickDate) {
		this.lastClickDate = lastClickDate;
	}

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setShowInAds(boolean showInAds) {
		this.showInAds = showInAds;
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

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setZip(String zip) {
		this.zip = zip;
	}

}
