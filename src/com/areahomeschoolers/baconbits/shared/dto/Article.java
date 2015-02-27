package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.content.document.HasDocuments;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.GroupPolicy;

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
	private int owningOrgId;
	private boolean newsItem;
	private int viewCount;

	// auxiliary
	private String addedByFirstName;
	private String addedByLastName;
	private String visibilityLevel;
	private String groupName;
	private int documentCount;
	private GroupPolicy groupPolicy;
	private int userImageId;
	private int commentCount;
	private Date lastCommentDate;
	private String tags;

	private Integer imageId;
	private Integer smallImageId;
	private String imageExtension;

	public Article() {

	}

	public String getAddedByFirstName() {
		return addedByFirstName;
	}

	@Override
	public int getAddedById() {
		return addedById;
	}

	public String getAddedByLastName() {
		return addedByLastName;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getArticle() {
		return article;
	}

	public int getCommentCount() {
		return commentCount;
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

	public GroupPolicy getGroupPolicy() {
		return groupPolicy;
	}

	public String getImageExtension() {
		return imageExtension;
	}

	public Integer getImageId() {
		if (imageId == null || imageId == 0) {
			return null;
		}
		return imageId;
	}

	public Date getLastCommentDate() {
		return lastCommentDate;
	}

	public boolean getNewsItem() {
		return newsItem;
	}

	@Override
	public int getOwningOrgId() {
		return owningOrgId;
	}

	public Integer getSmallImageId() {
		if (smallImageId == null || smallImageId == 0) {
			return null;
		}
		return smallImageId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTags() {
		return tags;
	}

	public String getTitle() {
		return title;
	}

	public int getUserImageId() {
		return userImageId;
	}

	public int getViewCount() {
		return viewCount;
	}

	public String getVisibilityLevel() {
		return visibilityLevel;
	}

	public int getVisibilityLevelId() {
		return visibilityLevelId;
	}

	@Override
	public boolean hasDocuments() {
		return documentCount > 0;
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

	public void setArticle(String article) {
		this.article = article;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
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

	public void setGroupPolicy(GroupPolicy groupPolicy) {
		this.groupPolicy = groupPolicy;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setLastCommentDate(Date lastCommentDate) {
		this.lastCommentDate = lastCommentDate;
	}

	public void setNewsItem(boolean newsItem) {
		this.newsItem = newsItem;
	}

	@Override
	public void setOwningOrgId(int organizationId) {
		this.owningOrgId = organizationId;
	}

	public void setSmallImageId(Integer smallImageId) {
		this.smallImageId = smallImageId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUserImageId(int userImageId) {
		this.userImageId = userImageId;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public void setVisibilityLevel(String visibilityLevel) {
		this.visibilityLevel = visibilityLevel;
	}

	public void setVisibilityLevelId(int visibilityLevelId) {
		this.visibilityLevelId = visibilityLevelId;
	}

}
