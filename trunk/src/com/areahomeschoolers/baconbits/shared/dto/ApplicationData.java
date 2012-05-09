package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ApplicationData implements IsSerializable {
	private User currentUser;
	private Data userPreferences;
	private LinkedHashMap<Integer, Date> userActivity;
	private boolean isLive;

	public ApplicationData() {

	}

	public User getCurrentUser() {
		return currentUser;
	}

	public LinkedHashMap<Integer, Date> getUserActivity() {
		return userActivity;
	}

	public Data getUserPreferences() {
		return userPreferences;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void setUserActivity(LinkedHashMap<Integer, Date> userActivity) {
		this.userActivity = userActivity;
	}

	public void setUserPreferences(Data userPreferences) {
		this.userPreferences = userPreferences;
	}

	public void updateUserActivityFromMap(LinkedHashMap<Integer, Date> newActivity) {
		for (int i : newActivity.keySet()) {
			userActivity.remove(i);
			userActivity.put(i, newActivity.get(i));
		}
	}

}
