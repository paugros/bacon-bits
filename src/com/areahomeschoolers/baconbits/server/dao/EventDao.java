package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

public interface EventDao {

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteAgeGroup(EventAgeGroup ageGroup);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteEventField(int fieldId);

	public void deleteEventParticipant(EventParticipant participant);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteVolunteerPosition(EventVolunteerPosition position);

	public void deleteVolunteerPositionMapping(int id);

	public Event getById(int id);

	public ArrayList<Data> getEventFieldTypes();

	public ArrayList<EventField> getFields(ArgMap<EventArg> args);

	public EventPageData getPageData(int id);

	public ArrayList<EventParticipant> getParticipants(ArgMap<EventArg> args);

	public ArrayList<Data> getVolunteers(int eventId);

	public ArrayList<Event> list(ArgMap<EventArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public Event save(Event event);

	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup);

	public EventField saveField(EventField field);

	public ArrayList<EventParticipant> saveParticipant(EventParticipant participant);

	public EventRegistration saveRegistration(EventRegistration registration);

	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position);

}
