package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public class Adjustment extends EntityDto<Adjustment> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int adjustmentTypeId;
	private int userId;
	private int linkId;
	private double amount;
	private int statusId;
	private Date addedDate;

	// aux
	private String adjustmentType;
	private String userFullName;

	private String status;

	public Adjustment() {

	}

	public Date getAddedDate() {
		return addedDate;
	}

	public int getAdjustmentTypeId() {
		return adjustmentTypeId;
	}

	public double getAmount() {
		return amount;
	}

	public int getLinkId() {
		return linkId;
	}

	public String getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public int getUserId() {
		return userId;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public void setAdjustmentTypeId(int adjustmentTypeId) {
		this.adjustmentTypeId = adjustmentTypeId;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setLinkId(int sourceLinkId) {
		this.linkId = sourceLinkId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
