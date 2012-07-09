package com.areahomeschoolers.baconbits.shared.dto;

public class EventField extends EntityDto<EventField> {
	private int typeId;
	private String type;
	private int eventId;
	private int eventAgeGroupId;
	private String name;
	private boolean required;
	private String options;
	private String value;
	private int valueId;
	private int eventRegistrationId;

	private static final long serialVersionUID = 1L;

	public EventField() {

	}

	public int getEventAgeGroupId() {
		return eventAgeGroupId;
	}

	public int getEventId() {
		return eventId;
	}

	public int getEventRegistrationId() {
		return eventRegistrationId;
	}

	public String getName() {
		return name;
	}

	public String getOptions() {
		return options;
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

	public void setEventAgeGroupId(int eventAgeGroupId) {
		this.eventAgeGroupId = eventAgeGroupId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setEventRegistrationId(int eventRegistrationId) {
		this.eventRegistrationId = eventRegistrationId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOptions(String options) {
		this.options = options;
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
