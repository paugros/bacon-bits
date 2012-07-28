package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.content.document.HasDocuments;

public final class Article extends EntityDto<Article> implements HasDocuments {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private String article;
	private int addedById;
	private Date startDate, endDate, addedDate;
	private Integer groupId;
	private int accessLevelId;

	// auxiliary
	private String accessLevel;
	private String groupName;
	private int documentCount;

	public Article() {

	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public int getAccessLevelId() {
		return accessLevelId;
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

	public Integer getGroupId() {
		if (groupId == null || groupId == 0) {
			return null;
		}
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public boolean hasDocuments() {
		return documentCount > 0;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	public void setAccessLevelId(int accessLevelId) {
		this.accessLevelId = accessLevelId;
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

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
