package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public final class ServerSuggestion extends EntityDto<ServerSuggestion> implements Suggestion {
	private static final long serialVersionUID = 1L;
	private String displayString;
	private String entityType; // Added to distinguish between the Customers and Accounts for the Customer/Account search

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

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}