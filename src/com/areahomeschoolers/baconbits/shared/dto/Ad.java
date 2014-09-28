package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.Common;

public final class Ad extends EntityDto<Ad> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private int addedById;
	private Date startDate, endDate, addedDate;
	private int owningOrgId;

	// auxiliary
	private String addedByFirstName;
	private String addedByLastName;
	private int documentId;
	private int clickCount;
	private Date lastClickDate;

	public Ad() {

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

	public int getClickCount() {
		return clickCount;
	}

	public int getDocumentId() {
		return documentId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getLastClickDate() {
		return lastClickDate;
	}

	public int getOwningOrgId() {
		return owningOrgId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTitle() {
		return title;
	}

	public boolean isActive() {
		return Common.isActive(new Date(), endDate);
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

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setLastClickDate(Date lastClickDate) {
		this.lastClickDate = lastClickDate;
	}

	public void setOwningOrgId(int organizationId) {
		this.owningOrgId = organizationId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
