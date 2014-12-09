package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TagServiceAsync {
	public void addMapping(Tag mappingTag, AsyncCallback<Tag> callback);

	public void delete(int tagId, AsyncCallback<Void> callback);

	public void deleteMapping(Tag tag, AsyncCallback<Void> callback);

	public void list(ArgMap<TagArg> args, AsyncCallback<ArrayList<Tag>> callback);

	public void save(Tag tag, AsyncCallback<Tag> callback);

	void addMappings(int entityId, ArrayList<Tag> tags, AsyncCallback<Void> callback);

	void merge(Tag tag, HashSet<Integer> tagIds, AsyncCallback<Tag> callback);
}
