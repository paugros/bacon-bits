package com.areahomeschoolers.baconbits.client.content.document;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * {@link DialogBox} containing a {@link FileUploadForm} and description {@link TextBox} to handle most common file uploads.
 */
public class FileUploadDialog extends DefaultDialog {
	public final static Map<String, ImageResource> fileMap = new HashMap<String, ImageResource>();

	static {
		// initialize file icon image map
		ImageResource image = MainImageBundle.INSTANCE.fileIconImage();
		ImageResource excel = MainImageBundle.INSTANCE.fileIconExcel();
		ImageResource word = MainImageBundle.INSTANCE.fileIconWord();
		ImageResource pdf = MainImageBundle.INSTANCE.fileIconPdf();
		ImageResource visio = MainImageBundle.INSTANCE.fileIconVisio();
		ImageResource zip = MainImageBundle.INSTANCE.fileIconZip();
		ImageResource text = MainImageBundle.INSTANCE.fileIconText();
		ImageResource powerpoint = MainImageBundle.INSTANCE.fileIconPowerpoint();
		ImageResource html = MainImageBundle.INSTANCE.fileIconHtml();
		ImageResource unknown = MainImageBundle.INSTANCE.fileIconUnknown();

		fileMap.put("jpg", image);
		fileMap.put("jpeg", image);
		fileMap.put("gif", image);
		fileMap.put("png", image);
		fileMap.put("csv", excel);
		fileMap.put("xls", excel);
		fileMap.put("xlsx", excel);
		fileMap.put("doc", word);
		fileMap.put("docx", word);
		fileMap.put("pdf", pdf);
		fileMap.put("vsd", visio);
		fileMap.put("zip", zip);
		fileMap.put("txt", text);
		fileMap.put("ppt", powerpoint);
		fileMap.put("pptx", powerpoint);
		fileMap.put("htm", html);
		fileMap.put("html", html);
		fileMap.put("unknown", unknown);
	}

	public static ImageResource getFileIconResourceFromExtension(String ext) {
		ImageResource ir = fileMap.get(ext.toLowerCase());
		if (ir == null) {
			return fileMap.get("unknown");
		}
		return ir;
	}

	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formWidget) {
			uploadForm.submit();
		}
	});

	private VerticalPanel vp = new VerticalPanel();
	private FieldTable table = new FieldTable();
	private FileUploadForm uploadForm;
	private Label errorLabel = new Label("");

	public FileUploadDialog(DocumentLinkType dlt, int linkId, boolean showDescription, UploadCompleteHandler uploadHandler) {
		this(dlt, linkId, null, showDescription, uploadHandler);
	}

	public FileUploadDialog(DocumentLinkType dlt, int linkId, UploadCompleteHandler uploadHandler) {
		this(dlt, linkId, null, true, uploadHandler);
	}

	public FileUploadDialog(String uploadMethod, UploadCompleteHandler uploadHandler) {
		this(null, 0, uploadMethod, false, uploadHandler);
	}

	private FileUploadDialog(DocumentLinkType dlt, int linkId, String methodName, boolean showDesc, final UploadCompleteHandler uploadHandler) {
		super(false, true);
		setText("Upload Document");
		setWidth("400px");

		if (dlt != null) {
			uploadForm = new FileUploadForm();
			uploadForm.setLinkFields(dlt, linkId);
		} else {
			uploadForm = new FileUploadForm(methodName);
		}

		// init various settings
		errorLabel.addStyleName("errorText");
		vp.add(table);
		vp.add(form.getBottomPanel());
		form.getSubmitButton().setText("Upload");
		Button cancelButton = new Button("Cancel");
		cancelButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				hide();
			}
		});
		form.getButtonPanel().addCenterButton(cancelButton);

		// file upload
		FormField uploadField = form.createFormField("Document:", uploadForm);
		uploadField.setValidator(new Validator(new Button(), new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				validator.setError(uploadForm.getFileUpload().getFilename().length() == 0);
			}
		}));
		uploadField.setRequired(true);
		table.addField(uploadField);

		if (showDesc) {
			// description
			final RequiredTextBox descriptionInput = new RequiredTextBox();
			final FormField descriptionField = form.createFormField("Description:", descriptionInput, null);
			descriptionField.setInitializer(new Command() {
				@Override
				public void execute() {
					descriptionInput.setText("");
				}
			});
			table.addField(descriptionField);

			uploadForm.addSubmitHandler(new SubmitHandler() {
				@Override
				public void onSubmit(SubmitEvent event) {
					// add description value to hidden formpanel input
					uploadForm.setDescription(descriptionInput.getValue());
				}
			});
		}

		form.initialize();
		vp.add(form.getButtonPanel());
		setWidget(vp);
		uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String results = new HTML(event.getResults()).getText();
				if (!Common.isInteger(results)) {
					// show errors from server
					form.getErrorPanel().add(new HTML(results));
					form.getSubmitButton().setEnabled(true);
					return;
				}
				// on success, invoke callback with the document id and hide
				hide();
				uploadHandler.onUploadComplete(Integer.parseInt(results));
				// the below allows for re-submission in FF, but IE doesn't like it, so we generally new up another instance of this dialog
				uploadForm.reset();
			}
		});
	}

	public String getFileName() {
		return uploadForm.getFileUpload().getFilename();
	}

	public Form getForm() {
		return form;
	}

	@Override
	public void hide() {
		super.hide();
		vp.remove(errorLabel);
		form.initialize();
	}
}
