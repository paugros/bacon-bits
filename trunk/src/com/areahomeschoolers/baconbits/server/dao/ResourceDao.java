package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ResourcePageData;

public interface ResourceDao {
	public void clickResource(int adId);

	public Resource getById(int resourceId);

	public ResourcePageData getPageData(int resourceId);

	public ArrayList<Resource> list(ArgMap<ResourceArg> args);

	public Resource save(Resource resource);
}