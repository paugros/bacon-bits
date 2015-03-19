package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/tag")
public interface TagService extends RemoteService {
	public Tag addMapping(Tag mappingTag);

	public void addMappings(int entityId, HashSet<Tag> tags);

	public void delete(int tagId);

	public void deleteMapping(Tag tag);

	public ArrayList<Tag> list(ArgMap<TagArg> args);

	public Tag merge(Tag tag, HashSet<Integer> tagIds);

	public Tag save(Tag tag);
}
