package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.List;

public final class EventRegistrationParticipant extends EntityDto<EventRegistrationParticipant> {
	private static final long serialVersionUID = 1L;
	private String firstName, lastName;
	private int eventRegistrationId;
	private int ageGroupId;
	private int age;

	private List<EventField> eventFields = new ArrayList<EventField>();

	public EventRegistrationParticipant() {

	}

	public int getAge() {
		return age;
	}

	public int getAgeGroupId() {
		return ageGroupId;
	}

	public List<EventField> getEventFields() {
		return eventFields;
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

	public void setAge(int age) {
		this.age = age;
	}

	public void setAgeGroupId(int ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	public void setEventFields(List<EventField> eventFields) {
		this.eventFields = eventFields;
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
