package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class EventRegistrationParticipant extends EntityDto<EventRegistrationParticipant> {
	private static final long serialVersionUID = 1L;
	private String firstName, lastName;
	private int eventRegistrationId;
	private Integer ageGroupId;
	private boolean attended;
	private boolean canceled;
	private int userId;
	private Date addedDate;

	// aux
	private User user;
	private Date birthDate;
	private double price;
	private boolean waiting;
	private List<EventField> eventFields = new ArrayList<EventField>();
	private String parentFirstName, parentLastName;
	private int parentId;

	public EventRegistrationParticipant() {

	}

	public Date getAddedDate() {
		return addedDate;
	}

	public Integer getAgeGroupId() {
		if (ageGroupId == null || ageGroupId == 0) {
			return null;
		}
		return ageGroupId;
	}

	public boolean getAttended() {
		return attended;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public boolean getCanceled() {
		return canceled;
	}

	public List<EventField> getEventFields() {
		return eventFields;
	}

	public int getEventRegistrationId() {
		return eventRegistrationId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getParentFirstName() {
		return parentFirstName;
	}

	public int getParentId() {
		return parentId;
	}

	public String getParentLastName() {
		return parentLastName;
	}

	public double getPrice() {
		return price;
	}

	public User getUser() {
		return user;
	}

	public int getUserId() {
		return userId;
	}

	public boolean getWaiting() {
		return waiting;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setAgeGroupId(Integer ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public void setEventFields(List<EventField> eventFields) {
		this.eventFields = eventFields;
	}

	public void setEventRegistrationId(int eventRegistrationId) {
		this.eventRegistrationId = eventRegistrationId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setParentFirstName(String parentFirstName) {
		this.parentFirstName = parentFirstName;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public void setParentLastName(String parentLastName) {
		this.parentLastName = parentLastName;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

}
