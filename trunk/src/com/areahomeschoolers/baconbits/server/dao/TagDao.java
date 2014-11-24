package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

public interface TagDao {
	public Tag addMapping(Tag mappingTag);

	public void delete(int tagId);

	public void deleteMapping(Tag tag);

	public ArrayList<Tag> list(ArgMap<TagArg> args);

	public void save(Tag tag);
}
