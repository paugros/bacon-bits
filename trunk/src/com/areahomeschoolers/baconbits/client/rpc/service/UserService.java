package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Email;
import com.areahomeschoolers.baconbits.shared.dto.GroupData;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
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

	public ArrayList<MainMenuItem> getMenuItems(ArgMap<UserArg> args);

	public UserPageData getPageData(int userId);

	public PollResponseData getPollData();

	public User getUserByUsername(String username);

	public ArrayList<User> list(ArgMap<UserArg> args);

	public ArrayList<UserGroup> listGroups(ArgMap<UserGroupArg> args);

	public HashMap<Integer, GroupData> refreshSecurityGroups();

	public ServerResponseData<User> save(User user);

	public MainMenuItem saveMenuItem(MainMenuItem item);

	public PrivacyPreference savePrivacyPreference(PrivacyPreference privacyPreference);

	public UserGroup saveUserGroup(UserGroup group);

	public void sendEmail(Email email);

	public void switchToUser(int userId);

	public void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add);

	public void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add);

	public void updateUserGroupRelation(User u, UserGroup g, boolean add);

	public ServerResponseData<String> validatePassword(String password);
}
