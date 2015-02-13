package com.areahomeschoolers.baconbits.client;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * GWT Module EntryPoint that authenticates user and then loads Application in another code fragment
 */
public class BaconBitsEntryPoint implements EntryPoint {
	private final LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

	@Override
	public void onModuleLoad() {
		// constant contact fuckup
		if (Window.Location.getHref().contains("?utm_source=")) {
			String newUrl = Window.Location.getHref().replaceAll(".utm_source=.*", "");
			Window.Location.replace(newUrl);
		}
		System.out.println(Window.Location.getHref());
		loginService.getApplicationData(new AsyncCallback<ApplicationData>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(final ApplicationData ap) {
				initHistoryToken();
				String digest = HistoryToken.getElement("rr");
				int userId = 0;
				try {
					userId = Integer.parseInt(HistoryToken.getElement("uu"));
				} catch (NumberFormatException e) {
				}

				if (digest != null && userId > 0) {
					loginService.loginForPasswordReset(userId, digest, new AsyncCallback<ApplicationData>() {
						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(ApplicationData ap) {
							initApplication(ap);
							HistoryToken.set(PageUrl.home(), false);
						}
					});
					return;
				}

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

	private void initHistoryToken() {
		HistoryToken.createMapFromToken(History.getToken());
	}

}
