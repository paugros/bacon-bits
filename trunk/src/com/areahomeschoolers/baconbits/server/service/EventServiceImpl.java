package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.server.dao.EventDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
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

@Controller
@RequestMapping("/event")
public class EventServiceImpl extends GwtController implements EventService {

	private static final long serialVersionUID = 1L;
	private final EventDao dao;

	@Autowired
	public EventServiceImpl(EventDao dao) {
		this.dao = dao;
	}

	@Override
	public void deleteEventField(int fieldId) {
		dao.deleteEventField(fieldId);
	}

	@Override
	public Event getById(int id) {
		return dao.getById(id);
	}

	@Override
	public ArrayList<Data> getEventFieldTypes() {
		return dao.getEventFieldTypes();
	}

	@Override
	public ArrayList<EventField> getFields(ArgMap<EventArg> args) {
		return dao.getFields(args);
	}

	@Override
	public EventPageData getPageData(int id) {
		return dao.getPageData(id);
	}

	@Override
	public ArrayList<Event> list(ArgMap<EventArg> args) {
		return dao.list(args);
	}

	@Override
	public Event save(Event event) {
		return dao.save(event);
	}

	@Override
	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup) {
		return dao.saveAgeGroup(ageGroup);
	}

	@Override
	public EventField saveEventField(EventField field) {
		return dao.saveField(field);
	}

	@Override
	public ServerResponseData<EventRegistration> saveRegistration(EventRegistration registration) {
		return dao.saveRegistration(registration);
	}

	@Override
	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position) {
		return dao.saveVolunteerPosition(position);
	}

}
