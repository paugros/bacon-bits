package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResourcePageData implements IsSerializable {
	private Resource resource;
	private ArrayList<Data> addressScopes;

	public ArrayList<Data> getAddressScopes() {
		return addressScopes;
	}

	public Resource getResource() {
		return resource;
	}

	public void setAddressScopes(ArrayList<Data> addressScopes) {
		this.addressScopes = addressScopes;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
