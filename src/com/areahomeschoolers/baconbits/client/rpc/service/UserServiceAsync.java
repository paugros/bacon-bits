package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Email;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {
	public void getById(int userId, AsyncCallback<User> callback);

	public void getPageData(int userId, AsyncCallback<UserPageData> callback);

	public void getUserByUsername(String username, AsyncCallback<User> callback);

	public void list(ArgMap<UserArg> args, AsyncCallback<ArrayList<User>> callback);

	public void listGroups(ArgMap<UserGroupArg> args, AsyncCallback<ArrayList<UserGroup>> callback);

	public void refreshSecurityGroups(AsyncCallback<HashMap<Integer, Boolean>> callback);

	public void save(User user, AsyncCallback<ServerResponseData<User>> callback);

	public void saveUserGroup(UserGroup group, AsyncCallback<UserGroup> callback);

	public void sendEmail(Email email, AsyncCallback<Void> callback);

	public void switchToUser(int userId, AsyncCallback<Void> callback);

	public void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add, AsyncCallback<Void> callback);

	public void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add, AsyncCallback<Void> callback);

	public void updateUserGroupRelation(User u, UserGroup g, boolean add, AsyncCallback<Void> callback);

	public void validatePassword(String password, AsyncCallback<ServerResponseData<String>> callback);
}
