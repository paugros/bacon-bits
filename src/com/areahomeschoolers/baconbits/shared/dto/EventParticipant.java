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
	private int paymentStatusId;
	private int eventId;
	private Integer eventGroupId;
	private Integer eventOrganizationId;
	private Date eventStartDate;
	private Date eventEndDate;
	private String eventTitle;
	private String fieldValues;
	private User user;
	private Date birthDate;
	private double price;
	private double markup;
	private List<EventField> eventFields = new ArrayList<EventField>();
	private String addedByFirstName, addedByLastName;
	private int addedById;
	private int parentId;
	private int eventSeriesId;
	private boolean requiredInSeries;
	private boolean updateAllInSeries;
	private String sex;
	private String registrantEmailAddress;
	private String payPalEmail;

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

	public double getAdjustedPrice() {
		return price + markup;
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

	public Date getEventEndDate() {
		return eventEndDate;
	}

	public List<EventField> getEventFields() {
		return eventFields;
	}

	public Integer getEventGroupId() {
		return eventGroupId;
	}

	public int getEventId() {
		return eventId;
	}

	public Integer getEventOrganizationId() {
		return eventOrganizationId;
	}

	public int getEventRegistrationId() {
		return eventRegistrationId;
	}

	public int getEventSeriesId() {
		return eventSeriesId;
	}

	public Date getEventStartDate() {
		return eventStartDate;
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

	public double getMarkup() {
		return markup;
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

	public int getPaymentStatusId() {
		return paymentStatusId;
	}

	public String getPayPalEmail() {
		return payPalEmail;
	}

	public double getPrice() {
		return price;
	}

	public String getRegistrantEmailAddress() {
		return registrantEmailAddress;
	}

	public boolean getRequiredInSeries() {
		return requiredInSeries;
	}

	public ArrayList<Integer> getSeriesEventIds() {
		return seriesEventIds;
	}

	public String getSex() {
		return sex;
	}

	public String getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public boolean getUpdateAllInSeries() {
		return updateAllInSeries;
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

	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public void setEventFields(List<EventField> eventFields) {
		this.eventFields = eventFields;
	}

	public void setEventGroupId(Integer eventGroupId) {
		this.eventGroupId = eventGroupId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setEventOrganizationId(Integer eventOrganizationId) {
		this.eventOrganizationId = eventOrganizationId;
	}

	public void setEventRegistrationId(int eventRegistrationId) {
		this.eventRegistrationId = eventRegistrationId;
	}

	public void setEventSeriesId(int eventSeriesId) {
		this.eventSeriesId = eventSeriesId;
	}

	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
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

	public void setMarkup(double markup) {
		this.markup = markup;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public void setPaymentStatusId(int paymentStatusId) {
		this.paymentStatusId = paymentStatusId;
	}

	public void setPayPalEmail(String payPalEmail) {
		this.payPalEmail = payPalEmail;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setRegistrantEmailAddress(String registrantEmailAddress) {
		this.registrantEmailAddress = registrantEmailAddress;
	}

	public void setRequiredInSeries(boolean requiredInSeries) {
		this.requiredInSeries = requiredInSeries;
	}

	public void setSeriesEventIds(ArrayList<Integer> seriesEventIds) {
		this.seriesEventIds = seriesEventIds;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setUpdateAllInSeries(boolean updateAllInSeries) {
		this.updateAllInSeries = updateAllInSeries;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
