package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/user")
public interface UserService extends RemoteService {
	public User getById(int userId);

	public UserPageData getPageData(int userId);

	public User getUserByUsername(String username);

	public ArrayList<User> list(ArgMap<UserArg> args);

	public ArrayList<UserGroup> listGroups(ArgMap<UserArg> args);

	public ServerResponseData<User> save(User user);

	public UserGroup saveUserGroup(UserGroup group);

	public ServerResponseData<String> validatePassword(String password);

	void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add);

	void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add);

	void updateUserGroupRelation(User u, UserGroup g, boolean add);
}
