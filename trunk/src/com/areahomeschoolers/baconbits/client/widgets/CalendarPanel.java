package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.bradrydzewski.gwt.calendar.client.Calendar;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.bradrydzewski.gwt.calendar.client.agenda.AgendaView;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabBar;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.datepicker.client.DatePicker;

public class CalendarPanel extends FlowPanel {

	// private class CalendarAppointmentPanel extends PopupPanel {
	// private VerticalPanel vp = new VerticalPanel();
	//
	// public CalendarAppointmentPanel(Appointment a) {
	// Hyperlink link = new Hyperlink(a.getTitle(), PageUrl.event(Integer.parseInt(a.getId())));
	// link.setStyleName("largeText");
	// vp.add(link);
	// Label date = new Label(Formatter.formatTime(a.getStart()) + " to " + Formatter.formatTime(a.getEnd()));
	// date.getElement().getStyle().setMarginTop(10, Unit.PX);
	// vp.add(date);
	// Label d = new Label(a.getDescription());
	// vp.add(d);
	//
	// SimplePanel sp = new SimplePanel(vp);
	// sp.addStyleName("heavyPadding");
	// setWidget(sp);
	// setAutoHideEnabled(true);
	// setWidth("300px");
	// }
	// }

	private Calendar calendar = null;
	private DatePicker datePicker = new DatePicker();
	private FlexTable layoutTable = new FlexTable();
	private AbsolutePanel leftPanel = new AbsolutePanel();
	private AbsolutePanel topPanel = new AbsolutePanel();
	private DecoratorPanel dayViewDecorator = new DecoratorPanel();
	private DecoratorPanel datePickerDecorator = new DecoratorPanel();
	private DecoratedTabBar calendarViewsTabBar = new DecoratedTabBar();

	private CalendarSettings settings = new CalendarSettings();

	public CalendarPanel() {
		configureCalendar();
		configureViewsTabBar();

		datePicker.setValue(new Date());
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				calendar.setDate(event.getValue());
			}
		});

		final Button leftWeekButton = new Button("<b>&lt;</b>");
		final Button rightWeekButton = new Button("<b>&gt;</b>");
		final Button todayButton = new Button("<b>Today</b>");

		leftWeekButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickChangeDateButton(-7);
			}
		});
		rightWeekButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickChangeDateButton(7);
			}
		});
		todayButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickChangeDateButton(0);
			}

		});

		topPanel.add(calendarViewsTabBar);
		topPanel.setStyleName("daysTabBar");
		leftPanel.setStyleName("leftPanel");
		leftPanel.add(datePickerDecorator);

		datePickerDecorator.add(datePicker);
		dayViewDecorator.add(calendar);

		layoutTable.setWidth("99%");
		layoutTable.setCellPadding(0);
		layoutTable.setCellSpacing(0);
		layoutTable.setText(0, 0, "");
		layoutTable.setWidget(0, 1, topPanel);
		layoutTable.setWidget(1, 1, dayViewDecorator);
		layoutTable.setWidget(1, 0, leftPanel);
		layoutTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		layoutTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
		layoutTable.getCellFormatter().setWidth(1, 0, "50px");
		add(layoutTable);

		getElement().getStyle().setPadding(10, Unit.PX);
	}

	public Calendar getCalendar() {
		return calendar;
	}

	private void clickChangeDateButton(int numOfDays) {
		if (numOfDays == 0) {
			calendar.setDate(new Date());
		} else {
			calendar.addDaysToDate(numOfDays);
		}
	}

	/**
	 * Configures the calendar widget in the calendar panel. Configuration includes handlers for the <code>delete</code> and <code>open
	 * appointment</code> operations, a set of random appointments and two multi day appointments.
	 * 
	 * @see AppointmentBuilder#build()
	 */
	private void configureCalendar() {

		// change hour offset to false to facilitate google style
		settings.setOffsetHourLabels(false);
		// settings.setEnableDragDrop(true);
		// settings.setEnableDragDropCreation(true);
		// settings.setTimeBlockClickNumber(Click.Drag);

		calendar = new Calendar();
		calendar.setSettings(settings);
		// calendar.setView(Calendar.DAY_VIEW);
		calendar.setWidth("100%");
		// calendar.addOpenHandler(new OpenHandler<Appointment>() {
		// @Override
		// public void onOpen(OpenEvent<Appointment> event) {
		// CalendarAppointmentPanel p = new CalendarAppointmentPanel(event.getTarget());
		// p.center();
		// }
		// });
	}

	/**
	 * Configures the tab bar that allows users to switch views in the calendar.
	 */
	private void configureViewsTabBar() {
		calendarViewsTabBar.addTab("1 Day");
		calendarViewsTabBar.addTab("3 Day");
		calendarViewsTabBar.addTab("School Week");
		calendarViewsTabBar.addTab("Week");
		calendarViewsTabBar.addTab("Agenda");
		calendarViewsTabBar.addTab("Month");

		calendarViewsTabBar.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int tabIndex = event.getSelectedItem();
				if (tabIndex == 0) {
					calendar.setView(CalendarViews.DAY, 1);
				} else if (tabIndex == 1) {
					calendar.setView(CalendarViews.DAY, 3);
				} else if (tabIndex == 2) {
					calendar.setView(CalendarViews.DAY, 5);
				} else if (tabIndex == 3) {
					calendar.setView(CalendarViews.DAY, 7);
				} else if (tabIndex == 4) {
					calendar.setView(new AgendaView());
				} else if (tabIndex == 5) {
					calendar.setView(CalendarViews.MONTH);
				}
			}
		});

		calendarViewsTabBar.selectTab(3, true);
	}

	// private DialogBox createCalendaryEventDialogBox(Object event) {
	// // Create a dialog box and set the caption text
	// final DialogBox dialogBox = new DialogBox();
	// dialogBox.ensureDebugId("cwDialogBox");
	// dialogBox.setText("Calendar event");
	//
	// // Create a table to layout the content
	// VerticalPanel dialogContents = new VerticalPanel();
	// dialogContents.setSpacing(4);
	// dialogBox.setWidget(dialogContents);
	//
	// // Add some text to the top of the dialog
	// HTML eventName = new HTML("Event Name");
	// dialogContents.add(eventName);
	// final TextBox eventNameText = new TextBox();
	// dialogContents.add(eventNameText);
	// eventNameText.ensureDebugId("cwBasicText-textbox");
	// eventNameText.setFocus(true);
	// eventNameText.selectAll();
	// HTML when = new HTML("When");
	// dialogContents.add(when);
	// final TextBox eventWhenText = new TextBox();
	// dialogContents.add(eventWhenText);
	// HTML until = new HTML("To");
	// dialogContents.add(until);
	// final TextBox eventUntilText = new TextBox();
	// dialogContents.add(eventUntilText);
	// // Description
	// HTML description = new HTML("Description");
	// dialogContents.add(description);
	// // Add a text area
	// final TextArea descriptionText = new TextArea();
	// descriptionText.ensureDebugId("cwBasicText-textarea");
	// descriptionText.setVisibleLines(5);
	//
	// dialogContents.add(descriptionText);
	//
	// OpenEvent<Appointment> targetAppointment = null;
	// Appointment ap = null;
	// if (event instanceof OpenEvent) {
	// targetAppointment = (OpenEvent<Appointment>) event;
	// Appointment appt = targetAppointment.getTarget();
	// eventNameText.setText(appt.getTitle());
	// eventWhenText.setText(appt.getStart().toString());
	//
	// eventUntilText.setText(appt.getEnd().toString());
	// descriptionText.setText(appt.getDescription());
	// ap = appt;
	// }
	// final Appointment appointment = ap;
	//
	// // Add a close button at the bottom of the dialog
	// Button closeButton = new Button("Close", new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// appointment.setTitle(eventNameText.getText());
	// appointment.setDescription(descriptionText.getText());
	// dialogBox.hide();
	// }
	// });
	// dialogContents.add(closeButton);
	// // Add a close button at the bottom of the dialog
	// Button deleteButton = new Button("Delete", new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// calendar.removeAppointment(appointment);
	// dialogBox.hide();
	// }
	// });
	// dialogContents.add(deleteButton);
	//
	// return dialogBox;
	// }
}
