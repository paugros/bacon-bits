package com.areahomeschoolers.baconbits.server.dao;

import com.areahomeschoolers.baconbits.shared.dto.Data;

public interface UserPreferenceDao {
	Data getPreferencesByGroupName(int userId, String group);

	void set(int userId, String key, String value);

	Data setPreferencesByGroupPrefix(int userId, String group, Data prefs);
}
