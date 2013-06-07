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

package com.areahomeschoolers.baconbits.client.content.calendar;

import com.areahomeschoolers.baconbits.client.content.calendar.agenda.AgendaView;
import com.areahomeschoolers.baconbits.client.content.calendar.dayview.DayView;
import com.areahomeschoolers.baconbits.client.content.calendar.monthview.MonthView;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public class Calendar extends CalendarWidget implements RequiresResize, ProvidesResize {

	/**
	 * The component to manage the presentation of appointments in a single day layout.
	 */
	private DayView dayView = null;

	private AgendaView agendaView = null;

	/**
	 * The component to manage the presentation of appointments in a month.
	 */
	private MonthView monthView = null;

	private CalendarViews selectedView = null;

	private Timer resizeTimer = new Timer() {
		/**
		 * Snapshot of the Calendar's height at the last time it was resized.
		 */
		private int height;

		@Override
		public void run() {

			int newHeight = getOffsetHeight();
			if (newHeight != height) {
				height = newHeight;
				doSizing();
				if (getView() instanceof MonthView) {
					doLayout();
				}
			}
		}
	};

	/**
	 * Constructs a <code>Calendar</code> with the DayView currently displayed.
	 */
	public Calendar() {
		this(CalendarViews.DAY, CalendarSettings.DEFAULT_SETTINGS);
	}

	public Calendar(CalendarSettings settings) {
		this(CalendarViews.DAY, settings);
	}

	/**
	 * Constructs a <code>Calendar</code> with the a user-defined CalendarView displayed by default.
	 */
	public Calendar(CalendarView view) {
		super();
		setView(view);
	}

	/**
	 * Constructs a <code>Calendar</code> with the given CalendarView displayed by default.
	 */
	public Calendar(CalendarViews view, CalendarSettings settings) {
		super();
		this.setSettings(settings);
		setView(view);
	}

	/**
	 * Gets the current view of this calendar.
	 * 
	 * @return Current view
	 */
	public CalendarViews getCalendarView() {
		return selectedView;
	}

	@Override
	public void onResize() {
		resizeTimer.schedule(500);
	}

	/**
	 * Sets the CalendarView that should be used by the Calendar to display the list of appointments.
	 * 
	 * @param view
	 */
	final public void setView(CalendarViews view) {
		setView(view, getDays());
	}

	/**
	 * Sets the current view of this calendar.
	 * 
	 * @param view
	 *            The ID of a view used to visualize the appointments managed by the calendar
	 * @param days
	 *            The number of days to display in the view, which can be ignored by some views.
	 */
	public void setView(CalendarViews view, int days) {
		switch (view) {
		case DAY: {
			if (dayView == null) {
				dayView = new DayView();
			}
			dayView.setDisplayedDays(days);
			setView(dayView);
			break;
		}
		case AGENDA: {
			agendaView = new AgendaView();
			setView(agendaView);
			break;
		}
		case MONTH: {
			if (monthView == null) {
				monthView = new MonthView();
			}
			setView(monthView);
			break;
		}
		}

		selectedView = view;
	}
}
