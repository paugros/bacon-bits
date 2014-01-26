package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SuggestServiceAsync {
	public void getSuggestions(String token, ArrayList<String> suggestTypes, int limit, Data options, AsyncCallback<ArrayList<ServerSuggestion>> async);
}
