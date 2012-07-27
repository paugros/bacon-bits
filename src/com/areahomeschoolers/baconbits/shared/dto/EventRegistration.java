package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;

public final class EventRegistration extends EntityDto<EventRegistration> {
	private static final long serialVersionUID = 1L;

	private Date addedDate;
	private int eventId;
	private int addedById;
	private ArrayList<EventRegistrationParticipant> participants = new ArrayList<EventRegistrationParticipant>();
	private ArrayList<EventVolunteerPosition> volunteerPositions = new ArrayList<EventVolunteerPosition>();

	// aux
	private boolean canceled;

	public EventRegistration() {

	}

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public boolean getCanceled() {
		return canceled;
	}

	public int getEventId() {
		return eventId;
	}

	public ArrayList<EventRegistrationParticipant> getParticipants() {
		return participants;
	}

	public ArrayList<EventVolunteerPosition> getVolunteerPositions() {
		return volunteerPositions;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setCanceled(boolean cancelAll) {
		this.canceled = cancelAll;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setParticipants(ArrayList<EventRegistrationParticipant> participants) {
		this.participants = participants;
	}

	public void setVolunteerPositions(ArrayList<EventVolunteerPosition> volunteerPositions) {
		this.volunteerPositions = volunteerPositions;
	}

}
