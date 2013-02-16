package com.areahomeschoolers.baconbits.shared.dto;

public final class Book extends EntityDto<Book> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private int userId;
	private int categoryId;
	private int gradeLevelId;
	private int statusId;
	private double price;
	private String isbn;
	private String notes;
	private Integer conditionId;
	private Integer imageId;
	private Integer smallImageId;
	private String imageUrl;

	// auxillary
	private String userFirstName, userLastName;
	private String userEmail;
	private String status;
	private String category;
	private String gradeLevel;
	private String condition;

	public Book() {

	}

	public String getCategory() {
		return category;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public String getCondition() {
		return condition;
	}

	public Integer getConditionId() {
		if (conditionId == null || conditionId == 0) {
			return null;
		}

		return conditionId;
	}

	public String getGradeLevel() {
		return gradeLevel;
	}

	public int getGradeLevelId() {
		return gradeLevelId;
	}

	public Integer getImageId() {
		if (imageId == null || imageId == 0) {
			return null;
		}

		return imageId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getNotes() {
		return notes;
	}

	public double getPrice() {
		return price;
	}

	public Integer getSmallImageId() {
		if (smallImageId == null || smallImageId == 0) {
			return null;
		}

		return smallImageId;
	}

	public String getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getTitle() {
		return title;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public int getUserId() {
		return userId;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public boolean isActive() {
		return true;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setConditionId(int conditionId) {
		this.conditionId = conditionId;
	}

	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}

	public void setGradeLevelId(int gradeLevelId) {
		this.gradeLevelId = gradeLevelId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setSmallImageId(Integer smallImageId) {
		this.smallImageId = smallImageId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

}
