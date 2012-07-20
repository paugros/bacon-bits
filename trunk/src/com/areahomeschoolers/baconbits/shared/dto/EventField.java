package com.areahomeschoolers.baconbits.shared.dto;

public class EventField extends EntityDto<EventField> {
	private int typeId;
	private String type;
	private Integer eventAgeGroupId;
	private String name;
	private boolean required;
	private String options;
	private String value;
	private int valueId;
	private Integer eventId;
	private Integer participantId;

	private static final long serialVersionUID = 1L;

	public EventField() {

	}

	public Integer getEventAgeGroupId() {
		if (eventAgeGroupId == 0) {
			return null;
		}
		return eventAgeGroupId;
	}

	public Integer getEventId() {
		if (eventId == 0) {
			return null;
		}
		return eventId;
	}

	public String getName() {
		return name;
	}

	public String getOptions() {
		return options;
	}

	public Integer getParticipantId() {
		if (participantId == 0) {
			return null;
		}
		return participantId;
	}

	public boolean getRequired() {
		return required;
	}

	public String getType() {
		return type;
	}

	public int getTypeId() {
		return typeId;
	}

	public String getValue() {
		return value;
	}

	public int getValueId() {
		return valueId;
	}

	public boolean hasValue() {
		return valueId > 0;
	}

	public void setEventAgeGroupId(int eventAgeGroupId) {
		this.eventAgeGroupId = eventAgeGroupId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public void setParticipantId(Integer participantId) {
		this.participantId = participantId;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

}
