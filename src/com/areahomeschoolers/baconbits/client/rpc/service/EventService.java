package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/event")
public interface EventService extends RemoteService {

	public void deleteAgeGroup(EventAgeGroup ageGroup);

	public void deleteEventField(int fieldId);

	public void deleteEventParticipant(EventParticipant participant);

	public void deleteVolunteerPosition(EventVolunteerPosition position);

	public void deleteVolunteerPositionMapping(int id);

	public Event getById(int id);

	public ArrayList<Data> getEventFieldTypes();

	public ArrayList<EventField> getFields(ArgMap<EventArg> args);

	public EventPageData getPageData(int id);

	public ArrayList<EventParticipant> getParticipants(ArgMap<EventArg> args);

	public Data getUnpaidBalance(int userId);

	public ArrayList<Data> getVolunteers(int eventId);

	public ArrayList<Event> list(ArgMap<EventArg> args);

	public PaypalData payForEvents(ArrayList<Integer> participantIds);

	public Event save(Event event);

	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup);

	public EventField saveEventField(EventField field);

	public ServerResponseData<ArrayList<EventParticipant>> saveParticipant(EventParticipant participant);

	public EventRegistration saveRegistration(EventRegistration registration);

	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position);

	public void setVolunteerFulFilled(int id, boolean fulfilled);

}
