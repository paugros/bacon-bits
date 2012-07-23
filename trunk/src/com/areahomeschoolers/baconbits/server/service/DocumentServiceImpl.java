package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.DocumentService;
import com.areahomeschoolers.baconbits.server.dao.DocumentDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;

@Controller
@RequestMapping("/document")
public class DocumentServiceImpl extends GwtController implements DocumentService {

	private static final long serialVersionUID = 1L;

	private DocumentDao dao;

	@Autowired
	public DocumentServiceImpl(DocumentDao dao) {
		this.dao = dao;
	}

	@Override
	public void delete(int documentId) {
		dao.delete(documentId);
	}

	@Override
	public Document getById(int documentId) {
		return dao.getById(documentId);
	}

	@Override
	public ArrayList<Document> list(ArgMap<DocumentArg> args) {
		return dao.list(args);
	}

	@Override
	public Document save(Document document) {
		return dao.save(document);
	}

}
