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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {

	void deleteAgeGroup(EventAgeGroup ageGroup, AsyncCallback<Void> callback);

	void deleteEventField(int fieldId, AsyncCallback<Void> callback);

	void deleteEventParticipant(EventParticipant participant, AsyncCallback<Void> callback);

	void deleteVolunteerPosition(EventVolunteerPosition position, AsyncCallback<Void> callback);

	void deleteVolunteerPositionMapping(int id, AsyncCallback<Void> callback);

	void getById(int id, AsyncCallback<Event> callback);

	void getEventFieldTypes(AsyncCallback<ArrayList<Data>> callback);

	void getFields(ArgMap<EventArg> args, AsyncCallback<ArrayList<EventField>> callback);

	void getPageData(int id, AsyncCallback<EventPageData> callback);

	void getParticipants(ArgMap<EventArg> args, AsyncCallback<ArrayList<EventParticipant>> callback);

	void getVolunteers(int eventId, AsyncCallback<ArrayList<Data>> callback);

	void list(ArgMap<EventArg> args, AsyncCallback<ArrayList<Event>> callback);

	void save(Event event, AsyncCallback<Event> callback);

	void saveAgeGroup(EventAgeGroup ageGroup, AsyncCallback<EventAgeGroup> callback);

	void saveEventField(EventField field, AsyncCallback<EventField> callback);

	void saveParticipant(EventParticipant participant, AsyncCallback<ArrayList<EventParticipant>> callback);

	void saveRegistration(EventRegistration registration, AsyncCallback<EventRegistration> callback);

	void saveVolunteerPosition(EventVolunteerPosition position, AsyncCallback<EventVolunteerPosition> callback);

	void setVolunteerFulFilled(int id, boolean fulfilled, AsyncCallback<Void> callback);

}
