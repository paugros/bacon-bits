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

public class EventMiniModule extends Composite {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel vp = new VerticalPanel();

	public EventMiniModule() {
		initWidget(vp);
	}

	public EventMiniModule(String title, ArrayList<Event> events, String extraParams) {
		this();
		populate(title, events, extraParams);
	}

	protected void populate(final String title, ArgMap<EventArg> args, final String extraParams) {
		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				if (!result.isEmpty()) {
					populate(title, result, extraParams);
				} else {
					removeFromParent();
				}
			}
		});
	}

	protected void populate(String title, ArrayList<Event> events, String extraParams) {
		if (Common.isNullOrEmpty(events)) {
			removeFromParent();
			return;
		}

		vp.addStyleName("module moduleEventPanel");

		if (extraParams == null) {
			extraParams = "";
		}
		VerticalPanel lvp = new VerticalPanel();
		lvp.setWidth("100%");
		lvp.setSpacing(8);

		Label label = new Label(title);
		label.addStyleName("moduleTitle");
		lvp.add(label);

		for (Event e : events) {
			VerticalPanel ep = new VerticalPanel();
			Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
			link.addStyleName("mediumText");

			ep.add(link);

			String subText = Formatter.formatDateTime(e.getStartDate());
			if (e.getCategoryId() == 6) {
				subText += " - ";
				if (e.getPrice() > 0) {
					subText += Formatter.formatCurrency(e.getPrice());
				} else {
					subText += "Free";
				}
			}
			Label date = new Label(subText);
			date.addStyleName("italic");
			ep.add(date);

			HTML h = new HTML();
			h.setHTML(e.getDescription().replaceAll("<br>", " "));
			String text = h.getText().trim();
			if (text.length() > 100) {
				text = text.substring(0, 101) + "...";
			}
			ep.add(new Label(text));

			lvp.add(ep);
		}

		if (events.isEmpty()) {
			lvp.add(new Label("None right now."));
		} else {
			String url = PageUrl.eventList() + extraParams;
			lvp.add(new Hyperlink("See more events...", url));
		}

		vp.add(lvp);
	}
}
