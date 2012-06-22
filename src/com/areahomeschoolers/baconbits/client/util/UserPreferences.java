package com.areahomeschoolers.baconbits.client.util;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Data;

public abstract class UserPreferences {
	private static UserPreferenceServiceAsync userPreferenceService = (UserPreferenceServiceAsync) ServiceCache.getService(UserPreferenceService.class);

	public static void save(String key, String value) {
		if (Application.getUserPreferences() == null) {
			return;
		}

		Application.getUserPreferences().put(key, value);
		userPreferenceService.set(Application.getCurrentUser().getId(), key, value, new Callback<Void>(false) {
			@Override
			protected void doOnSuccess(Void result) {
			}
		});
	}

	/**
	 * This will replace all existing prefs with this group name prefix with the content of userPrefs. A null group name will replace all of the current user's
	 * preferences with userPrefs.
	 * 
	 * @param groupPrefix
	 * @param userPrefs
	 */
	public static void saveGroup(String groupPrefix, Data userPrefs) {
		userPreferenceService.setPreferencesByGroupPrefix(Application.getCurrentUser().getId(), groupPrefix, userPrefs, new Callback<Data>(false) {
			@Override
			protected void doOnSuccess(Data result) {
				Application.getApplicationData().setUserPreferences(result);
			}
		});
	}

	private UserPreferences() {

	}
}
