package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

public interface EventDao {

	public Event getById(int id);

	public EventPageData getPageData(int id);

	public ArrayList<Event> list(ArgMap<EventArg> args);

	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public Event save(Event event);

}
