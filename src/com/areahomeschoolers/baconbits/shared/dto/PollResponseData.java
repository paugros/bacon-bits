package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PollResponseData implements IsSerializable {
	private LinkedHashMap<Integer, Date> userActivity;
	private ArrayList<HistoryEntry> historyItems;
	private Data unpaidBalance;

	public PollResponseData() {

	}

	public ArrayList<HistoryEntry> getHistoryItems() {
		return historyItems;
	}

	public Data getUnpaidBalance() {
		return unpaidBalance;
	}

	public LinkedHashMap<Integer, Date> getUserActivity() {
		return userActivity;
	}

	public void setHistoryItems(ArrayList<HistoryEntry> historyItems) {
		this.historyItems = historyItems;
	}

	public void setUnpaidBalance(Data unpaidBalance) {
		this.unpaidBalance = unpaidBalance;
	}

	public void setUserActivity(LinkedHashMap<Integer, Date> userActivity) {
		this.userActivity = userActivity;
	}
}
