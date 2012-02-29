package com.areahomeschoolers.baconbits.shared.dto;

public final class Tags extends EntityDto<Tags> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String tag;

	public String getType() {
		return tag;
	}

	public void setType(String type) {
		this.tag = type;
	}

}