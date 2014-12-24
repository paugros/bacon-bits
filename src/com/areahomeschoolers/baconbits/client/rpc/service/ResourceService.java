package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ResourcePageData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/resource")
public interface ResourceService extends RemoteService {
	public void clickResource(int adId);

	public Resource getById(int resourceId);

	public ResourcePageData getPageData(int resourceId);

	public void incrementImpressions(ArrayList<Integer> ids);

	public ArrayList<Resource> list(ArgMap<ResourceArg> args);

	public Resource save(Resource resource);
}
