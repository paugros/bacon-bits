package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DateTimeRangeBox;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventSeriesDialog extends DefaultDialog {
	private Event calendarEvent;
	private VerticalPanel vp = new VerticalPanel();
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventSeriesDialog(Event event) {
		setText("Create an Event Series");
		calendarEvent = event;

		vp.setSpacing(8);

		CheckBox cb = new CheckBox("Entire series is required when registering");
		cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				calendarEvent.setRequiredInSeries(event.getValue());
			}
		});

		vp.add(cb);

		final FlexTable dateTable = new FlexTable();
		dateTable.getElement().getStyle().setBackgroundColor("#d0e4f6");
		dateTable.setWidth("300px");

		final DateTimeRangeBox dateInput = new DateTimeRangeBox();
		Button add = new Button("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!dateInput.getValidator().validate()) {
					return;
				}

				final HandlerRegistration r = calendarEvent.addSeriesDate(dateInput.getStartDate(), dateInput.getEndDate());
				String text = Formatter.formatDateTime(dateInput.getStartDate()) + " to " + Formatter.formatDateTime(dateInput.getEndDate());

				final int row = dateTable.getRowCount();

				ClickLabel ex = new ClickLabel("X", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						r.removeHandler();
						dateTable.removeRow(row);
					}
				});

				dateTable.setWidget(row, 0, new Label(text));
				dateTable.setWidget(row, 1, ex);

				dateInput.clear();
			}
		});

		PaddedPanel pp = new PaddedPanel();
		Label text = new Label("Start/end dates:");
		text.setWordWrap(false);
		pp.add(text);
		pp.add(dateInput);
		pp.add(add);

		vp.add(pp);
		vp.add(dateTable);

		ButtonPanel bp = new ButtonPanel(this);
		final Button save = new Button("Create series");
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Common.isNullOrEmpty(calendarEvent.getSeriesDates())) {
					AlertDialog.alert("You must specify at least one series date and click Add.");
					return;
				}
				save.setEnabled(false);
				eventService.createSeries(calendarEvent, new Callback<Void>() {
					@Override
					protected void doOnSuccess(Void result) {
						hide();
						HistoryToken.set(PageUrl.event(calendarEvent.getId()) + "&tab=2");
						Application.reloadPage();
					}
				});
			}
		});
		bp.getCloseButton().setText("Cancel");
		bp.addRightButton(save);

		vp.add(bp);

		setWidget(vp);
	}
}
