package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ApplicationData implements IsSerializable {
	private User currentUser;

	private HashMap<String, String> entityLinkers;
	private ArrayList<Data> userLinks;
	private HashMap<String, Data> notificationEntityTypeColors;
	private LinkedHashMap<Integer, Date> userActivity;
	private Data userPreferences;
	private HashSet<EntityType> entityTypes;

	private boolean isLive;

	public ApplicationData() {

	}

	public User getCurrentUser() {
		return currentUser;
	}

	public HashMap<String, String> getEntityLinkers() {
		return entityLinkers;
	}

	public HashSet<EntityType> getEntityTypes() {
		return entityTypes;
	}

	public HashMap<String, Data> getNotificationEntityTypeColors() {
		return notificationEntityTypeColors;
	}

	public LinkedHashMap<Integer, Date> getUserActivity() {
		return userActivity;
	}

	public ArrayList<Data> getUserLinks() {
		return userLinks;
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

	public void setEntityLinkers(HashMap<String, String> entityLinkers) {
		this.entityLinkers = entityLinkers;
	}

	public void setEntityTypes(HashSet<EntityType> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void setNotificationEntityTypeColors(HashMap<String, Data> notificationEntityTypeColors) {
		this.notificationEntityTypeColors = notificationEntityTypeColors;
	}

	public void setUserActivity(LinkedHashMap<Integer, Date> userActivity) {
		this.userActivity = userActivity;
	}

	public void setUserLinks(ArrayList<Data> userLinks) {
		this.userLinks = userLinks;
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
