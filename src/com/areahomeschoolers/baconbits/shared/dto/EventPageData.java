package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EventPageData implements IsSerializable {
	private Event event;
	private EventRegistration registration;
	private ArrayList<Data> categories;
	private ArrayList<EventAgeGroup> ageGroups;
	private ArrayList<EventVolunteerPosition> volunteerPositions;
	private ArrayList<Event> eventsInSeries;

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

	public ArrayList<Event> getEventsInSeries() {
		return eventsInSeries;
	}

	public EventRegistration getRegistration() {
		return registration;
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

	public void setEventsInSeries(ArrayList<Event> eventsInSeries) {
		this.eventsInSeries = eventsInSeries;
	}

	public void setRegistration(EventRegistration registration) {
		this.registration = registration;
	}

	public void setVolunteerPositions(ArrayList<EventVolunteerPosition> volunteerPositions) {
		this.volunteerPositions = volunteerPositions;
	}
}
