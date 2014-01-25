package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

public class NewsBulletinComment extends EntityDto<NewsBulletinComment> {
	private static final long serialVersionUID = 1L;
	private String comment, addedBy;
	private Date addedDate;
	private int articleId;
	private int userId;
	private int imageDocumentId;

	public NewsBulletinComment() {
	}

	public void addedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public int getUserId() {
		return userId;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getComment() {
		return comment;
	}

	public int getImageDocumentId() {
		return imageDocumentId;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setImageDocumentId(int imageDocumentId) {
		this.imageDocumentId = imageDocumentId;
	}

	public void setArticleId(int newsArticleId) {
		this.articleId = newsArticleId;
	}

}
