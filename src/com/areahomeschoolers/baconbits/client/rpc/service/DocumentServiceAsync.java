package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DocumentServiceAsync {

	void getById(int documentId, AsyncCallback<Document> callback);

	void list(ArgMap<DocumentArg> args, AsyncCallback<ArrayList<Document>> callback);

	void save(Document document, AsyncCallback<Document> callback);
}
