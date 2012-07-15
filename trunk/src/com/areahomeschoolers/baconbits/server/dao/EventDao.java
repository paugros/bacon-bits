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
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

public interface EventDao {

	public void deleteEventField(int fieldId);

	public Event getById(int id);

	public ArrayList<Data> getEventFieldTypes();

	public ArrayList<EventField> getFields(ArgMap<EventArg> args);

	public EventPageData getPageData(int id);

	public ArrayList<Event> list(ArgMap<EventArg> args);

	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public Event save(Event event);

	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup);

	public EventField saveField(EventField field);

	public ServerResponseData<EventRegistration> saveRegistration(EventRegistration registration);

	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position);

}
