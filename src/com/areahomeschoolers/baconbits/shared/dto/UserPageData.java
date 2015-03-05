package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserPageData implements IsSerializable {
	private User user;
	private ArrayList<Tag> interests;
	private int firstBirthYear;
	private ArrayList<Data> resources;

	public UserPageData() {

	}

	public int getFirstBirthYear() {
		return firstBirthYear;
	}

	public ArrayList<Tag> getInterests() {
		return interests;
	}

	public ArrayList<Data> getResources() {
		return resources;
	}

	public User getUser() {
		return user;
	}

	public void setFirstBirthYear(int firstBirthYear) {
		this.firstBirthYear = firstBirthYear;
	}

	public void setInterests(ArrayList<Tag> interests) {
		this.interests = interests;
	}

	public void setResources(ArrayList<Data> resources) {
		this.resources = resources;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
