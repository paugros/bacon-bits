package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

@Controller
@RequestMapping("/tag")
public class TagServiceImpl extends GwtController implements TagService {

	private static final long serialVersionUID = 1L;

	private TagDao dao;

	@Autowired
	public TagServiceImpl(TagDao dao) {
		this.dao = dao;
	}

	@Override
	public Tag addMapping(Tag mappingTag) {
		return dao.addMapping(mappingTag);
	}

	@Override
	public void deleteMapping(Tag tag) {
		dao.deleteMapping(tag);
	}

	@Override
	public ArrayList<Tag> list(ArgMap<TagArg> args) {
		return dao.list(args);
	}

}
