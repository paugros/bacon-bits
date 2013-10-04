package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

public class PollUpdateData extends EntityDto<PollUpdateData> {
	private static final long serialVersionUID = -4524434771240327430L;
	private Integer userId;
	private ArrayList<HistoryEntry> historyUpdates;

	public PollUpdateData() {
		super();
	}

	public PollUpdateData(Integer userId) {
		this();
		this.userId = userId;
		historyUpdates = new ArrayList<HistoryEntry>();
	}

	public void addHistoryUpdate(String title, String url) {
		historyUpdates.add(new HistoryEntry(title, url));
	}

	public ArrayList<HistoryEntry> getHistoryUpdates() {
		return historyUpdates;
	}

	public Integer getUserId() {
		return userId;
	}

	public boolean hasHistoryUpdates() {
		return historyUpdates != null && !historyUpdates.isEmpty();
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("UserID=").append(userId).append(",\n");
		sb.append("HistoryUpdates {");
		for (HistoryEntry he : historyUpdates) {
			sb.append("\"" + he.getTitle() + "\"(" + he.getUrl() + "),");
		}
		sb.append("},\n");
		sb.append("]");
		return sb.toString();
	}

}
