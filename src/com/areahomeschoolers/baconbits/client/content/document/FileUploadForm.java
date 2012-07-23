package com.areahomeschoolers.baconbits.client.content.document;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link FormPanel} containing a {@link FileUpload} intended to handle the most common file upload form needs
 */
public class FileUploadForm extends FormPanel {
	private final FileUpload upload = new FileUpload();
	private final Hidden username = new Hidden("userId", Integer.toString(Application.getCurrentUser().getId()));
	private final Hidden uploadMethod = new Hidden("uploadMethod");
	private final Hidden description = new Hidden("description");
	private final Hidden linkType = new Hidden("linkType");
	private final Hidden linkId = new Hidden("linkId");
	private final Hidden fileName = new Hidden("fileName");
	private final Hidden isPublic = new Hidden("isPublic", "false");

	public FileUploadForm() {
		this("document");
	}

	public FileUploadForm(boolean addLinkTypes) {
		this("document", addLinkTypes);
	}

	public FileUploadForm(String uploadMethodName) {
		this(uploadMethodName, true);
	}

	public FileUploadForm(String uploadMethodName, boolean addLinkTypes) {
		final VerticalPanel vp = new VerticalPanel();

		setAction("/baconbits/service/file");
		setEncoding(FormPanel.ENCODING_MULTIPART);
		setMethod(FormPanel.METHOD_POST);

		uploadMethod.setValue(uploadMethodName);

		upload.setName("fileUpload");
		vp.add(upload);
		vp.add(uploadMethod);
		vp.add(description);
		vp.add(username);
		vp.add(fileName);
		vp.add(isPublic);

		if (addLinkTypes) {
			vp.add(linkType);
			vp.add(linkId);
		}

		add(vp);
	}

	public Hidden getFileName() {
		return fileName;
	}

	public FileUpload getFileUpload() {
		return upload;
	}

	public void setDescription(String value) {
		description.setValue(value);
	}

	public void setFileName(String name) {
		fileName.setValue(name);
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic.setValue(Boolean.toString(isPublic));
	}

	/**
	 * @param dlt
	 * @param id
	 */
	public void setLinkFields(DocumentLinkType dlt, int id) {
		linkType.setValue(dlt.toString());
		linkId.setValue(Integer.toString(id));
	}
}
