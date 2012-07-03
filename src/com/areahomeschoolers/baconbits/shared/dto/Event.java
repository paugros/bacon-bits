package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Event extends EntityDto<Event> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private String description;
	private int addedById;
	private Date startDate, endDate, addedDate, registrationStartDate, registrationEndDate, publishDate;
	private Integer groupId;
	private int categoryId;
	private double cost;
	private String address;
	private boolean adultRequired = false;
	private boolean publicEvent = false;
	private boolean active = true;
	private boolean sendSurvey;
	private int minimumParticipants, maximumParticipants;
	private String notificationEmail;

	// auxilliary
	private String category;
	private String groupName;
	private String addedByFullName;

	public Event() {

	}

	public boolean getActive() {
		return active;
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

	public String getAddress() {
		return address;
	}

	public boolean getAdultRequired() {
		return adultRequired;
	}

	public String getCategory() {
		return category;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public double getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Integer getGroupId() {
		if (groupId == null || groupId == 0) {
			return null;
		}
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getMaximumParticipants() {
		return maximumParticipants;
	}

	public int getMinimumParticipants() {
		return minimumParticipants;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public boolean getPublicEvent() {
		return publicEvent;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public Date getRegistrationEndDate() {
		return registrationEndDate;
	}

	public Date getRegistrationStartDate() {
		return registrationStartDate;
	}

	public boolean getSendSurvey() {
		return sendSurvey;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTitle() {
		return title;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public void setAddress(String address) {
		this.address = address;
	}

	public void setAdultRequired(boolean adultRequired) {
		this.adultRequired = adultRequired;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setMaximumParticipants(int maximumParticipants) {
		this.maximumParticipants = maximumParticipants;
	}

	public void setMinimumParticipants(int minimumParticipants) {
		this.minimumParticipants = minimumParticipants;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public void setPublicEvent(boolean publicEvent) {
		this.publicEvent = publicEvent;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public void setRegistrationEndDate(Date registrationEndDate) {
		this.registrationEndDate = registrationEndDate;
	}

	public void setRegistrationStartDate(Date registrationStartDate) {
		this.registrationStartDate = registrationStartDate;
	}

	public void setSendSurvey(boolean sendSurvey) {
		this.sendSurvey = sendSurvey;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
