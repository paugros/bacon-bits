package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {

	void getById(int id, AsyncCallback<Event> callback);

	void getPageData(int id, AsyncCallback<EventPageData> callback);

	void list(ArgMap<EventArg> args, AsyncCallback<ArrayList<Event>> callback);

	void save(Event event, AsyncCallback<Event> callback);

}
