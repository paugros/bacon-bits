package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Book extends EntityDto<Book> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private int userId;
	private int categoryId;
	private Integer gradeLevelId;
	private int statusId;
	private double price;
	private String isbn;
	private String notes;
	private Integer conditionId;
	private Integer imageId;
	private Integer smallImageId;
	private String imageUrl;
	private boolean soldAtBookSale;
	private String author;
	private Date soldDate;
	private String subTitle;
	private String publisher;
	private Date publishDate;
	private String description;
	private int pageCount;
	private String googleCategories;

	// auxillary
	private String amazonNewPrice, amazonUsedPrice;
	private String shippingFrom;
	private String amazonUrl;
	private String userFirstName, userLastName;
	private String userEmail;
	private String status;
	private String category;
	private String gradeLevel;
	private String condition;
	private boolean inMyShoppingCart;

	public Book() {

	}

	public String getAmazonNewPrice() {
		return amazonNewPrice;
	}

	public String getAmazonUrl() {
		return amazonUrl;
	}

	public String getAmazonUsedPrice() {
		return amazonUsedPrice;
	}

	public String getAuthor() {
		return author;
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

	public String getDescription() {
		return description;
	}

	public String getGoogleCategories() {
		return googleCategories;
	}

	public String getGradeLevel() {
		return gradeLevel;
	}

	public Integer getGradeLevelId() {
		if (gradeLevelId == null || gradeLevelId == 0) {
			return null;
		}
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

	public boolean getInMyShoppingCart() {
		return inMyShoppingCart;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getNotes() {
		return notes;
	}

	public int getPageCount() {
		return pageCount;
	}

	public double getPrice() {
		return price;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getShippingFrom() {
		return shippingFrom;
	}

	public Integer getSmallImageId() {
		if (smallImageId == null || smallImageId == 0) {
			return null;
		}

		return smallImageId;
	}

	public boolean getSoldAtBookSale() {
		return soldAtBookSale;
	}

	public Date getSoldDate() {
		return soldDate;
	}

	public String getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getSubTitle() {
		return subTitle;
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

	public void setAmazonNewPrice(String amazonNewPrice) {
		this.amazonNewPrice = amazonNewPrice;
	}

	public void setAmazonUrl(String amazonUrl) {
		this.amazonUrl = amazonUrl;
	}

	public void setAmazonUsedPrice(String amazonUsedPrice) {
		this.amazonUsedPrice = amazonUsedPrice;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGoogleCategories(String googleCategories) {
		this.googleCategories = googleCategories;
	}

	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}

	public void setGradeLevelId(Integer gradeLevelId) {
		this.gradeLevelId = gradeLevelId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setInMyShoppingCart(boolean inMyShoppingCart) {
		this.inMyShoppingCart = inMyShoppingCart;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setShippingFrom(String shippingFrom) {
		this.shippingFrom = shippingFrom;
	}

	public void setSmallImageId(Integer smallImageId) {
		this.smallImageId = smallImageId;
	}

	public void setSoldAtBookSale(boolean soldAtBookSale) {
		this.soldAtBookSale = soldAtBookSale;
	}

	public void setSoldDate(Date soldDate) {
		this.soldDate = soldDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
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
