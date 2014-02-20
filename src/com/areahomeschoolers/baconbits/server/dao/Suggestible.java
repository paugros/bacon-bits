package com.areahomeschoolers.baconbits.server.dao;

import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;

public interface Suggestible {
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options);
}
