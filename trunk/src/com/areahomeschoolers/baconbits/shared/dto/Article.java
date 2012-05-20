package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public final class Article extends EntityDto<Article> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private String article;
	private int addedById;
	private Date startDate, endDate, addedDate;
	private int groupId;

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getArticle() {
		return article;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getGroupId() {
		return groupId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getTitle() {
		return title;
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

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
