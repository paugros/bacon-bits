package com.areahomeschoolers.baconbits.shared.dto;

public final class HomePageModules extends EntityDto<HomePageModules> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String name;

	public String getType() {
		return name;
	}

	public void setType(String type) {
		this.name = type;
	}

}