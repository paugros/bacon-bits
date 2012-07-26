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
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.DocumentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.EntityType;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DocumentSection {
	private final DocumentServiceAsync documentService = (DocumentServiceAsync) ServiceCache.getService(DocumentService.class);
	private FlexTable documentTable = new FlexTable();
	private DocumentArg documentListArg;
	private DocumentLinkType linkType;
	private int entityId;
	private boolean isAdmin;
	private FieldTable fieldTable;

	public DocumentSection(FieldTable fieldTable, EntityType type, int entityId, boolean isAdmin) {
		switch (type) {
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
		this.entityId = entityId;
		this.fieldTable = fieldTable;
		this.isAdmin = isAdmin;

		documentTable.setWidth("200px");
	}

	public void populate() {
		documentService.list(new ArgMap<DocumentArg>(documentListArg, entityId), new Callback<ArrayList<Document>>() {
			@Override
			protected void doOnSuccess(ArrayList<Document> result) {
				if (!isAdmin && Common.isNullOrEmpty(result)) {
					return;
				}

				documentTable.removeAllRows();

				if (!documentTable.isAttached()) {
					fieldTable.addField("Documents:", documentTable);
				}

				if (isAdmin) {
					ClickLabel cl = new ClickLabel("Add", new MouseDownHandler() {
						@Override
						public void onMouseDown(MouseDownEvent event) {
							FileUploadDialog dialog = new FileUploadDialog(linkType, entityId, new UploadCompleteHandler() {
								@Override
								public void onUploadComplete(int documentId) {
									populate();
								}
							});

							dialog.center();
						}
					});
					cl.addStyleName("bold");
					documentTable.setWidget(0, 0, cl);
				}

				for (final Document d : result) {
					final int row = documentTable.getRowCount();
					Image icon = new Image(FileUploadDialog.getFileIconResourceFromExtension(d.getFileExtension()));
					PaddedPanel hp = new PaddedPanel();
					hp.add(icon);
					Widget name;
					name = new Anchor(d.getDescription(), "/baconbits/service/file?id=" + d.getId());
					hp.add(name);
					documentTable.setWidget(row, 0, hp);

					if (isAdmin) {
						documentTable.setWidget(row, 1, new ClickLabel("X", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								ConfirmDialog.confirm("Really delete \"" + d.getDescription() + "\"?", new ConfirmHandler() {
									@Override
									public void onConfirm() {
										documentService.delete(d.getId(), new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void result) {
												documentTable.removeRow(row);
											}
										});
									}
								});
							}
						}));
					}
				}

			}
		});
	}

}