package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/userPreference")
public interface UserPreferenceService extends RemoteService {
	Data getPreferencesByGroupName(int userId, String group);

	void set(int userId, String key, String value);

	Data setPreferencesByGroupPrefix(int userId, String group, Data prefs);
}
