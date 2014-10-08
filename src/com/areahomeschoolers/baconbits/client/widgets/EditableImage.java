package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.content.document.FileUploadDialog;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class EditableImage extends Composite {
	private Image image;
	private UploadCompleteHandler uploadCompleteHandler;
	private Integer docId;
	private int entId;
	private DocumentLinkType linkType;

	public EditableImage(DocumentLinkType documentLinkType, int entityId, Integer documentId, boolean editable) {
		docId = documentId;
		entId = entityId;
		linkType = documentLinkType;

		if (docId == null || docId == 0) {
			image = new Image(MainImageBundle.INSTANCE.upload());
		} else {
			image = new Image(Constants.DOCUMENT_URL_PREFIX + docId);
		}

		initWidget(image);

		if (!editable) {
			return;
		}

		image.addStyleName("pointer");

		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				click();
			}
		});
	}

	public void click() {
		final FileUploadDialog uploadDialog = new FileUploadDialog(linkType, entId, false, new UploadCompleteHandler() {
			@Override
			public void onUploadComplete(int documentId) {
				docId = documentId;
				image.setUrl(Constants.DOCUMENT_URL_PREFIX + documentId);
				if (uploadCompleteHandler != null) {
					uploadCompleteHandler.onUploadComplete(documentId);
				}
			}
		});

		uploadDialog.getForm().addFormValidatorCommand(new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				String fileName = uploadDialog.getFileName();
				if (Common.isNullOrBlank(fileName)) {
					validator.setError(true);
				}

				if (!Document.hasImageExtension(fileName)) {
					validator.setError(true);
					validator.setErrorMessage("Invalid image file.");
				}
			}
		});

		uploadDialog.center();
	}

	public Image getImage() {
		return image;
	}

	public void setUploadCompleteHandler(UploadCompleteHandler uploadCompleteHandler) {
		this.uploadCompleteHandler = uploadCompleteHandler;
	}
}
