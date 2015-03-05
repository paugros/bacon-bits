package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Review extends EntityDto<Review> {
	public enum ReviewType {
		RESOURCE;

		private ReviewType() {
		}
	}

	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String review;
	private int articleId;
	private int userId;
	private Integer hiddenById;
	private int rating;
	private Date endDate, addedDate;
	private ReviewType type;
	private boolean anonymous;

	// aux
	private String addedByFullName;
	private int entityId;
	private boolean addedByOwner;

	public String getAddedByFullName() {
		return addedByFullName;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public boolean getAnonymous() {
		return anonymous;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getEntityId() {
		return entityId;
	}

	public Integer getHiddenById() {
		if (hiddenById == null || hiddenById == 0) {
			return null;
		}
		return hiddenById;
	}

	public int getLocationId() {
		return articleId;
	}

	public int getRating() {
		return rating;
	}

	public String getReview() {
		return review;
	}

	public ReviewType getType() {
		return type;
	}

	public int getUserId() {
		return userId;
	}

	public boolean getAddedByOwner() {
		return addedByOwner;
	}

	public void setAddedByFullName(String addedByFullName) {
		this.addedByFullName = addedByFullName;
	}

	public void setAddedByOwner(boolean addedByOwner) {
		this.addedByOwner = addedByOwner;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public void setHiddenById(Integer hiddenById) {
		this.hiddenById = hiddenById;
	}

	public void setLocationId(int locationId) {
		this.articleId = locationId;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void setType(ReviewType type) {
		this.type = type;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
