package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResourcePageData implements IsSerializable {
	private Resource resource;
	private ArrayList<Data> owners;

	public ArrayList<Data> getOwners() {
		return owners;
	}

	public Resource getResource() {
		return resource;
	}

	public void setOwners(ArrayList<Data> owners) {
		this.owners = owners;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
