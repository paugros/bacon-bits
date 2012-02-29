package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Document extends EntityDto<Document> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String description;
	private String fileName;
	private String fileType;
	private String fileExtension;
	private String document;
	private int articleId;
	private int addedById;
	private Date startDate, endDate, addedDate;

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getBannerText() {
		return description;
	}

	public String getDocument() {
		return document;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public int getLocationId() {
		return articleId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setBannerText(String bannerText) {
		this.description = bannerText;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setLocationId(int locationId) {
		this.articleId = locationId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
