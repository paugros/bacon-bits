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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class EditableImage extends Composite {
	private Image image;
	private UploadCompleteHandler uploadCompleteHandler;
	private Integer imageId;
	private int itemId;
	private DocumentLinkType linkType;
	private boolean enabled = true;
	private ImageResource imageResource;

	public EditableImage(DocumentLinkType documentLinkType, int entityId) {
		itemId = entityId;
		linkType = documentLinkType;
	}

	public void click() {
		final FileUploadDialog uploadDialog = new FileUploadDialog(linkType, itemId, false, new UploadCompleteHandler() {
			@Override
			public void onUploadComplete(int documentId) {
				imageId = documentId;
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

	public ImageResource getImageResource() {
		return imageResource;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void populate() {
		if (image == null) {
			if (imageId != null && imageId != 0) {
				image = new Image(Constants.DOCUMENT_URL_PREFIX + imageId);
			} else if (imageResource != null) {
				image = new Image(imageResource);
			} else {
				image = new Image(MainImageBundle.INSTANCE.logo());
			}
		}

		initWidget(image);

		if (!enabled) {
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

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setImageResource(ImageResource imageResource) {
		this.imageResource = imageResource;
	}

	public void setUploadCompleteHandler(UploadCompleteHandler uploadCompleteHandler) {
		this.uploadCompleteHandler = uploadCompleteHandler;
	}
}
