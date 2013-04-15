package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

public interface UserDao {
	public User getById(int userId);

	public UserPageData getPageData(int userId);

	public User getUserByUsername(String username);

	public ArrayList<User> list(ArgMap<UserArg> args);

	public ArrayList<UserGroup> listGroups(ArgMap<UserArg> args);

	public void recordLogin(String username);

	public HashMap<Integer, Boolean> refreshSecurityGroups();

	public ServerResponseData<User> save(User user);

	@PreAuthorize("hasRole('SYSTEM_ADMINISTRATORS')")
	public UserGroup saveUserGroup(UserGroup group);

	public boolean sendPasswordResetEmail(String username);

	public User setPasswordFromDigest(int id, String digest);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void updateUserGroupRelation(User u, UserGroup g, boolean add);

	public List<String> validatePassword(String password);
}
