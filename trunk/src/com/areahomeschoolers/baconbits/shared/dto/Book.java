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

	// auxillary
	private String userFirstName, userLastName;
	private String status;
	private String category;
	private String gradeLevel;

	public Book() {

	}

	public String getGradeLevel() {
		return gradeLevel;
	}

	public int getGradeLevelId() {
		return gradeLevelId;
	}

	public String getCategory() {
		return category;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public double getPrice() {
		return price;
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

	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}

	public void setGradeLevelId(int gradeLevelId) {
		this.gradeLevelId = gradeLevelId;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public void setPrice(double price) {
		this.price = price;
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
