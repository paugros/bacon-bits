package com.areahomeschoolers.baconbits.shared.dto;

public final class ArticleType extends EntityDto<ArticleType> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}