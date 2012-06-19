package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserPageData implements IsSerializable {
	private User user;
	private ArrayList<Data> userTypes;

	public UserPageData() {

	}

	public ArrayList<Data> getUserTypes() {
		return userTypes;
	}

	public User getUser() {
		return user;
	}

	public void setUserTypes(ArrayList<Data> types) {
		this.userTypes = types;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
