package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerSuggestionData implements IsSerializable {
	private ArrayList<ServerSuggestion> suggestions = new ArrayList<ServerSuggestion>();
	private Integer totalMatches; // optional

	public ServerSuggestionData() {
	}

	public ArrayList<ServerSuggestion> getSuggestions() {
		return suggestions;
	}

	public Integer getTotalMatches() {
		return totalMatches;
	}

	public void setSuggestions(ArrayList<ServerSuggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public void setTotalMatches(Integer totalMatches) {
		this.totalMatches = totalMatches;
	}

}
