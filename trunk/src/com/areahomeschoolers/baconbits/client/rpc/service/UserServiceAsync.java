package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {

	void getById(int userId, AsyncCallback<User> callback);

	void getPageData(int userId, AsyncCallback<UserPageData> callback);

	void getUserByUsername(String username, AsyncCallback<User> callback);

	void list(ArgMap<UserArg> args, AsyncCallback<ArrayList<User>> callback);

	void listGroups(ArgMap<UserArg> args, AsyncCallback<ArrayList<UserGroup>> callback);

	void save(User user, AsyncCallback<ServerResponseData<User>> callback);

	void validatePassword(String password, AsyncCallback<ServerResponseData<String>> callback);
}
