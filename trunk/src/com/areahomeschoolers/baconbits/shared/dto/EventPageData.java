package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EventPageData implements IsSerializable {
	private Event event;
	private ArrayList<Data> categories;
	private ArrayList<EventAgeGroup> ageGroups;
	private ArrayList<EventVolunteerPosition> volunteerPositions;

	public EventPageData() {

	}

	public ArrayList<EventAgeGroup> getAgeGroups() {
		return ageGroups;
	}

	public ArrayList<Data> getCategories() {
		return categories;
	}

	public Event getEvent() {
		return event;
	}

	public ArrayList<EventVolunteerPosition> getVolunteerPositions() {
		return volunteerPositions;
	}

	public void setAgeGroups(ArrayList<EventAgeGroup> ageGroups) {
		this.ageGroups = ageGroups;
	}

	public void setCategories(ArrayList<Data> categories) {
		this.categories = categories;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public void setVolunteerPositions(ArrayList<EventVolunteerPosition> volunteerPositions) {
		this.volunteerPositions = volunteerPositions;
	}
}
