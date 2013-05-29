package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserPageData implements IsSerializable {
	private User user;
	private ArrayList<Tag> interests;
	private int firstBirthYear;

	public UserPageData() {

	}

	public int getFirstBirthYear() {
		return firstBirthYear;
	}

	public ArrayList<Tag> getInterests() {
		return interests;
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

	public void setUser(User user) {
		this.user = user;
	}

}
