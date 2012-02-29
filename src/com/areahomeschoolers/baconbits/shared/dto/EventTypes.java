package com.areahomeschoolers.baconbits.shared.dto;

public final class EventTypes extends EntityDto<EventTypes> {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String eventType;

	public String getType() {
		return eventType;
	}

	public void setType(String type) {
		this.eventType = type;
	}

}