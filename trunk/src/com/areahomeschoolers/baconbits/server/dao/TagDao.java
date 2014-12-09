package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

public interface TagDao {
	public Tag addMapping(Tag mappingTag);

	public void addMappings(int entityId, ArrayList<Tag> tags);

	public void delete(int tagId);

	public void deleteMapping(Tag tag);

	public ArrayList<Tag> list(ArgMap<TagArg> args);

	public Tag merge(Tag tag, HashSet<Integer> tagIds);

	public Tag save(Tag tag);
}
