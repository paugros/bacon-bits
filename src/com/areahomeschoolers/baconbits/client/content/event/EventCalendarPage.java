package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.CalendarPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventCalendarPage implements Page {
	private VerticalPanel page;
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventCalendarPage(VerticalPanel p) {
		page = p;
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.INCLUDE_COMMUNITY);

		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				String title = "Event Calendar";
				Label heading = new Label(title);
				heading.addStyleName("hugeText");
				page.add(heading);

				CalendarPanel cp = new CalendarPanel();
				cp.getCalendar().addSelectionHandler(new SelectionHandler<Appointment>() {
					@Override
					public void onSelection(SelectionEvent<Appointment> event) {
						HistoryToken.set(PageUrl.event(Integer.parseInt(event.getSelectedItem().getId())));
					}
				});

				cp.getCalendar().suspendLayout();

				for (Event e : result) {
					Appointment a = new Appointment();
					a.setReadOnly(true);
					a.setStart(e.getStartDate());
					a.setEnd(e.getEndDate());
					a.setTitle(e.getTitle());
					a.setId(Integer.toString(e.getId()));
					HTML h = new HTML(e.getDescription().replaceAll("<br>", " "));
					String text = h.getText().trim();
					a.setDescription(text);

					if (e.getCurrentUserParticipantCount() > 0) {
						a.setStyle(AppointmentStyle.GREEN);
					} else if (e.getCategoryId() == 6) {
						a.setStyle(AppointmentStyle.BLUE);
					} else {
						a.setStyle(AppointmentStyle.DARK_PURPLE);
					}

					cp.getCalendar().addAppointment(a);
				}
				cp.getCalendar().resumeLayout();

				page.add(cp);

				Application.getLayout().setPage(title, page);
			}
		});

	}
}
