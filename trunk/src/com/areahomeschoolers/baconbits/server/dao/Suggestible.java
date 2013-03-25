package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;

public interface Suggestible {
	public ArrayList<ServerSuggestion> getSuggestions(String token, int limit, Data options);
}
