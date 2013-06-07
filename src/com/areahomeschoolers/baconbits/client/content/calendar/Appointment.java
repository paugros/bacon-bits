/*
 * This file is part of gwt-cal
 * Copyright (C) 2010-2011  Scottsdale Software LLC
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an event that Calendar Views display and manipulate through the gwt-cal provided user interface elements.
 * <p>
 * The <code>Appointment</code> class provides a set of text-based properties to describe it, including a <em>title, description, location, createdBy</em>, etc.
 * Additional to these, there is a set of properties that exist to provide the gwt-cal components with information useful during the <code>Appointment</code>
 * rendering in the widget views (<code>allDay</code>, <code>recurring</code>, etc.)
 * </p>
 * <p>
 * All <code>Appointment</code> properties are ultimately used by the gwt-cal views and it is up to these components to decide how to render (if at all) them as
 * well as to provide appropriate runtime features to modify them.
 * </p>
 * 
 * @author Brad Rydzewski
 * @author Carlos D. Morales
 */
@SuppressWarnings("serial")
public class Appointment implements Comparable<Appointment>, Serializable {

	private String id;
	private String title;
	private String description;
	private Date start;
	private Date end;
	private String location;
	private String createdBy;
	private List<Attendee> attendees = new ArrayList<Attendee>();
	private boolean allDay = false;
	private AppointmentStyle style = AppointmentStyle.DEFAULT;
	private String customStyle;
	private boolean readOnly = false;

	/**
	 * <p>
	 * Creates an <code>Appointment</code> with the following attributes set to <code>null</code>
	 * 
	 * <ul>
	 * <li><code>title</code></li>
	 * <li><code>description</code></li>
	 * <li><code>start</code></li>
	 * <li><code>end</code></li>
	 * <li><code><code>location</code></li>
	 * <li><code>createdBy</code></li>
	 * </ul>
	 * the <code>attendees</code> collection empty the <code>allDay</code> and the <code>readOnly</code> property <code>false</code>.
	 * </p>
	 * 
	 */
	public Appointment() {

	}

	public Appointment cloneAppointment() {
		Appointment clone = new Appointment();
		clone.setId(this.id);
		clone.setAllDay(this.allDay);
		clone.setAttendees(new ArrayList<Attendee>(this.attendees));
		clone.setCreatedBy(this.createdBy);
		clone.setDescription(this.description);
		clone.setEnd(DateUtils.newDate(this.end));
		clone.setLocation(this.location);
		clone.setStart(DateUtils.newDate(this.start));
		clone.setTitle(this.title);
		clone.setStyle(this.style);
		clone.setCustomStyle(this.customStyle);
		clone.setReadOnly(this.readOnly);

		return clone;
	}

	/**
	 * Compares this <code>Appointment</code> with the specified <code>appointment</code> based first on the <code>start</code> dates of each appointment and
	 * then (if they happen to be the same), on the <code>end</code> dates.
	 * 
	 * @param appointment
	 *            The appointment to compare this one to
	 * @return a negative integer if <code>this</code> appointment <em>is before</em> <code>appointment</code>, <code>zero</code> if both appointments have the
	 *         same <code>start</code>/ <code>end</code> dates, and a positive integer if <code>this</code> appointment <em>is after</em>
	 *         <code>appointment</code>.
	 */
	@Override
	public int compareTo(final Appointment appointment) {
		int compare = this.getStart().compareTo(appointment.getStart());

		if (compare == 0) {
			compare = appointment.getEnd().compareTo(this.getEnd());
		}

		return compare;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Appointment)) {
			return false;
		}
		Appointment other = (Appointment) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (allDay != other.allDay) {
			return false;
		}
		if (attendees == null) {
			if (other.attendees != null) {
				return false;
			}
		} else if (!attendees.equals(other.attendees)) {
			return false;
		}
		if (createdBy == null) {
			if (other.createdBy != null) {
				return false;
			}
		} else if (!createdBy.equals(other.createdBy)) {
			return false;
		}
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (readOnly != other.readOnly) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the collection of associated attendees.
	 * 
	 * @return The currently configured list of attendees
	 */
	public List<Attendee> getAttendees() {
		return attendees;
	}

	/**
	 * Returns a creator of this <code>Appointment</code>.
	 * 
	 * @return The <code>appointment</code> creator description.
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	public String getCustomStyle() {
		return customStyle;
	}

	/**
	 * Returns a description for this <code>Appointment</code>.
	 * 
	 * @return The <code>appointment</code>'s description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the configured end time-stamp of this <code>Appointment</code>.
	 * 
	 * @return A date object with the date and time this appointment ends on
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Returns the unique identifier for this <code>Appointment</code>. The field is optional (and not used by gwt-cal) and therefore may be null.
	 * 
	 * @return A unique identifier for this Appointment (optional).
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns a location of this <code>Appointment</code>.
	 * 
	 * @return The <code>appointment</code> location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the configured start time-stamp of this <code>Appointment</code>.
	 * 
	 * @return A date object with the date and time this appointment starts on
	 */
	public Date getStart() {
		return start;
	}

	public AppointmentStyle getStyle() {
		return style;
	}

	/**
	 * Returns the identifying title of this <code>Appointment</code>.
	 * 
	 * @return The title's short text
	 */
	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Boolean.valueOf(this.allDay).hashCode();
		result = prime * result + ((attendees == null) ? 0 : attendees.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + Boolean.valueOf(this.readOnly).hashCode();

		return result;
	}

	/**
	 * Returns the configured value of the <code>allDay</code> property, which indicates if this <code>Appointment</code> should be considered as spanning all
	 * day. It is left to the view rendering this <code>Appointment</code> to decide how to render an appointment based on this property value. For instance,
	 * the month view, will display the <code>Appointment</code> at the top of the days in a week.
	 * 
	 * @return The current value of the <code>allDay</code> property
	 */
	public boolean isAllDay() {
		return allDay;
	}

	/**
	 * Tells whether this <code>Appointment</code> spans more than a single day, based on its <code>start</code> and <code>end</code> properties.
	 * 
	 * @return <code>true</code> if the <code>start</code> and <code>end</code> dates fall on different dates, <code>false</code> otherwise.
	 */
	public boolean isMultiDay() {
		if (getEnd() != null && getStart() != null) {
			return !DateUtils.areOnTheSameDay(getEnd(), getStart());
		}
		throw new IllegalStateException("Calculating isMultiDay with no start/end dates set");
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Configures the the <code>allDay</code> property, which indicates if this <code>Appointment</code> should be considered as spanning all day. It is left to
	 * the view rendering this <code>Appointment</code> to decide how to render an appointment based on this property value. For instance, the month view, will
	 * display the <code>Appointment</code> at the top of the days in a week.
	 * 
	 * @param allDay
	 *            The current value of the <code>allDay</code> property
	 */
	public void setAllDay(final boolean allDay) {
		this.allDay = allDay;
	}

	/**
	 * Sets the attendees associated to this <code>Appointment</code>.
	 * 
	 * @param attendees
	 *            The entities associated (<em>attending</em>) this <code>Appointment</code>
	 */
	public void setAttendees(final List<Attendee> attendees) {
		this.attendees = attendees;
	}

	/**
	 * Sets the creator of this <code>Appointment</code>.
	 * 
	 * @param createdBy
	 *            The <code>appointment</code> creator description.
	 */
	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCustomStyle(final String customStyle) {
		this.customStyle = customStyle;
	}

	/**
	 * Sets the description of this <code>Appointment</code>.
	 * 
	 * @param description
	 *            The title's short text
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Sets the end time-stamp of this <code>Appointment</code>.
	 * 
	 * @param end
	 *            A date object with the date and time this appointment ends on
	 */
	public void setEnd(final Date end) {
		this.end = end;
	}

	/**
	 * Sets the unique identifier of this <code>Appointment</code>. This identifier is optional.
	 * 
	 * @param id
	 *            Arbitrary string to uniquely identify the appointment.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Sets the location of this <code>Appointment</code>.
	 * 
	 * @param location
	 *            The <code>appointment</code> location
	 */
	public void setLocation(final String location) {
		this.location = location;
	}

	public void setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Sets the start time-stamp of this <code>Appointment</code>.
	 * 
	 * @param start
	 *            A date object with the date and time this appointment starts
	 */
	public void setStart(final Date start) {
		this.start = start;
	}

	public void setStyle(final AppointmentStyle style) {
		this.style = style;
	}

	/**
	 * Sets the identifying title of this <code>Appointment</code>.
	 * 
	 * @param title
	 *            The title's short text
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
}
