package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PollResponseData implements IsSerializable {
	private LinkedHashMap<Integer, Date> userActivity;

	public PollResponseData() {

	}

	public LinkedHashMap<Integer, Date> getUserActivity() {
		return userActivity;
	}

	public void setUserActivity(LinkedHashMap<Integer, Date> userActivity) {
		this.userActivity = userActivity;
	}
}
