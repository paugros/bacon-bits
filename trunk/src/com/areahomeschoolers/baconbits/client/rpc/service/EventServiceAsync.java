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
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {
	public void createSeries(Event event, AsyncCallback<Void> callback);

	public void deleteAgeGroup(EventAgeGroup ageGroup, AsyncCallback<Void> callback);

	public void deleteEventField(int fieldId, AsyncCallback<Void> callback);

	public void deleteEventParticipant(EventParticipant participant, AsyncCallback<Void> callback);

	public void deleteVolunteerPosition(EventVolunteerPosition position, AsyncCallback<Void> callback);

	public void deleteVolunteerPositionMapping(int id, AsyncCallback<Void> callback);

	public void getById(int id, AsyncCallback<Event> callback);

	public void getEventFieldTypes(AsyncCallback<ArrayList<Data>> callback);

	public void getFields(ArgMap<EventArg> args, AsyncCallback<ArrayList<EventField>> callback);

	public void getHomePageData(AsyncCallback<HomePageData> callback);

	public void getPageData(int id, AsyncCallback<EventPageData> callback);

	public void getParticipants(ArgMap<EventArg> args, AsyncCallback<ArrayList<EventParticipant>> callback);

	public void getParticipantStatusList(AsyncCallback<ArrayList<Data>> callback);

	public void getRegistrationSummary(AsyncCallback<ArrayList<Data>> callback);

	public void getVolunteers(ArgMap<EventArg> args, AsyncCallback<ArrayList<Data>> callback);

	public void list(ArgMap<EventArg> args, AsyncCallback<ArrayList<Event>> callback);

	public void overrideParticipantStatus(EventParticipant participant, AsyncCallback<Void> callback);

	public void payForEvents(ArrayList<Integer> participantIds, AsyncCallback<PaypalData> callback);

	public void save(Event event, AsyncCallback<Event> callback);

	public void saveAgeGroup(EventAgeGroup ageGroup, AsyncCallback<EventAgeGroup> callback);

	public void saveEventField(EventField field, AsyncCallback<EventField> callback);

	public void saveParticipant(EventParticipant participant, AsyncCallback<ServerResponseData<ArrayList<EventParticipant>>> callback);

	public void saveRegistration(EventRegistration registration, AsyncCallback<EventRegistration> callback);

	public void saveVolunteerPosition(EventVolunteerPosition position, AsyncCallback<EventVolunteerPosition> callback);

	public void setVolunteerFulFilled(int id, boolean fulfilled, AsyncCallback<Void> callback);

}
