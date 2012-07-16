package com.areahomeschoolers.baconbits.shared.dto;

public final class EventRegistrationParticipant extends EntityDto<EventRegistrationParticipant> {
	private static final long serialVersionUID = 1L;
	private String firstName, lastName;
	private int eventRegistrationId;
	private int ageGroupId;

	public EventRegistrationParticipant() {

	}

	public int getAgeGroupId() {
		return ageGroupId;
	}

	public int getEventRegistrationId() {
		return eventRegistrationId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setAgeGroupId(int ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	public void setEventRegistrationId(int eventRegistrationId) {
		this.eventRegistrationId = eventRegistrationId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
