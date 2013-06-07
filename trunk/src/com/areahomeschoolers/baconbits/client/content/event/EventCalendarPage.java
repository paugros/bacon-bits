package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.areahomeschoolers.baconbits.client.content.calendar.AppointmentStyle;
import com.areahomeschoolers.baconbits.client.content.calendar.theme.google.client.GoogleAppointmentTheme;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.CalendarPanel;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventCalendarPage implements Page {
	private VerticalPanel page;
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private CalendarPanel cp = new CalendarPanel();

	public EventCalendarPage(VerticalPanel p) {
		page = p;
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.INCLUDE_COMMUNITY);

		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				PaddedPanel pp = new PaddedPanel();
				String title = "Event Calendar";
				ClickLabel download = new ClickLabel("Click here", new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						cp.getCalendar().exportToExcel();
					}
				});
				download.addStyleName("bold");
				String ins = "to download this calendar, then upload it to your Google Calendar ";
				ins += "(<a href=\"https://support.google.com/calendar/answer/37118?hl=en\">instructions</a>).";
				HTML desc = new HTML(ins);
				for (int i = 0; i < pp.getWidgetCount(); i++) {
					pp.setCellVerticalAlignment(pp.getWidget(i), HasVerticalAlignment.ALIGN_BOTTOM);
				}

				pp.add(download);
				pp.add(desc);
				page.add(pp);

				cp.getCalendar().suspendLayout();

				final List<Appointment> mine = new ArrayList<Appointment>();
				final List<Appointment> community = new ArrayList<Appointment>();
				final List<Appointment> group = new ArrayList<Appointment>();

				for (Event e : result) {
					Appointment a = new Appointment();
					a.setReadOnly(true);
					a.setStart(e.getStartDate());
					a.setEnd(e.getEndDate());
					a.setTitle(e.getTitle());
					a.setLocation(e.getAddress());
					a.setId(Integer.toString(e.getId()));
					HTML h = new HTML(e.getDescription().replaceAll("<br>", " "));
					String text = h.getText().trim();
					a.setDescription(text);

					if (e.getCurrentUserParticipantCount() > 0) {
						a.setStyle(AppointmentStyle.GREEN);
						mine.add(a);
					} else if (e.getCategoryId() == 6) {
						a.setStyle(AppointmentStyle.BLUE_GREY);
						community.add(a);
					} else {
						a.setStyle(AppointmentStyle.DARK_PURPLE);
						group.add(a);
					}

					cp.getCalendar().addAppointment(a);
				}
				cp.getCalendar().resumeLayout();

				// create toggling legend
				VerticalPanel lp = new VerticalPanel();
				CheckBox mc = new CheckBox("My events");
				mc.setValue(true);
				mc.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							cp.getCalendar().addAppointments(mine);
						} else {
							cp.getCalendar().removeAppointments(mine);
						}
					}
				});
				mc.getElement().getStyle().setBackgroundColor(GoogleAppointmentTheme.GREEN.getBorder());

				CheckBox gc = new CheckBox("Group events");
				gc.setValue(true);
				gc.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							cp.getCalendar().addAppointments(group);
						} else {
							cp.getCalendar().removeAppointments(group);
						}
					}
				});
				gc.getElement().getStyle().setBackgroundColor(GoogleAppointmentTheme.DARK_PURPLE.getBorder());

				CheckBox cc = new CheckBox("Community events");
				cc.setValue(true);
				cc.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							cp.getCalendar().addAppointments(community);
						} else {
							cp.getCalendar().removeAppointments(community);
						}
					}
				});
				cc.getElement().getStyle().setBackgroundColor(GoogleAppointmentTheme.BLUE_GREY.getBorder());

				if (Application.isAuthenticated()) {
					lp.add(mc);
				}
				mc.addStyleName("calendarLegendItem");
				gc.addStyleName("calendarLegendItem");
				cc.addStyleName("calendarLegendItem");

				lp.setSpacing(5);
				lp.add(gc);
				lp.add(cc);
				lp.setWidth("100%");
				cp.setLegend(lp);

				page.add(cp);

				Application.getLayout().setPage(title, page);
			}
		});
	}
}
