package com.areahomeschoolers.baconbits.client.rpc.service;

import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/login")
public interface LoginService extends RemoteService {
	public ApplicationData getApplicationData();

	public boolean login(String username, String password);

	public ApplicationData loginAndGetApplicationData(String username, String password);

	public ApplicationData loginForPasswordReset(int id, String digest);

	public void logout();

	public boolean sendPasswordResetEmail(String username);
}
