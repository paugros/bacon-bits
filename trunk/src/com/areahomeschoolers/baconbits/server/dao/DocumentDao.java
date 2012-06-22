package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;

public interface DocumentDao {
	public void delete(int documentId);

	public Document getById(int documentId);

	public ArrayList<Document> list(ArgMap<DocumentArg> args);

	public Document save(Document document);
}
