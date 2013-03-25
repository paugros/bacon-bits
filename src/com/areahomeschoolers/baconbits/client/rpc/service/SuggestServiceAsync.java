package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SuggestServiceAsync {
	void getSuggestions(String token, String suggestType, int limit, Data options, AsyncCallback<ArrayList<ServerSuggestion>> async);
}
