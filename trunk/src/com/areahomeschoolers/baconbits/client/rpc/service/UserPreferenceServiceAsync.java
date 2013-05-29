package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserPreferenceServiceAsync {
	public void getPreferencesByGroupName(int userId, String group, AsyncCallback<Data> callback);

	public void set(int userId, String key, String value, AsyncCallback<Void> callback);

	public void setPreferencesByGroupPrefix(int userId, String group, Data prefs, AsyncCallback<Data> callback);
}
