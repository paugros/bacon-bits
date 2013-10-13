package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

public class CommunityEventsMiniModule extends EventMiniModule {
	private String title = "COMMUNITY EVENTS";
	private String params = "&showCommunity=true";

	public CommunityEventsMiniModule() {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.UPCOMING_NUMBER, 5);
		args.put(EventArg.ONLY_COMMUNITY);
		populate(title, args, params);
	}

	public CommunityEventsMiniModule(ArrayList<Event> events) {
		populate(title, events, params);
	}

}
