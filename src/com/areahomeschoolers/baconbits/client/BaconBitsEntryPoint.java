package com.areahomeschoolers.baconbits.client;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * GWT Module EntryPoint that authenticates user and then loads Application in another code fragment
 */
public class BaconBitsEntryPoint implements EntryPoint {
	private final LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

	@Override
	public void onModuleLoad() {
		loginService.getApplicationData(new AsyncCallback<ApplicationData>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(final ApplicationData ap) {
				initApplication(ap);
			}
		});

	}

	private void initApplication(final ApplicationData ap) {
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess() {
				new Application(ap);
			}
		});

	}

	// private void showLoginDialog() {
	// LoginDialog ld = new LoginDialog(loginService);
	// ld.setLoginHandler(new LoginHandler() {
	// @Override
	// public void onLogin(ApplicationData ap) {
	// initApplication(ap);
	// }
	// });
	// ld.center();
	// }
}
