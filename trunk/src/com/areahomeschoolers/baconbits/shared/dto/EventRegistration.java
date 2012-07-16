package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;

public final class EventRegistration extends EntityDto<EventRegistration> {
	private static final long serialVersionUID = 1L;

	private Date addedDate;
	private int eventId;
	private boolean waiting, canceled, attended;
	private ArrayList<EventRegistrationParticipant> participants = new ArrayList<EventRegistrationParticipant>();
	private ArrayList<EventVolunteerPosition> volunteerPositions = new ArrayList<EventVolunteerPosition>();

	public EventRegistration() {

	}

	public Date getAddedDate() {
		return addedDate;
	}

	public boolean getAttended() {
		return attended;
	}

	public boolean getCanceled() {
		return canceled;
	}

	public int getEventTypeId() {
		return eventId;
	}

	public ArrayList<EventRegistrationParticipant> getParticipants() {
		return participants;
	}

	public boolean getWaiting() {
		return waiting;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setParticipants(ArrayList<EventRegistrationParticipant> participants) {
		this.participants = participants;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

	public ArrayList<EventVolunteerPosition> getVolunteerPositions() {
		return volunteerPositions;
	}

	public void setVolunteerPositions(ArrayList<EventVolunteerPosition> volunteerPositions) {
		this.volunteerPositions = volunteerPositions;
	}

}
