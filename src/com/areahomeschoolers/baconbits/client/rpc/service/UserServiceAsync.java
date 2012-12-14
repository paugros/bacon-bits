package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Email;
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

	void saveUserGroup(UserGroup group, AsyncCallback<UserGroup> callback);

	void sendEmail(Email email, AsyncCallback<Void> callback);

	void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add, AsyncCallback<Void> callback);

	void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add, AsyncCallback<Void> callback);

	void updateUserGroupRelation(User u, UserGroup g, boolean add, AsyncCallback<Void> callback);

	void validatePassword(String password, AsyncCallback<ServerResponseData<String>> callback);
}
