package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EventPageData implements IsSerializable {
	private Event event;
	private ArrayList<Data> categories;

	public EventPageData() {

	}

	public ArrayList<Data> getCategories() {
		return categories;
	}

	public Event getEvent() {
		return event;
	}

	public void setCategories(ArrayList<Data> categories) {
		this.categories = categories;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
}
