package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

public class UpcomingEventsMiniModule extends EventMiniModule {
	private String title = "UPCOMING EVENTS";

	public UpcomingEventsMiniModule() {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.UPCOMING_NUMBER, 5);
		populate(title, args, null);
	}

	public UpcomingEventsMiniModule(ArrayList<Event> events) {
		populate(title, events, null);
	}

}
