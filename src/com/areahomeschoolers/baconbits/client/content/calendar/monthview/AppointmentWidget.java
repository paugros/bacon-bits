/*
 * This file is part of gwt-cal
 * Copyright (C) 2010  Scottsdale Software LLC
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
package com.areahomeschoolers.baconbits.client.content.calendar.monthview;

import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;

/**
 * A panel used to render an <code>Appointment</code> in a <code>MonthView</code>.
 * <p>
 * Through an association to a domain-model <code>Appointment</code>, <code>AppointmentWidget</code>s allow displaying the appointment details <em>and</em> to
 * capture mouse and keyboard events.
 * </p>
 * 
 * @author Brad Rydzewski
 * @author Carlos D. Morales
 */
public class AppointmentWidget extends FocusPanel {
	/**
	 * The underlying <code>Appointment</code> represented by this panel.
	 */
	private Appointment appointment;

	/**
	 * Creates an <code>AppointmentWidget</code> with a reference to the provided <code>appointment</code>.
	 * 
	 * @param appointment
	 *            The appointment to be displayed through this panel widget
	 */
	public AppointmentWidget(Appointment appointment) {
		this.appointment = appointment;
		String hour = Formatter.formatDate(appointment.getStart(), "h");
		String ampm = Formatter.formatDate(appointment.getStart(), "a");
		String minute = Formatter.formatDate(appointment.getStart(), "mm");
		String time = "<b>" + hour;
		if (!"00".equals(minute)) {
			time += ":" + minute;
		}
		if (!"AM".equals(ampm)) {
			time += "p";
		}
		time += "</b>";
		InlineHyperlink link = new InlineHyperlink(time + " " + appointment.getTitle(), true, PageUrl.event(Integer.parseInt(appointment.getId())));
		this.setWidget(link);
	}

	/**
	 * Returns the <code>Appointment</code> this panel represents/is associated to.
	 * 
	 * @return The domain model appointment rendered through this panel
	 */
	public Appointment getAppointment() {
		return appointment;
	}
}
