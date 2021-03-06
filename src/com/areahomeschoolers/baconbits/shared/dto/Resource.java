package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.Common;
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
	private double price;
	private double highPrice;
	private int minimumAge;
	private int maximumAge;

	private int clickCount;
	private Date lastClickDate;
	private Integer imageId;
	private Integer smallImageId;
	private String imageExtension;
	private boolean priceNotApplicable = true;

	private String contactName;
	private String facilityName;
	private String facebookUrl;

	private Date startDate;
	private Date endDate;
	private int viewCount;

	private boolean showInAds;
	private boolean directoryPriority;
	private int impressions;

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
	private String addressScope;
	private String addedByFullName;
	private String tags;

	public Resource() {

	}

	public String getAddedByFullName() {
		return addedByFullName;
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

	public String getContactEmail() {
		return contactEmail;
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

	public Date getEndDate() {
		return endDate;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public double getHighPrice() {
		return highPrice;
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

	public int getImpressions() {
		return impressions;
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

	public int getMaximumAge() {
		return maximumAge;
	}

	public int getMinimumAge() {
		return minimumAge;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public double getPrice() {
		return price;
	}

	public boolean getPriceNotApplicable() {
		return priceNotApplicable;
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

	public String getTags() {
		return tags;
	}

	public String getUrl() {
		return url;
	}

	public int getViewCount() {
		return viewCount;
	}

	@Override
	public String getZip() {
		return zip;
	}

	public boolean isActive() {
		return Common.isActive(new Date(), endDate);
	}

	public void setAddedByFullName(String addedByFullName) {
		this.addedByFullName = addedByFullName;
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

	public void setContactEmail(String email) {
		this.contactEmail = email;
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

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setImpressions(int impressions) {
		this.impressions = impressions;
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

	public void setMaximumAge(int maximumAge) {
		this.maximumAge = maximumAge;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setPriceNotApplicable(boolean priceNotApplicable) {
		this.priceNotApplicable = priceNotApplicable;
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

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	@Override
	public void setZip(String zip) {
		this.zip = zip;
	}

}
