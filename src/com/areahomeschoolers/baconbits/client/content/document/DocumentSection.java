package com.areahomeschoolers.baconbits.client.content.document;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.DocumentService;
import com.areahomeschoolers.baconbits.client.rpc.service.DocumentServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DocumentSection extends Composite {
	private final DocumentServiceAsync documentService = (DocumentServiceAsync) ServiceCache.getService(DocumentService.class);
	private VerticalPanel vp = new VerticalPanel();
	private FlexTable documentTable = new FlexTable();
	private DocumentArg documentListArg;
	private DocumentLinkType linkType;
	private int entityId;
	private boolean isAdmin;
	private HasDocuments item;
	private ArrayList<Document> documents;

	public DocumentSection(HasDocuments item, boolean isAdmin) {
		this.item = item;
		switch (item.getEntityType()) {
		case ARTICLE:
			documentListArg = DocumentArg.ARTICLE_ID;
			linkType = DocumentLinkType.ARTICLE;
			break;
		case EVENT:
			documentListArg = DocumentArg.EVENT_ID;
			linkType = DocumentLinkType.EVENT;
			break;
		default:
			break;
		}
		this.entityId = item.getId();
		this.isAdmin = isAdmin;

		documentTable.setWidth("200px");

		if (isAdmin) {
			ClickLabel cl = new ClickLabel("+ Add document", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					FileUploadDialog dialog = new FileUploadDialog(linkType, entityId, new UploadCompleteHandler() {
						@Override
						public void onUploadComplete(int documentId) {
							init();
						}
					});

					dialog.center();
				}
			});
			cl.addStyleName("bold");
			vp.add(cl);
		}

		vp.add(documentTable);
		initWidget(vp);
	}

	public void init() {
		if (!item.hasDocuments()) {
			return;
		}

		documentService.list(new ArgMap<DocumentArg>(documentListArg, entityId), new Callback<ArrayList<Document>>() {
			@Override
			protected void doOnSuccess(ArrayList<Document> result) {
				documents = result;
				populateRows();
			}
		});
	}

	private void populateRows() {
		documentTable.removeAllRows();

		for (final Document d : documents) {
			final int row = documentTable.getRowCount();
			Image icon = new Image(FileUploadDialog.getFileIconResourceFromExtension(d.getFileExtension()));
			PaddedPanel hp = new PaddedPanel();
			hp.add(icon);
			Anchor name = new Anchor(d.getDescription(), "/baconbits/service/file?id=" + d.getId());
			name.setWordWrap(false);
			hp.add(name);
			documentTable.setWidget(row, 0, hp);

			if (isAdmin) {
				documentTable.setWidget(row, 1, new ClickLabel("X", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						ConfirmDialog.confirm("Really delete \"" + d.getDescription() + "\"?", new ConfirmHandler() {
							@Override
							public void onConfirm() {
								documentService.delete(d.getId(), new Callback<Void>() {
									@Override
									protected void doOnSuccess(Void result) {
										documents.remove(d);
										populateRows();
									}
								});
							}
						});
					}
				}));
			}
		}

	}

}
