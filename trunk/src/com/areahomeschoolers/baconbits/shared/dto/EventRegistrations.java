package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class EventRegistrations extends EntityDto<EventRegistrations> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int peopleCount;
	private String description;

	private int userId;

	private Date startDate, endDate, addedDate;
	private int eventId;

	public int getAddedById() {
		return userId;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getDescription() {
		return description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getEventTypeId() {
		return eventId;
	}

	public int getPeopleCount() {
		return peopleCount;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventId = eventTypeId;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
