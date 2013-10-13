package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

public class MyEventsMiniModule extends SimpleEventMiniModule {
	private String title = "MY UPCOMING EVENTS";
	private String url = PageUrl.user(Application.getCurrentUserId()) + "&tab=1";

	public MyEventsMiniModule() {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.UPCOMING_NUMBER, 5);
		args.put(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID, Application.getCurrentUserId());
		populate(title, args, url);
	}

	public MyEventsMiniModule(ArrayList<Event> events) {
		populate(title, events, url);
	}

}
