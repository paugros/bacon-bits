package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	public void getApplicationData(AsyncCallback<ApplicationData> callback);

	public void login(String username, String password, AsyncCallback<Boolean> callback);

	public void loginAndGetApplicationData(String username, String password, AsyncCallback<ApplicationData> callback);

	public void loginForPasswordReset(int id, String digest, AsyncCallback<ApplicationData> callback);

	public void logout(AsyncCallback<Void> callback);

	public void sendPasswordResetEmail(String username, AsyncCallback<Boolean> callback);
}
