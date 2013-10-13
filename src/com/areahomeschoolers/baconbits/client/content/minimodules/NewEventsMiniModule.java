package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

public class NewEventsMiniModule extends SimpleEventMiniModule {
	private String title = "NEWLY ADDED EVENTS";
	private String url = PageUrl.eventList() + "&newlyAdded=true";

	public NewEventsMiniModule() {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.UPCOMING_NUMBER, 5);
		args.put(EventArg.NEWLY_ADDED);
		populate(title, args, url);
	}

	public NewEventsMiniModule(ArrayList<Event> events) {
		populate(title, events, url);
	}

}
