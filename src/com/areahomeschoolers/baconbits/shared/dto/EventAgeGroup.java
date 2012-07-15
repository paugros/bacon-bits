package com.areahomeschoolers.baconbits.shared.dto;

public final class EventAgeGroup extends EntityDto<EventAgeGroup> {
	private static final long serialVersionUID = 1L;

	private int eventId;
	private int minimumAge;
	private int maximumAge;
	private int minimumParticipants;
	private int maximumParticipants;
	private double price;

	public EventAgeGroup() {

	}

	public int getEventId() {
		return eventId;
	}

	public int getMaximumAge() {
		return maximumAge;
	}

	public int getMaximumParticipants() {
		return maximumParticipants;
	}

	public int getMinimumAge() {
		return minimumAge;
	}

	public int getMinimumParticipants() {
		return minimumParticipants;
	}

	public double getPrice() {
		return price;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setMaximumAge(int maximumAge) {
		this.maximumAge = maximumAge;
	}

	public void setMaximumParticipants(int maximumParticipants) {
		this.maximumParticipants = maximumParticipants;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	public void setMinimumParticipants(int minimumParticipants) {
		this.minimumParticipants = minimumParticipants;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
