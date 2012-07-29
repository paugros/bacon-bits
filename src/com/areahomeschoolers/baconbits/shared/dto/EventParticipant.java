package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class EventParticipant extends EntityDto<EventParticipant> {
	private static final long serialVersionUID = 1L;
	private String firstName, lastName;
	private int eventRegistrationId;
	private Integer ageGroupId;
	private int userId;
	private Date addedDate;
	private int statusId;

	// aux
	private String status;
	private int eventId;
	private String eventTitle;
	private String fieldValues;
	private User user;
	private Date birthDate;
	private double price;
	private List<EventField> eventFields = new ArrayList<EventField>();
	private String parentFirstName, parentLastName;
	private int parentId;

	public EventParticipant() {

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

	public Date getBirthDate() {
		return birthDate;
	}

	public List<EventField> getEventFields() {
		return eventFields;
	}

	public int getEventId() {
		return eventId;
	}

	public int getEventRegistrationId() {
		return eventRegistrationId;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public String getFieldValues() {
		return fieldValues;
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

	public String getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public User getUser() {
		return user;
	}

	public int getUserId() {
		return userId;
	}

	public boolean hasAttended() {
		return statusId == 4;
	}

	public boolean isCanceled() {
		return statusId == 5;
	}

	public boolean isWaiting() {
		return statusId == 3;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setAgeGroupId(Integer ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setEventFields(List<EventField> eventFields) {
		this.eventFields = eventFields;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setEventRegistrationId(int eventRegistrationId) {
		this.eventRegistrationId = eventRegistrationId;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public void setFieldValues(String fieldValues) {
		this.fieldValues = fieldValues;
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

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
