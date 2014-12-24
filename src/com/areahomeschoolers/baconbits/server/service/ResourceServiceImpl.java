package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.server.dao.ResourceDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ResourcePageData;

@Controller
@RequestMapping("/resource")
public class ResourceServiceImpl extends GwtController implements ResourceService {

	private static final long serialVersionUID = 1L;

	private ResourceDao dao;

	@Autowired
	public ResourceServiceImpl(ResourceDao dao) {
		this.dao = dao;
	}

	@Override
	public void clickResource(int adId) {
		dao.clickResource(adId);
	}

	@Override
	public Resource getById(int resourceId) {
		return dao.getById(resourceId);
	}

	@Override
	public ResourcePageData getPageData(int resourceId) {
		return dao.getPageData(resourceId);
	}

	@Override
	public void incrementImpressions(ArrayList<Integer> ids) {
		dao.incrementImpressions(ids);
	}

	@Override
	public ArrayList<Resource> list(ArgMap<ResourceArg> args) {
		return dao.list(args);
	}

	@Override
	public Resource save(Resource resource) {
		return dao.save(resource);
	}

}
