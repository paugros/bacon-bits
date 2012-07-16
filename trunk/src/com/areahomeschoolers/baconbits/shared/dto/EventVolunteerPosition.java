package com.areahomeschoolers.baconbits.shared.dto;

public final class EventVolunteerPosition extends EntityDto<EventVolunteerPosition> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int positionCount;
	private int openPositionCount;
	// used when registering
	private int registerPositionCount;
	private int mappingId;
	private int eventRegistrationId;

	private String description;
	private String jobTitle;
	private double discount;
	private int eventId;

	public EventVolunteerPosition() {

	}

	public String getDescription() {
		return description;
	}

	public double getDiscount() {
		return discount;
	}

	public int getEventId() {
		return eventId;
	}

	public int getEventRegistrationId() {
		return eventRegistrationId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public int getMappingId() {
		return mappingId;
	}

	public int getOpenPositionCount() {
		return openPositionCount;
	}

	public int getPositionCount() {
		return positionCount;
	}

	public int getRegisterPositionCount() {
		return registerPositionCount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public void setEventId(int eventTypeId) {
		this.eventId = eventTypeId;
	}

	public void setEventRegistrationId(int eventRegistrationId) {
		this.eventRegistrationId = eventRegistrationId;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}

	public void setOpenPositionCount(int openPositionCount) {
		this.openPositionCount = openPositionCount;
	}

	public void setPositionCount(int positionCount) {
		this.positionCount = positionCount;
	}

	public void setRegisterPositionCount(int registerPositionCount) {
		this.registerPositionCount = registerPositionCount;
	}

}
