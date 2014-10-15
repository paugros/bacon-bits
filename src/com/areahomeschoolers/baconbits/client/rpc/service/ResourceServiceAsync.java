package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ResourceServiceAsync {
	public void getById(int resourceId, AsyncCallback<Resource> callback);

	public void list(ArgMap<ResourceArg> args, AsyncCallback<ArrayList<Resource>> callback);

	public void save(Resource resource, AsyncCallback<Resource> callback);

	void clickResource(int adId, AsyncCallback<Void> callback);
}
