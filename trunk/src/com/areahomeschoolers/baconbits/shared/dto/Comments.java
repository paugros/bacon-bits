package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Comments extends EntityDto<Comments> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String comment;
	private int articleId;
	private int userId;
	private Date startDate, endDate, addedDate;

	public int getAddedById() {
		return userId;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getBannerText() {
		return comment;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getLocationId() {
		return articleId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAddedById(int addedById) {
		this.userId = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setBannerText(String bannerText) {
		this.comment = bannerText;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setLocationId(int locationId) {
		this.articleId = locationId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
