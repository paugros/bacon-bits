package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
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

	public ServerResponseData<User> save(User user);

	public ServerResponseData<String> validatePassword(String password);
}
