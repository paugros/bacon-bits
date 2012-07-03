package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Banner extends EntityDto<Banner> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String bannerText;
	private int locationId;

	private int addedById;

	private Date startDate, endDate, addedDate;

	public Banner() {

	}

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getBannerText() {
		return bannerText;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getLocationId() {
		return locationId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
