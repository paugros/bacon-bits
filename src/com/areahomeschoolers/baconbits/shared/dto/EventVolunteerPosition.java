package com.areahomeschoolers.baconbits.shared.dto;

public final class EventVolunteerPosition extends EntityDto<EventVolunteerPosition> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int positionCount;
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

	public String getJobTitle() {
		return jobTitle;
	}

	public int getPositionCount() {
		return positionCount;
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

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setPositionCount(int positionCount) {
		this.positionCount = positionCount;
	}

}
