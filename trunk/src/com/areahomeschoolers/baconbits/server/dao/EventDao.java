package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

public interface EventDao {

	public Event getById(int id);

	public EventPageData getPageData(int id);

	public ArrayList<Event> list(ArgMap<EventArg> args);

	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public Event save(Event event);

	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup);

	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position);

}
