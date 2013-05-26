package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TagServiceAsync {
	void addMapping(Tag mappingTag, AsyncCallback<Tag> callback);

	void deleteMapping(Tag tag, AsyncCallback<Void> callback);

	void list(ArgMap<TagArg> args, AsyncCallback<ArrayList<Tag>> callback);

	void save(Tag tag, AsyncCallback<Tag> callback);
}
