package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.HasAddress;

public final class Resource extends EntityDto<Resource> implements HasAddress {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int addedById;
	private Date addedDate;

	private String url;
	private String description;
	private String phone;
	private String name;

	private int categoryId = 8;
	private int clickCount;
	private Date lastClickDate;
	private Integer documentId;

	private Date startDate;
	private Date endDate;

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
	private String category;

	public Resource() {

	}

	public int getAddedById() {
		return addedById;
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

	public String getCategory() {
		return category;
	}

	public int getCategoryId() {
		return categoryId;
	}

	@Override
	public String getCity() {
		return city;
	}

	public int getClickCount() {
		return clickCount;
	}

	public String getDescription() {
		return description;
	}

	public Integer getDocumentId() {
		if (documentId == null || documentId == 0) {
			return null;
		}
		return documentId;
	}

	public Date getEndDate() {
		return endDate;
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

	public String getUrl() {
		return url;
	}

	@Override
	public String getZip() {
		return zip;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
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

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setZip(String zip) {
		this.zip = zip;
	}

}
