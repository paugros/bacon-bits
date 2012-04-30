package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserPreferenceServiceAsync {

	void getPreferencesByGroupName(int userId, String group, AsyncCallback<Data> callback);

	void set(int userId, String key, String value, AsyncCallback<Void> callback);

	void setPreferencesByGroupPrefix(int userId, String group, Data prefs, AsyncCallback<Data> callback);

}
