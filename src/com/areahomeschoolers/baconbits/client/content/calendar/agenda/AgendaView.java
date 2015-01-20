/*
 * This file is part of gwt-cal
 * Copyright (C) 2009  Scottsdale Software LLC
 * 
 * gwt-cal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/
 */

package com.areahomeschoolers.baconbits.client.content.calendar.agenda;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarView;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarWidget;
import com.areahomeschoolers.baconbits.client.content.calendar.DateUtils;
import com.areahomeschoolers.baconbits.client.content.calendar.util.AppointmentUtil;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.areahomeschoolers.baconbits.client.widgets.DefaultInlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class AgendaView extends CalendarView {

	/**
	 * Adapter class that maps an Appointment to the widgets (DIV's, etc) that represent it on the screen. This is necessary because a single appointment is
	 * represented by many widgets. For example, an appointment is represented by a title widget, a description widget, and has a "get more details" label.
	 * 
	 * By mapping an appointment to these widgets we can easily figure out which appointment the user is interacting with as they click around the AgendaView.
	 * 
	 */
	class AgendaViewAppointmentAdapter {
		private Widget titleLabel;
		private Widget dateLabel;
		private Appointment appointment;

		public AgendaViewAppointmentAdapter(Widget titleLabel, Widget dateLabel, Appointment appointment) {
			super();
			this.titleLabel = titleLabel;
			this.dateLabel = dateLabel;
			this.appointment = appointment;
		}

		public Appointment getAppointment() {
			return appointment;
		}

		public Widget getDateLabel() {
			return dateLabel;
		}

		public Widget getTitleLabel() {
			return titleLabel;
		}
	}

	/**
	 * FlexTable used to display a list of appointments.
	 */
	private FlexTable appointmentGrid = new FlexTable();

	/**
	 * List of appointment adapters, used to map widgets to the appointments they represent.
	 */
	private ArrayList<AgendaViewAppointmentAdapter> appointmentAdapterList = new ArrayList<AgendaViewAppointmentAdapter>();

	/**
	 * DateTime format used to represent a day.
	 */
	private static final DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("EEE MMM d");

	/**
	 * DateTime format used when displaying an appointments start and end time.
	 */
	private static final DateTimeFormat DEFAULT_TIME_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.TIME_SHORT);

	/**
	 * Style used to format this view.
	 */
	private String styleName = "gwt-cal-ListView";

	/**
	 * Adds the calendar view to the calendar widget and performs required formatting.
	 */
	@Override
	public void attach(CalendarWidget widget) {
		super.attach(widget);

		appointmentGrid.setCellPadding(5);
		appointmentGrid.setCellSpacing(0);
		appointmentGrid.setBorderWidth(0);
		appointmentGrid.setWidth("100%");
		calendarWidget.getRootPanel().add(appointmentGrid);
		calendarWidget.getRootPanel().add(appointmentGrid);
	}

	@Override
	public void doLayout() {

		appointmentAdapterList.clear();
		appointmentGrid.clear();
		for (int i = appointmentGrid.getRowCount() - 1; i >= 0; i--) {
			appointmentGrid.removeRow(i);
		}

		// Get the start date, make sure time is 0:00:00 AM
		Date startDate = (Date) calendarWidget.getDate().clone();
		Date today = new Date();
		Date endDate = (Date) calendarWidget.getDate().clone();
		endDate = ClientDateUtils.addDays(endDate, 1);
		DateUtils.resetTime(today);
		DateUtils.resetTime(startDate);
		DateUtils.resetTime(endDate);

		int row = 0;

		for (int i = 0; i < calendarWidget.getDays(); i++) {

			// Filter the list by date
			List<Appointment> filteredList = AppointmentUtil.filterListByDate(calendarWidget.getAppointments(), startDate, endDate);

			if (filteredList != null && filteredList.size() > 0) {

				appointmentGrid.setText(row, 0, DEFAULT_DATE_FORMAT.format(startDate));

				appointmentGrid.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
				appointmentGrid.getFlexCellFormatter().setRowSpan(row, 0, filteredList.size());
				appointmentGrid.getFlexCellFormatter().setStyleName(row, 0, "dateCell");
				int startingCell = 1;

				// Row styles will alternate, so we set the style accordingly
				String rowStyle = (i % 2 == 0) ? "row" : "row-alt";

				// If a Row represents the current date (Today) then we style it differently
				if (startDate.equals(today)) {
					rowStyle += "-today";
				}

				for (Appointment appt : filteredList) {
					// add the time range
					String timeSpanString = DEFAULT_TIME_FORMAT.format(appt.getStart()) + " - " + DEFAULT_TIME_FORMAT.format(appt.getEnd());
					DefaultInlineHyperlink timeSpanLink = new DefaultInlineHyperlink(timeSpanString.toLowerCase(), PageUrl.event(Integer.parseInt(appt.getId())));

					appointmentGrid.setWidget(row, startingCell, timeSpanLink);

					// add the title and description
					FlowPanel titleContainer = new FlowPanel();
					DefaultInlineHyperlink titleLink = new DefaultInlineHyperlink(appt.getTitle(), PageUrl.event(Integer.parseInt(appt.getId())));
					titleContainer.add(titleLink);
					InlineLabel descLabel = new InlineLabel(" - " + appt.getDescription());
					descLabel.setStyleName("descriptionLabel");
					titleContainer.add(descLabel);
					appointmentGrid.setWidget(row, startingCell + 1, titleContainer);

					// Format the Cells
					appointmentGrid.getCellFormatter().setVerticalAlignment(row, startingCell, HasVerticalAlignment.ALIGN_TOP);
					appointmentGrid.getCellFormatter().setVerticalAlignment(row, startingCell + 1, HasVerticalAlignment.ALIGN_TOP);
					appointmentGrid.getCellFormatter().setStyleName(row, startingCell, "timeCell");
					appointmentGrid.getCellFormatter().setStyleName(row, startingCell + 1, "titleCell");
					appointmentGrid.getRowFormatter().setStyleName(row, rowStyle);

					// increment the row
					// make sure the starting column is reset to 0
					startingCell = 0;
					row++;
				}
			}

			// increment the date
			endDate = ClientDateUtils.addDays(endDate, 1);
			startDate = ClientDateUtils.addDays(startDate, 1);
		}
	}

	@Override
	public String getStyleName() {
		return styleName;
	}

	@Override
	public void onAppointmentSelected(Appointment appt) {

	}

	@Override
	public void onDoubleClick(Element element, Event event) {

	}

	@Override
	public void onMouseOver(Element element, Event event) {

	}

	@Override
	public void onSingleClick(Element element, Event event) {

	}

	/**
	 * Given Widget w determine which appointment was clicked. This is necessary because each appointment has 3 widgets that can be clicked - the title, date
	 * range and description.
	 * 
	 * @param w
	 *            Widget that was clicked.
	 * @return Appointment mapped to that widget.
	 */
	protected AgendaViewAppointmentAdapter getAppointmentFromClickedWidget(Widget w) {
		for (AgendaViewAppointmentAdapter a : appointmentAdapterList) {
			if (w.equals(a.dateLabel) || w.equals(a.titleLabel)) {
				return a;
			}
		}
		return null;
	}
}
