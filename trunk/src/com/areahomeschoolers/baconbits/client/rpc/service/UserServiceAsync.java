package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {

	void getById(int userId, AsyncCallback<User> callback);

	void getUserByUsername(String username, AsyncCallback<User> callback);

	void list(AsyncCallback<ArrayList<User>> callback);

	void save(User user, AsyncCallback<User> callback);
}
