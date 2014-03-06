package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

public class PollUpdateData extends EntityDto<PollUpdateData> {
	private static final long serialVersionUID = -4524434771240327430L;
	private int organizationId;
	private ArrayList<HistoryEntry> historyUpdates;

	public PollUpdateData() {
		super();
	}

	public PollUpdateData(int organizationId) {
		this();
		this.organizationId = organizationId;
		historyUpdates = new ArrayList<HistoryEntry>();
	}

	public void addHistoryUpdate(String title, String url) {
		historyUpdates.add(new HistoryEntry(title, url));
	}

	public void clearHistoryUpdates() {
		historyUpdates.clear();
	}

	public ArrayList<HistoryEntry> getHistoryUpdates() {
		return historyUpdates;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public boolean hasHistoryUpdates() {
		return historyUpdates != null && !historyUpdates.isEmpty();
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

}
