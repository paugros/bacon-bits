package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.content.document.HasDocuments;

public final class Event extends EntityDto<Event> implements HasDocuments {
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
	private double price;
	private String address;
	private boolean adultRequired = false;
	private boolean active = true;
	private boolean finished, registrationOpen;
	private boolean sendSurvey;
	private boolean requiresRegistration = true;
	private int minimumParticipants, maximumParticipants;
	private String notificationEmail;
	private String website;
	private String phone;
	private int accessLevelId;

	// auxilliary
	private int cloneFromId;
	private int currentUserParticipantCount;
	private String agePrices;
	private String ageRanges;
	private int documentCount;
	private String category;
	private String groupName;
	private String accessLevel;
	private String addedByFullName;

	public Event() {

	}

	public boolean allowRegistrations() {
		return active && !finished && registrationOpen;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public int getAccessLevelId() {
		return accessLevelId;
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

	public String getAgePrices() {
		return agePrices;
	}

	public String getAgeRanges() {
		return ageRanges;
	}

	public String getCategory() {
		return category;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public int getCloneFromId() {
		return cloneFromId;
	}

	public double getCost() {
		return cost;
	}

	public int getCurrentUserParticipantCount() {
		return currentUserParticipantCount;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int getDocumentCount() {
		return documentCount;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.EVENT;
	}

	public boolean getFinished() {
		return finished;
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

	public String getPhone() {
		return phone;
	}

	public double getPrice() {
		return price;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public Date getRegistrationEndDate() {
		return registrationEndDate;
	}

	public boolean getRegistrationOpen() {
		return registrationOpen;
	}

	public Date getRegistrationStartDate() {
		return registrationStartDate;
	}

	public boolean getRequiresRegistration() {
		return requiresRegistration;
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

	public String getWebsite() {
		return website;
	}

	@Override
	public boolean hasDocuments() {
		return documentCount > 0;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	public void setAccessLevelId(int accessLevelId) {
		this.accessLevelId = accessLevelId;
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

	public void setAgePrices(String agePrices) {
		this.agePrices = agePrices;
	}

	public void setAgeRanges(String ageRanges) {
		this.ageRanges = ageRanges;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public void setCloneFromId(int cloneFromId) {
		this.cloneFromId = cloneFromId;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setCurrentUserParticipantCount(int currentUserParticipantCount) {
		this.currentUserParticipantCount = currentUserParticipantCount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
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

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public void setRegistrationEndDate(Date registrationEndDate) {
		this.registrationEndDate = registrationEndDate;
	}

	public void setRegistrationOpen(boolean registrationFinished) {
		this.registrationOpen = registrationFinished;
	}

	public void setRegistrationStartDate(Date registrationStartDate) {
		this.registrationStartDate = registrationStartDate;
	}

	public void setRequiresRegistration(boolean requiresRegistration) {
		this.requiresRegistration = requiresRegistration;
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

	public void setWebsite(String website) {
		this.website = website;
	}

}
