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
	private Integer paymentId;

	// aux
	private String status;
	private int eventId;
	private Date eventDate;
	private String eventTitle;
	private String fieldValues;
	private User user;
	private Date birthDate;
	private double price;
	private List<EventField> eventFields = new ArrayList<EventField>();
	private String addedByFirstName, addedByLastName;
	private int addedById;
	private int parentId;

	// for bulk registration of a series
	private ArrayList<Integer> seriesEventIds;

	public EventParticipant() {

	}

	public String getAddedByFirstName() {
		return addedByFirstName;
	}

	public int getAddedById() {
		return addedById;
	}

	public String getAddedByLastName() {
		return addedByLastName;
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

	public Date getEventDate() {
		return eventDate;
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

	public int getParentId() {
		return parentId;
	}

	public Integer getPaymentId() {
		if (paymentId == null || paymentId == 0) {
			return null;
		}
		return paymentId;
	}

	public double getPrice() {
		return price;
	}

	public ArrayList<Integer> getSeriesEventIds() {
		return seriesEventIds;
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

	public void setAddedByFirstName(String addedByFirstName) {
		this.addedByFirstName = addedByFirstName;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedByLastName(String addedByLastName) {
		this.addedByLastName = addedByLastName;
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

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
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

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setSeriesEventIds(ArrayList<Integer> seriesEventIds) {
		this.seriesEventIds = seriesEventIds;
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
