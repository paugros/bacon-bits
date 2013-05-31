package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.Common;

public class Tag extends EntityDto<Tag> {
	public enum TagMappingType {
		USER, ARTICLE, EVENT, BOOK;
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private Date addedDate;
	private int addedById;

	// mapping
	private int entityId;
	private Date mappingAddedDate;
	private int mappingId;
	private TagMappingType mappingType;

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public int getEntityId() {
		return entityId;
	}

	public Date getMappingAddedDate() {
		return mappingAddedDate;
	}

	public String getMappingColumn() {
		if (getMappingType() == null) {
			return null;
		}
		return getMappingType().toString().toLowerCase() + "Id";
	}

	public int getMappingId() {
		return mappingId;
	}

	public String getMappingTable() {
		if (getMappingType() == null) {
			return null;
		}
		return "tag" + Common.ucWords(getMappingType().toString()) + "Mapping";
	}

	public TagMappingType getMappingType() {
		return mappingType;
	}

	public String getName() {
		return name;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public void setMappingAddedDate(Date mappingAddedDate) {
		this.mappingAddedDate = mappingAddedDate;
	}

	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}

	public void setMappingType(TagMappingType t) {
		mappingType = t;
	}

	public void setName(String name) {
		this.name = name;
	}

}
