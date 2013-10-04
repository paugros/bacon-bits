package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PollResponseData implements IsSerializable {
	private LinkedHashMap<Integer, Date> userActivity;
	private ArrayList<HistoryEntry> historyItems;

	public PollResponseData() {

	}

	public ArrayList<HistoryEntry> getHistoryItems() {
		return historyItems;
	}

	public LinkedHashMap<Integer, Date> getUserActivity() {
		return userActivity;
	}

	public void setHistoryItems(ArrayList<HistoryEntry> historyItems) {
		this.historyItems = historyItems;
	}

	public void setUserActivity(LinkedHashMap<Integer, Date> userActivity) {
		this.userActivity = userActivity;
	}
}
