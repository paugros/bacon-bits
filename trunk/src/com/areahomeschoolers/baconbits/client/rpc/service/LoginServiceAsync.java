package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void getApplicationData(AsyncCallback<ApplicationData> callback);

	void login(String username, String password, AsyncCallback<Boolean> callback);

	void loginAndGetApplicationData(String username, String password, AsyncCallback<ApplicationData> callback);

	void logout(AsyncCallback<Void> callback);
}
