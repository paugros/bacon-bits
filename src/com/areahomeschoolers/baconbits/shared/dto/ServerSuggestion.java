package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public final class ServerSuggestion extends EntityDto<ServerSuggestion> implements Suggestion {
	private static final long serialVersionUID = 1L;
	private String displayString;
	private String entityType;
	private String stringId;

	public ServerSuggestion() {

	}

	public ServerSuggestion(String displayString) {
		this.displayString = displayString;
	}

	public ServerSuggestion(String displayString, int id) {
		this(displayString);
		setId(id);
	}

	@Override
	public String getDisplayString() {
		return displayString;
	}

	public String getEntityType() {
		return entityType;
	}

	@Override
	public String getReplacementString() {
		return displayString;
	}

	public String getStringId() {
		return stringId;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public void setStringId(String stringId) {
		this.stringId = stringId;
	}
}