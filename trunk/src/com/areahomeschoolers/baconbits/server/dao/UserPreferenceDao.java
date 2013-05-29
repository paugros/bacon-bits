package com.areahomeschoolers.baconbits.server.dao;

import com.areahomeschoolers.baconbits.shared.dto.Data;

public interface UserPreferenceDao {
	public Data getPreferencesByGroupName(int userId, String group);

	public void set(int userId, String key, String value);

	public Data setPreferencesByGroupPrefix(int userId, String group, Data prefs);
}
