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

package com.areahomeschoolers.baconbits.client.content.calendar;

import java.io.Serializable;

/**
 * A simple JavaBean class representing an entity associated to an appointment, most likely a person, but might as well be a resource (like a conference room or
 * a projector).
 */
@SuppressWarnings("serial")
public class Attendee implements Serializable {

	/**
	 * The <code>Attendee</code> id. This field can be used to relate the Attendee with some external source (contact, resource, ....)
	 */
	private String id;

	/**
	 * The <code>Attendee</code> name (if a person) or description (when a resource).
	 */
	private String name;

	/**
	 * This <code>Attendee</code> email address.
	 */
	private String email;

	/**
	 * The status of attendance of this attendee to an <code>Appointment</code>.
	 */
	private Attending attending = Attending.Maybe;

	/**
	 * A URL to an image to depict this <code>Attendee</code>.
	 */
	private String imageUrl;

	/**
	 * Returns the attendance status of <code>this</code> attendant to the <code>Appointment</code> referencing it.
	 * 
	 * @return The attendance status of this <code>Attendee</code>
	 */
	public Attending getAttending() {
		return attending;
	}

	/**
	 * Returns this <code>Attendee</code> email address.
	 * 
	 * @return The email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns the attendance ID.
	 * 
	 * @return The attendance ID
	 * @since 0.9.4
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the URL to an image to (optionally) depict this <code>Attendee</code> in the views.
	 * 
	 * @return A URL (relative or absolute) meaningful within the deployed application context
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * Returns the name (if a person) or description (when a resource) of this <code>Attendee</code>.
	 * 
	 * @return The currently configured name of this attendee
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the attendance status of <code>this</code> attendant to the <code>Appointment</code> referencing it.
	 * 
	 * @param attending
	 *            The attendance status
	 */
	public void setAttending(Attending attending) {
		this.attending = attending;
	}

	/**
	 * Sets this <code>Attendee</code> email address.
	 * 
	 * @param email
	 *            The email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the attendance the ID.
	 * 
	 * @param id
	 *            The ID
	 * @since 0.9.4
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the URL to an image to (optionally) depict this <code>Attendee</code> in the views.
	 * 
	 * @param imageUrl
	 *            A URL (relative or absolute) meaningful within the
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * Sets the name (if a person) or description (when a resource) of this <code>Attendee</code>.
	 * 
	 * @param name
	 *            The name of this attendee
	 */
	public void setName(String name) {
		this.name = name;
	}
}
