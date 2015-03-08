package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResourcePageData implements IsSerializable {
	private Resource resource;
	private ArrayList<Data> owners;
	private ArrayList<Data> events;

	public ArrayList<Data> getEvents() {
		return events;
	}

	public ArrayList<Data> getOwners() {
		return owners;
	}

	public Resource getResource() {
		return resource;
	}

	public void setEvents(ArrayList<Data> events) {
		this.events = events;
	}

	public void setOwners(ArrayList<Data> owners) {
		this.owners = owners;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
