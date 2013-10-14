package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleEventMiniModule extends Composite {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel vp = new VerticalPanel();

	public SimpleEventMiniModule() {
		initWidget(vp);
	}

	public SimpleEventMiniModule(String title, ArrayList<Event> events, String url) {
		this();
		populate(title, events, url);
	}

	protected void populate(final String title, ArgMap<EventArg> args, final String url) {
		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				if (!result.isEmpty()) {
					populate(title, result, url);
				} else {
					setVisible(false);
					removeFromParent();
				}
			}
		});
	}

	protected void populate(String title, ArrayList<Event> events, String url) {
		if (Common.isNullOrEmpty(events)) {
			removeFromParent();
			return;
		}

		vp.addStyleName("module");
		vp.setSpacing(8);

		Label label = new Label(title);
		label.addStyleName("moduleTitle");
		vp.add(label);

		for (Event e : events) {
			VerticalPanel mhp = new VerticalPanel();

			Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
			link.addStyleName("mediumText");
			mhp.add(link);

			HTML date = new HTML(Formatter.formatDateTime(e.getStartDate()));
			date.setWordWrap(false);
			date.addStyleName("italic");
			mhp.add(date);

			vp.add(mhp);
		}

		if (events.size() == 5) {
			vp.add(new Hyperlink("See more...", url));
		}
	}

}
