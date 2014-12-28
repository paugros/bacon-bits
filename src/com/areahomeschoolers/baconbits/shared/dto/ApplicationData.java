package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ApplicationData implements IsSerializable {
	private User currentUser;
	private UserGroup currentOrg;
	private Data userPreferences;
	private LinkedHashMap<Integer, Date> userActivity;
	private Data unpaidBalance;
	private boolean isLive;
	private int adultBirthYear;
	private ArrayList<Tag> interests = new ArrayList<Tag>();
	private ArrayList<MainMenuItem> dynamicMenuItems = new ArrayList<MainMenuItem>();
	private String currentLocation;
	private double currentLat;
	private double currentLng;
	private int currentRadius;

	public ApplicationData() {

	}

	public int getAdultBirthYear() {
		return adultBirthYear;
	}

	public double getCurrentLat() {
		return currentLat;
	}

	public double getCurrentLng() {
		return currentLng;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public UserGroup getCurrentOrg() {
		return currentOrg;
	}

	public int getCurrentRadius() {
		return currentRadius;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public ArrayList<MainMenuItem> getDynamicMenuItems() {
		return dynamicMenuItems;
	}

	public Data getUnpaidBalance() {
		return unpaidBalance;
	}

	public LinkedHashMap<Integer, Date> getUserActivity() {
		return userActivity;
	}

	public ArrayList<Tag> getUserInterests() {
		return interests;
	}

	public Data getUserPreferences() {
		return userPreferences;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setAdultBirthYear(int adultBirthYear) {
		this.adultBirthYear = adultBirthYear;
	}

	public void setCurrentLat(double currentLat) {
		this.currentLat = currentLat;
	}

	public void setCurrentLng(double currentLng) {
		this.currentLng = currentLng;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public void setCurrentOrg(UserGroup currentOrg) {
		this.currentOrg = currentOrg;
	}

	public void setCurrentRadius(int currentRadius) {
		this.currentRadius = currentRadius;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public void setDynamicMenuItems(ArrayList<MainMenuItem> dynamicMenuItems) {
		this.dynamicMenuItems = dynamicMenuItems;
	}

	public void setInterests(ArrayList<Tag> interests) {
		this.interests = interests;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void setUnpaidBalance(Data unpaidBalance) {
		this.unpaidBalance = unpaidBalance;
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
