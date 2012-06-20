package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

public interface UserDao {
	public User getById(int userId);

	public UserPageData getPageData(int userId);

	public User getUserByUsername(String username);

	public ArrayList<User> list(ArgMap<UserArg> args);

	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public ServerResponseData<User> save(User user);

	public boolean sendPasswordResetEmail(String username);

	public User setPasswordFromDigest(int id, String digest);

	public List<String> validatePassword(String password);
}
