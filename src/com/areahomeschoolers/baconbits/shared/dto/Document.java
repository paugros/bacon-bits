package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public final class Document extends EntityDto<Document> {
	public enum DocumentLinkType implements IsSerializable {
		ARTICLE("articles"), EVENT("events");

		private String table;
		private static final Map<String, DocumentLinkType> lookup = new HashMap<String, DocumentLinkType>();

		static {
			for (DocumentLinkType s : EnumSet.allOf(DocumentLinkType.class)) {
				lookup.put(s.getTable(), s);
			}
		}

		public static DocumentLinkType getByTableName(String tableName) {
			return lookup.get(tableName);
		}

		private DocumentLinkType(String table) {
			this.table = table;
		}

		public String getTable() {
			return table;
		}
	}

	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String description;
	private String fileName;
	private String fileType;
	private String fileExtension;
	private String stringData;
	private byte[] data;
	private Integer addedById;
	private Date startDate, endDate, addedDate;
	private int fileSize;
	// one-time use for linking
	private int linkId;
	private DocumentLinkType linkType;

	public Integer getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public byte[] getData() {
		return data;
	}

	public String getDescription() {
		return description;
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

	public int getFileSize() {
		return fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public int getLinkId() {
		return linkId;
	}

	public DocumentLinkType getLinkType() {
		return linkType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getStringData() {
		return stringData;
	}

	public void setAddedById(Integer addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setDescription(String bannerText) {
		this.description = bannerText;
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

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}

	public void setLinkType(DocumentLinkType linkType) {
		this.linkType = linkType;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStringData(String document) {
		this.stringData = document;
	}
}
