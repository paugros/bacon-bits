package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.validation.ValidationErrorHandler;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.validation.ValidatorManager;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Container class that manages {@link FormField FormFields} as a set of related member fields. Provides bulk operations and state tracking, and interacts with
 * a {@link FormTable}, if present. Emancipated members are subject to limited management, as they toggle and submit independently.
 */
public class Form {
	public static enum FieldEditMode {
		EMANCIPATED, GROUPED, ALL
	}

	private String id;
	private final Set<FormField> allFormFields = new LinkedHashSet<FormField>();
	private final Set<FormField> emancipatedFormFields = new LinkedHashSet<FormField>();
	private final Set<FormField> validatorFormFields = new LinkedHashSet<FormField>();
	private final List<Validator> submitValidators = new ArrayList<Validator>();
	private ValidatorManager validatorManager = new ValidatorManager();
	private final Map<Integer, FormField> idFormFields = new HashMap<Integer, FormField>();
	private final List<FormSubmitHandler> submitHandlers = new ArrayList<FormSubmitHandler>();
	private ClickLabel editLabel;
	private LinkPanel linkPanel;
	private ButtonPanel buttonPanel;
	private VerticalPanel errorPanel;
	private VerticalPanel bottomPanel;
	private Button submitButton;
	private ValidationErrorHandler errorHandler;
	private boolean enabled = true;
	private EntityDto<?> dto;
	private final Set<Form> partners = new HashSet<Form>();
	private String customNotePreText;
	private HandlerRegistration submitButtonHandler;
	private boolean useConfirmation = false;

	/**
	 * New {@link Form} which will execute formSubmitHandler upon submission.
	 * 
	 * @param formSubmitHandler
	 */
	public Form(FormSubmitHandler formSubmitHandler) {
		submitHandlers.add(formSubmitHandler);
	}

	/**
	 * Adds a {@link FormField}.
	 * 
	 * @param field
	 */
	public void addField(FormField field) {
		allFormFields.add(field);
		field.setForm(this);
		if (!enabled) {
			field.setEnabled(false);
		}
	}

	public void addFields(Set<FormField> fields) {
		for (FormField field : fields) {
			addField(field);
		}
	}

	/**
	 * Adds a {@link FormSubmitHandler} for this manager.
	 * 
	 * @param formSubmitHandler
	 */
	public void addFormSubmitHandler(FormSubmitHandler formSubmitHandler) {
		submitHandlers.add(formSubmitHandler);
	}

	/**
	 * This should be used to add a validation to the Form that does not pertain to a particular field. These commands will not be run if any form fields fail
	 * validation.
	 * 
	 * @param vc
	 */
	public HandlerRegistration addFormValidatorCommand(ValidatorCommand vc) {
		final Validator validator = new Validator(null, vc);
		validatorManager.addValidator(validator);
		submitValidators.add(validator);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				validatorManager.removeValidator(validator);
				submitValidators.remove(validator);
			}
		};
	}

	public void addPartner(Form form) {
		partners.add(form);
	}

	public void clearErrors() {
		if (errorPanel != null) {
			errorPanel.clear();
		}

		if (validatorManager != null) {
			validatorManager.clearErrors();
		}

		for (FormField field : validatorFormFields) {
			field.getLabelPanel().removeStyleName("errorText");
		}
	}

	public void clearPartners() {
		partners.clear();
	}

	/**
	 * Converts the contained form for use on an add page: all input widgets will be displayed, and the toggle label will be hidden.
	 */
	public void configureForAdd() {
		if (editLabel != null) {
			editLabel.setVisible(false);
		}
		setInputVisibility(true);
	}

	public void configureForAdd(FieldTable fieldTable) {
		fieldTable.addLinkPanel(getLinkPanel());
		fieldTable.addBottomPanel(getBottomPanel());
		configureForAdd();
	}

	/**
	 * Converts the contained form for use on an edit page: all input widgets will be displayed, and the toggle label will be shown.
	 */
	public void configureForEdit() {
		getEditLink().setVisible(true);
		setInputVisibility(true);
	}

	/**
	 * Converts the contained form for use on a view page: all input widgets will be hidden, and the toggle label will be shown.
	 */
	public void configureForView() {
		getEditLink().setVisible(true);
		setInputVisibility(false);
	}

	public FormField createFormField(String label, Widget inputWidget) {
		return createFormField(label, inputWidget, null);
	}

	/**
	 * Creates a grouped {@link FormField}, which implements {@link FormField}, and subjects it to grouped management.
	 * 
	 * @param label
	 * @param inputWidget
	 * @param displayWidget
	 * @return The new field
	 */
	public FormField createFormField(String label, Widget inputWidget, Widget displayWidget) {
		FormField field = new FormField(label, inputWidget, displayWidget);
		field.setForm(this);
		allFormFields.add(field);
		return field;
	}

	/**
	 * Creates an emancipated {@link FormField}, which implements {@link FormField}, and subjects it to management.
	 * 
	 * @param label
	 * @param inputWidget
	 * @param displayWidget
	 * @param formSubmitHandler
	 * @return The new field
	 */
	public FormField createFormField(String label, Widget inputWidget, Widget displayWidget, FormSubmitHandler formSubmitHandler) {
		FormField field = createFormField(label, inputWidget, displayWidget);
		field.emancipate(formSubmitHandler);
		return field;
	}

	public FormField createFormField(String label, WidgetCreator widgetCreator, Widget displayWidget) {
		FormField field = createFormField(label, new Label(), displayWidget);
		field.setInputCreator(widgetCreator);
		return field;
	}

	/**
	 * Creates a Button that has various submission behaviors associated with it, including execution of validation and subsequent prevention or execution of
	 * {@link FormSubmitHandler} items.
	 * 
	 * @return
	 */
	public Button createSubmitButton() {
		registerSubmitButton(new Button("Save"));
		getButtonPanel().addCenterButton(submitButton);
		return submitButton;
	}

	/**
	 * Emancipates <b>all</b> fields. Useful for converting an add form into an edit form.
	 */
	public void emancipate() {
		for (FormField field : allFormFields) {
			field.emancipate(submitHandlers);
			emancipatedFormFields.add(field);
		}
	}

	public Set<FormField> getAllFormFields() {
		return allFormFields;
	}

	public VerticalPanel getBottomPanel() {
		if (bottomPanel == null) {
			createBottomPanel();
		}
		return bottomPanel;
	}

	public ButtonPanel getButtonPanel() {
		if (buttonPanel == null) {
			createButtonPanel();
		}
		return buttonPanel;
	}

	public String getCustomNotePreText() {
		return customNotePreText;
	}

	public EntityDto<?> getDto() {
		return dto;
	}

	/**
	 * Returns a set of all member fields that currently have validation errors.
	 * 
	 * @return
	 */
	public List<FormField> getErrorFields() {
		List<FormField> errorFields = new ArrayList<FormField>();
		for (FormField field : validatorFormFields) {
			if (field.getValidator().hasError()) {
				errorFields.add(field);
			}
		}
		return errorFields;
	}

	public ValidationErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public VerticalPanel getErrorPanel() {
		if (errorPanel == null) {
			createErrorPanel();
		}
		return errorPanel;
	}

	/**
	 * The first member in the set of all. Useful in providing a {@link FormField} to a {@link FormSubmitHandler} when grouped fields are being submitted. This
	 * is important so that the submission code can have access to this manager without storing a reference to it.
	 */
	public FormField getFirstFormField() {
		if (allFormFields.isEmpty()) {
			return null;
		}

		return allFormFields.iterator().next();
	}

	/**
	 * @param id
	 * @return The {@link FormField} associated with the specified numeric id
	 */
	public FormField getFormFieldById(Integer id) {
		return idFormFields.get(id);
	}

	/**
	 * @return The {@link FormSubmitHandler} to be executed upon submission of the form
	 */
	public List<FormSubmitHandler> getFormSubmitHandlers() {
		return submitHandlers;
	}

	/**
	 * @return This manager's unique identifier
	 */
	public String getId() {
		return id;
	}

	public LinkPanel getLinkPanel() {
		if (linkPanel == null) {
			createLinkPanel();
		}
		return linkPanel;
	}

	/**
	 * @return The submit button associated with this form.
	 */
	public Button getSubmitButton() {
		if (submitButton == null) {
			createSubmitButton();
		}
		return submitButton;
	}

	/**
	 * @return The {@link ValidatorManager} for this form, containing all relevant {@link Validator}s.
	 */
	public ValidatorManager getValidatorManager() {
		return validatorManager;
	}

	/**
	 * Bulk operation to initialize all members at once.
	 */
	public void initialize() {
		clearErrors();
		if (submitButton != null) {
			submitButton.setEnabled(true);
		}
		for (FormField field : allFormFields) {
			field.initialize();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	// /**
	// * Returns a VerticalPanel with the LinkPanel at the top, the provided form contents in the middle, and the bottom panel at the bottom. The bottom panel
	// * consists of the error panel and the button panel.
	// *
	// * @param contents
	// * @return
	// */
	// public VerticalPanel getWrappedFormContents(FieldTable contents) {
	//
	// VerticalPanel vp = new VerticalPanel();
	// vp.setWidth("100%");
	// vp.add(getLinkPanel());
	// vp.add(contents);
	// vp.add(getBottomPanel());
	//
	// return vp;
	// }

	/**
	 * Notifies the manager that the specified field is to be considered emancipated. Generally invoked from within a {@link FormField FormField's} emancipation
	 * implementation. This will exclude the field from many bulk operations.
	 * 
	 * @param field
	 */
	public void notifyEmancipation(FormField field) {
		emancipatedFormFields.add(field);
	}

	/**
	 * Notifies the manager that the specified field has received a numeric id.
	 * 
	 * @param field
	 * @param id
	 */
	public void notifyId(FormField field, Integer id) {
		idFormFields.put(id, field);
	}

	/**
	 * Notifies the manager that the specified field will have validation performed on it, and should be considered in bulk validation operations.
	 * 
	 * @param field
	 */
	public void notifyValidator(FormField field) {
		validatorManager.addValidator(field.getValidator());
		validatorFormFields.add(field);
	}

	/**
	 * Adds various submit behaviors to a button, including execution of validation and subsequent prevention or execution of {@link FormSubmitHandler} items.
	 * 
	 * @param button
	 */
	public HandlerRegistration registerSubmitButton(Button button) {
		submitButton = button;
		submitButtonHandler = button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
		});

		submitButton.setVisible(groupedInputsAreVisible());
		return submitButtonHandler;
	}

	/**
	 * Removes all references to the FormField.
	 * 
	 * @param field
	 */
	public void removeField(FormField field) {
		validatorFormFields.remove(field);
		emancipatedFormFields.remove(field);
		allFormFields.remove(field);
	}

	public void setBottomPanel(VerticalPanel bottomPanel) {
		this.bottomPanel = bottomPanel;
	}

	public void setButtonPanel(ButtonPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	public void setCustomNotePreText(String customNotePreText) {
		this.customNotePreText = customNotePreText;
	}

	public void setDto(EntityDto<?> dto) {
		this.dto = dto;
	}

	public void setEditLink(ClickLabel editLink) {
		this.editLabel = editLink;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (!enabled) {
			setInputVisibility(false);
		}
		for (FormField field : allFormFields) {
			field.setEnabled(enabled);
		}
	}

	public void setErrorHandler(ValidationErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void setErrorPanel(VerticalPanel errorPanel) {
		this.errorPanel = errorPanel;
	}

	/**
	 * Sets the unique identifier for this manager.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setInputVisibility(boolean setVisible) {
		setInputVisibility(setVisible, FieldEditMode.GROUPED);
	}

	/**
	 * Bulk operation that sets the visibility of the input widgets in grouped members.
	 * 
	 * @param setVisible
	 *            Whether to show or hide the input widgets
	 */
	public void setInputVisibility(boolean setVisible, FieldEditMode mode) {
		// don't allow showing of inputs when disabled
		if (!enabled && setVisible) {
			return;
		}
		for (FormField field : allFormFields) {
			switch (mode) {
			case ALL:
				field.setInputVisibility(setVisible);
				break;
			case EMANCIPATED:
				if (field.isEmancipated()) {
					field.setInputVisibility(setVisible);
				}
				break;
			case GROUPED:
				if (!field.isEmancipated()) {
					field.setInputVisibility(setVisible);
				}
				break;
			}
		}

		if (submitButton != null && mode != FieldEditMode.EMANCIPATED) {
			submitButton.setVisible(setVisible);
			submitButton.setEnabled(true);

			if (setVisible) {
				if (editLabel != null) {
					editLabel.setText("Cancel");
				}
			} else {
				clearErrors();
				if (editLabel != null) {
					editLabel.setText("Edit");
				}
			}
		}
	}

	public void setLinkPanel(LinkPanel linkPanel) {
		this.linkPanel = linkPanel;
	}

	/**
	 * This will control whether or not the form will use the confirmation dialog before submitting. If useConfirmation(String) is not called, this will method
	 * will have no effect.
	 * 
	 * @param use
	 */
	public void setUseConfirmation(boolean use) {
		useConfirmation = use;
	}

	/**
	 * Sets the {@link ValidatorManager} for input validators in this form.
	 * 
	 * @param validatorManager
	 */
	public void setValidatorManager(ValidatorManager validatorManager) {
		this.validatorManager = validatorManager;
	}

	/**
	 * Displays a list of validation errors in the validation error panel.
	 * 
	 * @param errors
	 */
	public void showValidationErrors() {
		if (errorPanel == null) {
			createErrorPanel();
		}
		Set<String> errors = getValidatorManager().getAllErrorMessages();
		boolean hasVisibleErrors = false;
		for (String error : errors) {
			if (error.isEmpty()) {
				continue;
			}

			hasVisibleErrors = true;
			errorPanel.add(new Label(error));
		}

		if (!hasVisibleErrors) {
			errorPanel.clear();
			errorPanel.add(new Label("One or more fields have errors."));
		}
	}

	/**
	 * Programmatically submits this form.
	 */
	public void submit() {
		submit(true);
	}

	/**
	 * Bulk operation that toggles the visibility of the input widgets in grouped members.
	 */
	public void toggleInputVisibility() {
		setInputVisibility(!groupedInputsAreVisible());
	}

	/**
	 * Bulk operation that executes the DTO updater {@link Command} for all members (including emancipated members).
	 */
	public void updateDto() {
		for (Form form : partners) {
			form.updateDto();
		}

		for (FormField field : allFormFields) {
			field.updateDto();
		}
	}

	/**
	 * Sticks in a confirm dialog around the submit command.
	 * 
	 * @param message
	 */
	public void useConfirmation(final String message) {
		useConfirmation = true;
		submitButtonHandler.removeHandler();

		submitButtonHandler = submitButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (useConfirmation && validate()) {
					ConfirmDialog.confirm(message, new ConfirmHandler() {
						@Override
						public void onConfirm() {
							submit(false);
						}
					});
				} else {
					submit();
				}
			}
		});
	}

	/**
	 * This is used here instead of {@link ValidatorManager#validateAll()} because so as to be field label aware (turn label red upon error).
	 * 
	 * @return Returns whether the form validated correctly.
	 */
	public boolean validate() {
		clearErrors();
		for (FormField formField : validatorFormFields) {
			if (!formField.isEmancipated() && formField.isVisible() && formField.isAttached()) {
				formField.validate();
			}
		}

		if (!partners.isEmpty()) {
			for (Form form : partners) {
				form.validate();
			}
		}

		if (!validatorManager.hasError()) {
			for (Validator val : submitValidators) {
				val.validate();
			}
		}

		return !getValidatorManager().hasError() && !hasValidatorPartnerErrors();
	}

	private void createBottomPanel() {
		bottomPanel = new VerticalPanel();
		bottomPanel.setWidth("100%");
		bottomPanel.add(getErrorPanel());
		bottomPanel.add(getButtonPanel());
	}

	private void createButtonPanel() {
		buttonPanel = new ButtonPanel();
		buttonPanel.setWidth("100%");
		createSubmitButton();
	}

	private void createEditLabel() {
		editLabel = new ClickLabel("Edit", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				toggleInputVisibility();
			}
		});

		if (groupedInputsAreVisible()) {
			editLabel.setText("Cancel");
		}

		editLabel.setVisible(false);
	}

	private void createErrorPanel() {
		errorPanel = new VerticalPanel();
		errorPanel.getElement().getStyle().setMarginLeft(18, Unit.PX);
		errorPanel.setStyleName("errorText");
	}

	private void createLinkPanel() {
		linkPanel = new LinkPanel();
		linkPanel.setWidth("600px");
		linkPanel.add(getEditLink());
		linkPanel.setCellHorizontalAlignment(getEditLink(), HasHorizontalAlignment.ALIGN_RIGHT);
		linkPanel.setSpacing(3);
	}

	private ClickLabel getEditLink() {
		if (editLabel == null) {
			createEditLabel();
		}
		return editLabel;
	}

	private boolean groupedInputsAreVisible() {
		for (FormField field : allFormFields) {
			if (!field.isEmancipated() && field.isEnabled()) {
				return field.getInputWidget().isVisible();
			}
		}

		return true;
	}

	private boolean hasValidatorPartnerErrors() {
		for (Form form : partners) {
			if (form.getValidatorManager().hasError()) {
				return true;
			}
		}

		return false;
	}

	private void submit(boolean useValidation) {
		if (!useValidation || validate()) {
			submitButton.setEnabled(false);
			updateDto();
			Application.setRpcFailureCommand(new Command() {
				@Override
				public void execute() {
					submitButton.setEnabled(true);
				}
			});
			// return the first form field so that the page can access the form controller through it (if it doesn't have a reference)
			for (FormSubmitHandler handler : getFormSubmitHandlers()) {
				handler.onFormSubmit(getFirstFormField());
			}
		} else {
			if (errorHandler != null) {
				errorHandler.onError(getErrorFields());
			} else {
				showValidationErrors();
			}
		}
	}
}
