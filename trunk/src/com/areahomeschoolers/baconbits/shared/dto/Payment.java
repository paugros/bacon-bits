package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Payment extends EntityDto<Payment> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String payKey;
	private int userId;
	private Date paymentDate;
	private double amount;
	private int statusId;
	private double paymentFee;
	private String transactionId;
	private Date ipnDate;
	private String rawData;
	private int paymentTypeId;

	// aux
	private String paymentType;
	private String userFullName;
	private String status;
	private String returnPage;
	private String memo;
	private PaypalData paypalData;

	public Payment() {

	}

	public double getAmount() {
		return amount;
	}

	public Date getIpnDate() {
		return ipnDate;
	}

	public String getMemo() {
		return memo;
	}

	public String getPayKey() {
		return payKey;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public double getPaymentFee() {
		return paymentFee;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public int getPaymentTypeId() {
		return paymentTypeId;
	}

	public PaypalData getPaypalData() {
		return paypalData;
	}

	public String getRawData() {
		return rawData;
	}

	public String getReturnPage() {
		return returnPage;
	}

	public String getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public int getUserId() {
		return userId;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setIpnDate(Date ipnDate) {
		this.ipnDate = ipnDate;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public void setPaymentFee(double paymentFee) {
		this.paymentFee = paymentFee;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public void setPaymentTypeId(int paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public void setPaypalData(PaypalData paypalData) {
		this.paypalData = paypalData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public void setReturnPage(String returnPage) {
		this.returnPage = returnPage;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
