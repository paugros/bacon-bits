package com.areahomeschoolers.baconbits.shared.dto;

public final class TagMapping extends EntityDto<TagMapping> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int tagId;
	private int articleId;

	public int getArticleId() {
		return articleId;
	}

	public int getTagId() {
		return tagId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

}