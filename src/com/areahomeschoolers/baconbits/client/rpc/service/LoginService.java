package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/login")
public interface LoginService extends RemoteService {
	ApplicationData getApplicationData();

	boolean login(String username, String password);

	ApplicationData loginAndGetApplicationData(String username, String password);

	ApplicationData loginForPasswordReset(int id, String digest);

	void logout();

	boolean sendPasswordResetEmail(String username);
}
