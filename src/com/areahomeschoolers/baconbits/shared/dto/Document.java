package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.user.client.rpc.IsSerializable;

public final class Document extends EntityDto<Document> {
	public enum DocumentLinkType implements IsSerializable {
		ARTICLE("article"), EVENT("event"), HTML_IMAGE_INSERT(""), BOOK("book"), PROFILE("users");

		private String entityType;
		private static final Map<String, DocumentLinkType> lookup = new HashMap<String, DocumentLinkType>();

		static {
			for (DocumentLinkType s : EnumSet.allOf(DocumentLinkType.class)) {
				lookup.put(s.getEntityType(), s);
			}
		}

		public static DocumentLinkType getByTableName(String tableName) {
			return lookup.get(tableName);
		}

		private DocumentLinkType() {

		}

		private DocumentLinkType(String entityType) {
			this.entityType = entityType;
		}

		public String getEntityType() {
			return entityType;
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String[] imageExtensions = new String[] { "JPG", "JPEG", "GIF", "PNG" };

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static boolean hasExtension(String fileName, List<String> allowedExtensions) {
		String extension = Common.getFileExtension(fileName);

		if (Common.isNullOrBlank(extension)) {
			return false;
		}

		return isExtension(extension, allowedExtensions);
	}

	public static boolean hasImageExtension(String fileName) {
		return hasExtension(fileName, Common.asList(imageExtensions));
	}

	public static boolean isImageExtension(String extension) {
		return isExtension(extension, Common.asList(imageExtensions));
	}

	private static boolean isExtension(String extension, List<String> allowedExtensions) {
		if (extension == null) {
			return false;
		}
		extension = extension.toUpperCase();

		for (String allowedExt : allowedExtensions) {
			if (extension.equals(allowedExt)) {
				return true;
			}
		}

		return false;
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

	public Document() {

	}

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
		if (data != null) {
			fileSize = data.length;
		}
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
