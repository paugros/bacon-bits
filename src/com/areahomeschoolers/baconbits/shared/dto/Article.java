package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.content.document.HasDocuments;

public final class Article extends EntityDto<Article> implements HasDocuments, HasGroupOwnership {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private String article;
	private int addedById;
	private Date startDate, endDate, addedDate;
	private Integer groupId;
	private int visibilityLevelId;

	// auxiliary
	private String visibilityLevel;
	private String groupName;
	private int documentCount;
	private int tagCount;
	private Integer organizationId;

	public Article() {

	}

	public String getVisibilityLevel() {
		return visibilityLevel;
	}

	public int getVisibilityLevelId() {
		return visibilityLevelId;
	}

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getArticle() {
		return article;
	}

	@Override
	public int getDocumentCount() {
		return documentCount;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.ARTICLE;
	}

	@Override
	public Integer getGroupId() {
		if (groupId == null || groupId == 0) {
			return null;
		}
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	@Override
	public Integer getOrganizationId() {
		if (organizationId == null || organizationId == 0) {
			return null;
		}
		return organizationId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public int getTagCount() {
		return tagCount;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public boolean hasDocuments() {
		return documentCount > 0;
	}

	public boolean hasTags() {
		return tagCount > 0;
	}

	public void setVisibilityLevel(String visibilityLevel) {
		this.visibilityLevel = visibilityLevel;
	}

	public void setVisibilityLevelId(int visibilityLevelId) {
		this.visibilityLevelId = visibilityLevelId;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
