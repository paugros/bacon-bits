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
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

public interface EventDao {

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void createSeries(Event event);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteAgeGroup(EventAgeGroup ageGroup);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteEventField(int fieldId);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public void deleteEventParticipant(EventParticipant participant);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteVolunteerPosition(EventVolunteerPosition position);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public void deleteVolunteerPositionMapping(int id);

	public Event getById(int id);

	public ArrayList<Data> getEventFieldTypes();

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public ArrayList<EventField> getFields(ArgMap<EventArg> args);

	public EventPageData getPageData(int id);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public ArrayList<EventParticipant> getParticipants(ArgMap<EventArg> args);

	public ArrayList<Data> getParticipantStatusList();

	public ArrayList<Data> getRegistrationSummary();

	public Data getUnpaidBalance(int userId);

	public ArrayList<Data> getVolunteers(ArgMap<EventArg> args);

	public ArrayList<Event> list(ArgMap<EventArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void overrideParticipantStatus(EventParticipant participant);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public PaypalData payForEvents(ArrayList<Integer> participantIds);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public Event save(Event event);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public EventField saveField(EventField field);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public ServerResponseData<ArrayList<EventParticipant>> saveParticipant(EventParticipant participant);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public EventRegistration saveRegistration(EventRegistration registration);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void setVolunteerFulFilled(int id, boolean fulfilled);

}
