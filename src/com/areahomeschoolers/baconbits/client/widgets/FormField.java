package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormCancelHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.FormToggleHandler;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.ValidationErrorHandler;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * {@link Composite} consisting of a {@link Grid} containing a label panel, input and display widget panel, and toggle label (left to right). {@link FormField
 * FormFields} can operate independently or as grouped or emancipated members of a {@link Form}. {@link FormField FormFields} make up the bulk of all forms in
 * the system. Core functionality includes visibility state management, widget initialization, and underlying DTO modification. <br>
 * <br>
 * <b>Notes about emancipated fields</b><br>
 * They have their own toggle and submission widgets.<br>
 * They capture the enter and escape keys to trigger submission and visibility toggling respectively.<br>
 * They place cursor focus on the input widget when it is shown, if it can receive focus.<br>
 * They perform validation independently. The submit button has a default {@link MouseDownHandler}, which ensures that if an error is present upon submission,
 * the following occurs:<br>
 * <br>
 * a) it will be displayed below the field<br>
 * b) the visibility state will not be altered<br>
 * c) the user-specified {@link FormSubmitHandler} will not be executed
 */
public class FormField extends Composite {
	/**
	 * Used to determine the placement of the submit button for this FormField.
	 */
	public enum ButtonPlacement {
		RIGHT, BOTTOM;
	}

	private boolean enabled = true;
	private ButtonPlacement buttonPlacement = ButtonPlacement.RIGHT;

	// the outermost grid upon which this Composite is based;
	// left cell contains input and display widgets and an error label below
	// right cell contains the edit state toggle label
	private final Grid fieldGrid = new Grid(1, 2);

	// first element in panel above; contains the panel below (and optionally bottom-placed submit buttons)
	private final VerticalPanel vPanel = new VerticalPanel();

	// first element in panel above; contains the input and display widgets (and optionally right-placed submit buttons)
	private final HorizontalPanel hPanel = new HorizontalPanel();

	/**
	 * This label contains the text label of the field, and the required asterisk. It is not automatically placed anywhere.
	 */
	private final HorizontalPanel labelPanel;
	private Form form;
	private Widget inputWidget, displayWidget;
	private boolean isEmancipated = false;
	private boolean alwaysShowAsterisk;
	private List<FormSubmitHandler> submitHandlers = new ArrayList<FormSubmitHandler>();
	private final List<FormCancelHandler> cancelHandlers = new ArrayList<FormCancelHandler>();
	private final List<FormToggleHandler> toggleHandlers = new ArrayList<FormToggleHandler>();
	private HandlerRegistration submitButtonHandler;
	private boolean useConfirmation = false;
	private Command dtoUpdater;
	private Command initializer;
	private final Button submitButton = new Button("Save");
	private Validator validator;
	private String editLabelText = "Edit";
	private final ClickLabel editLabel = new ClickLabel(editLabelText, new MouseDownHandler() {
		@Override
		public void onMouseDown(MouseDownEvent e) {
			toggleInputVisibility();
			if (!inputWidget.isVisible()) {
				fireCancelHandlers();
			}
		}
	});
	private final Label fieldLabel;
	private Label errorLabel = new Label();
	private final Label requiredLabel = new Label("*");
	private boolean isRequired;
	private ValidationErrorHandler errorHandler;
	private final List<Widget> inputPartners = new ArrayList<Widget>();
	private boolean hasDisplayWidget;
	private final Set<FormField> subFields = new LinkedHashSet<FormField>();
	private FieldTable subFieldTable;
	private WidgetCreator inputCreator;
	private LinkPanel linkPanel;

	/**
	 * New instance with the specified label, input and and display widgets.
	 * 
	 * @param label
	 *            Label containing the text description of this field
	 * @param inputWidget
	 *            Widget used to capture user input
	 * @param displayWidget
	 *            Widget used to display data from the underlying DTO
	 */
	public FormField(String label, Widget inputWidget, Widget displayWidget) {
		initWidget(fieldGrid);
		addStyleName("FormField");

		// label stuff
		fieldLabel = new Label(label);
		labelPanel = FieldTable.createFieldLabelPanel(fieldLabel, requiredLabel);
		requiredLabel.setVisible(false);
		fieldGrid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

		setInputWidget(inputWidget);
		setDisplayWidget(displayWidget);
		vPanel.add(hPanel);
		fieldGrid.setWidget(0, 0, vPanel);
		setInputVisibility(!hasDisplayWidget, false);

		submitButtonHandler = submitButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				submit();
			}
		});
	}

	/**
	 * Create and immediately emancipate a new field.
	 * 
	 * @param label
	 *            Label containing the text description of this field
	 * @param inputWidget
	 *            Widget used to capture user input
	 * @param displayWidget
	 *            Widget used to display data from the underlying DTO
	 * @param saveAction
	 *            The {@link FormSubmitHandler} to execute upon submission of this field
	 */
	public FormField(String label, Widget inputWidget, Widget displayWidget, FormSubmitHandler saveAction) {
		this(label, inputWidget, displayWidget);
		emancipate(saveAction);
	}

	public HandlerRegistration addFormCancelHandler(final FormCancelHandler formCancelHandler) {
		cancelHandlers.add(formCancelHandler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				cancelHandlers.remove(formCancelHandler);
			}
		};
	}

	public HandlerRegistration addFormSubmitHandler(final FormSubmitHandler formSubmitHandler) {
		return addFormSubmitHandler(formSubmitHandler, false);
	}

	/**
	 * Sets the {@link FormSubmitHandler} to be executed upon submission.
	 * 
	 * @param formSubmitHandler
	 */
	public HandlerRegistration addFormSubmitHandler(final FormSubmitHandler formSubmitHandler, boolean emancipatedOverride) {
		HandlerRegistration reg = new HandlerRegistration() {
			@Override
			public void removeHandler() {
				submitHandlers.remove(formSubmitHandler);
			}
		};

		if (!isEmancipated && !emancipatedOverride) {
			return reg;
		}

		submitHandlers.add(formSubmitHandler);

		return reg;
	}

	public HandlerRegistration addFormToggleHandler(final FormToggleHandler formToggleHandler) {
		toggleHandlers.add(formToggleHandler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				toggleHandlers.remove(formToggleHandler);
			}
		};
	}

	/**
	 * Adds a Widget whose visibility will be toggled along with the input widget.
	 * 
	 * @param partner
	 */
	public void addInputPartner(Widget partner) {
		inputPartners.add(partner);
		partner.setVisible(inputWidget.isVisible());
	}

	public void addSubField(FormField field) {
		subFields.add(field);
		if (subFieldTable == null) {
			subFieldTable = new FieldTable();
			subFieldTable.setLabelColumnWidth(LabelColumnWidth.NONE);
			vPanel.insert(subFieldTable, 1);
		}
		field.getFieldLabel().setWordWrap(false);
		subFieldTable.addField(field);
	}

	public void alwaysShowAsterisk(boolean alwaysShow) {
		this.alwaysShowAsterisk = alwaysShow;

		if (alwaysShow && isRequired) {
			requiredLabel.setVisible(true);
		}
	}

	public void cancel() {
		if (!inputWidget.isVisible()) {
			return;
		}

		setInputVisibility(false);
		fireCancelHandlers();
	}

	public void configureForAdd() {
		removeStyleDependentName("emancipated");
		setInputVisibility(true, false);
		editLabel.setVisible(false);
		submitButton.setVisible(false);
	}

	public void configureForView() {
		if (isEmancipated) {
			addStyleDependentName("emancipated");
			editLabel.setVisible(true);
		}
		setInputVisibility(false, false);
	}

	/**
	 * Displays the current input widget validation error, if any.
	 * 
	 */
	public void displayValidationError() {
		if (validator != null) {
			if (validator.getErrorMessage() != null && validator.hasError()) {
				getErrorLabel().setText(validator.getErrorMessage());
				vPanel.add(errorLabel);
				return;
			}
		}
		errorLabel.removeFromParent();
	}

	/**
	 * Frees the {@link FormWidget} from its containing {@link Form}, if belongs to one. A {@link FormSubmitHandler} is provided at this time to ensure the
	 * {@link FormWidget}'s ability to function independently. When a {@link FormWidget} is emancipated, it receives its own input/display widget toggler.
	 * 
	 * @param formSubmitHandler
	 *            The {@link FormSubmitHandler} to execute upon submission of this {@link FormWidget}
	 */
	public void emancipate() {
		if (isEmancipated) {
			return;
		}

		if (enabled) {
			addStyleDependentName("emancipated");
		} else {
			addStyleDependentName("disabled");
		}

		if (submitHandlers.isEmpty() && form != null) {
			submitHandlers.addAll(form.getFormSubmitHandlers());
		}

		isEmancipated = true;
		editLabel.setVisible(true);
		submitButton.setVisible(true);

		placeButton();
		setInputVisibility(!hasDisplayWidget, false);

		getLinkPanel().setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		linkPanel.add(editLabel);

		fieldGrid.setWidget(0, 1, linkPanel);
		fieldGrid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		fieldGrid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		editLabel.setVisible(enabled);

		if (form != null) {
			form.notifyEmancipation(this);
		}
	}

	public void emancipate(FormSubmitHandler formSubmitHandler) {
		submitHandlers.add(formSubmitHandler);
		emancipate();
	}

	public void emancipate(List<FormSubmitHandler> formSubmitHandlers) {
		if (submitHandlers.isEmpty()) {
			submitHandlers = formSubmitHandlers;
		}
		emancipate();
	}

	/**
	 * @return The widget used to display data from the underlying DTO.
	 */
	public Widget getDisplayWidget() {
		return displayWidget;
	}

	public Label getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new Label();
			errorLabel.addStyleName("errorText");
		}
		return errorLabel;
	}

	public Label getFieldLabel() {
		return fieldLabel;
	}

	/**
	 * @return The containing {@link Form}, if there is one.
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * @return The widget used to capture user input.
	 */
	public Widget getInputWidget() {
		return inputWidget;
	}

	/**
	 * @return The {@link HorizontalPanel} including the required indicator and the {@link FormWidget}'s text label
	 */
	public HorizontalPanel getLabelPanel() {
		return labelPanel;
	}

	public String getLabelText() {
		return fieldLabel.getText();
	}

	public LinkPanel getLinkPanel() {
		if (linkPanel == null) {
			linkPanel = new LinkPanel();
		}
		return linkPanel;
	}

	public Set<FormField> getSubFields() {
		return subFields;
	}

	public FieldTable getSubFieldTable() {
		return subFieldTable;
	}

	public Button getSubmitButton() {
		return submitButton;
	}

	public String getTextFromDisplay() {
		String value = displayWidget.toString();
		return new HTML(value).getText();
	}

	/**
	 * @return The input validator for this @ link FormWidget} .
	 */
	public Validator getValidator() {
		return validator;
	}

	/**
	 * @return The HorizontalPanel containing the input and display widgets, and optionally submit button on the right.
	 */
	public HorizontalPanel getWidgetPanel() {
		return hPanel;
	}

	/**
	 * Executes the widget initializer {@link Command} specified in {@link #setInitializer(Command)} at a minimum, but may also perform other initialization
	 * tasks.
	 */
	public void initialize() {
		if (initializer != null) {
			initializer.execute();
		}

		for (FormField field : subFields) {
			if (field.getForm() == null) {
				field.initialize();
			}
		}
	}

	public boolean inputIsCreated() {
		return inputCreator == null;
	}

	/**
	 * @return True if the {@link FormWidget} is emancipated
	 */
	public boolean isEmancipated() {
		return isEmancipated;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return True if the {@link FormWidget} is required
	 */
	public boolean isRequired() {
		return isRequired;
	}

	public void removeEditLabel() {
		linkPanel.removeWidget(editLabel);
	}

	public void removeEmancipation() {
		if (!isEmancipated) {
			return;
		}

		isEmancipated = false;

		setInputVisibility(true);
		submitButton.setVisible(false);
		editLabel.setVisible(false);
	}

	/**
	 * This
	 * 
	 * @param enabled
	 */
	public void setAllValidatorsEnabled(boolean enabled) {
		for (FormField subField : subFields) {
			subField.setAllValidatorsEnabled(enabled);
		}

		if (getValidator() != null) {
			getValidator().setEnabled(enabled);
		}
	}

	public void setButtonPlacement(ButtonPlacement buttonPlacement) {
		this.buttonPlacement = buttonPlacement;

		if (isEmancipated) {
			placeButton();
		}
	}

	public void setDisplayWidget(Widget displayWidget) {
		hasDisplayWidget = displayWidget != null;

		// a null display widget becomes a blank label, to serve as a place holder
		if (displayWidget == null) {
			displayWidget = new Label();
		}

		if (this.displayWidget != null) {
			displayWidget.setVisible(this.displayWidget.isVisible());
		}

		int currentIndex = hPanel.getWidgetIndex(this.displayWidget);
		if (currentIndex != -1) {
			hPanel.insert(displayWidget, currentIndex);
			this.displayWidget.removeFromParent();
		} else {
			hPanel.add(displayWidget);
		}

		this.displayWidget = displayWidget;
	}

	public void setDtoUpdater(Command dtoUpdater) {
		this.dtoUpdater = dtoUpdater;
	}

	public void setEnabled(boolean enabled) {
		setEnabled(enabled, false);
	}

	public void setEnabled(boolean enabled, boolean alwaysChangeVisibility) {
		this.enabled = enabled;
		if (!enabled) {
			removeStyleDependentName("emancipated");
			addStyleDependentName("disabled");
			if (hasDisplayWidget && (isEmancipated || alwaysChangeVisibility)) {
				setInputVisibility(false, false);
			}
		} else {
			if (isEmancipated) {
				removeStyleDependentName("disabled");
				addStyleDependentName("emancipated");
			}
		}

		if (validator != null) {
			if (enabled) {
				setRequired(validator.isRequired());
			} else {
				setRequired(false, false);
			}
			validator.setEnabled(enabled);
		}

		if (inputWidget instanceof CustomFocusWidget) {
			((CustomFocusWidget) inputWidget).setEnabled(enabled);
		} else if (inputWidget instanceof FocusWidget) {
			((FocusWidget) inputWidget).setEnabled(enabled);
		}

		if (isEmancipated) {
			editLabel.setVisible(enabled);
		}
	}

	/**
	 * Moves the {@link FormWidget} from its current {@link Form} (if any) to the one specified. If called on an emancipated {@link FormWidget}, this has the
	 * effect of un-emancipating it.
	 * 
	 * @param form
	 */
	public void setForm(Form form) {
		this.form = form;
		if (!form.isEnabled()) {
			setEnabled(false);
		}

		if (validator != null) {
			form.notifyValidator(this);
		}
	}

	/**
	 * The {@link Command} to be executed in order to initialize both the input and display widgets. This method is generally called both after the
	 * {@link FormWidget} is created and after each successful submission.
	 * 
	 * @param widgetInitializer
	 */
	public void setInitializer(Command initializer) {
		this.initializer = initializer;
	}

	public void setInputCreator(WidgetCreator creator) {
		inputCreator = creator;
	}

	/**
	 * Sets the visibility of the input widget, but does <b>not</b> modify the visibility of the display widget.
	 * 
	 * @param isVisible
	 */
	public void setInputVisibility(boolean setVisible) {
		setInputVisibility(setVisible, true);
	}

	public void setInputVisibility(boolean setVisible, boolean reInitialize) {
		// don't allow showing of inputs when disabled
		if (!enabled && setVisible) {
			return;
		}

		if (setVisible && !inputIsCreated()) {
			createInputWidget();
		}

		if (!setVisible) {
			// reinitialize if we're canceling or toggling back after a save
			// we used to defer the code in the block below for some reason pertaining to:
			// "because hitting escape prevents proper initialization from occurring immediately"
			if (reInitialize) {
				initialize();
			}
			if (inputWidget instanceof Focusable) {
				((Focusable) inputWidget).setFocus(false);
			} else if (inputWidget instanceof CustomFocusWidget) {
				((CustomFocusWidget) inputWidget).setFocus(false);
			}
			// also remove red text on label and hide required asterisk (if they were there)
			labelPanel.removeStyleName("errorText");
			if (!alwaysShowAsterisk) {
				requiredLabel.setVisible(false);
			}
			// also clear out any errors that may exist on the input widget
			if (validator != null && validator.hasError()) {
				validator.setError(false);
			}
			if (subFieldTable != null) {
				subFieldTable.setVisible(false);
			}
		} else if (isRequired) {
			// if we're showing the inputs and it's required, show the asterisk
			requiredLabel.setVisible(true);
		}

		inputWidget.setVisible(setVisible);
		if (hasDisplayWidget) {
			displayWidget.setVisible(!setVisible);
		}

		if (isEmancipated) {
			submitButton.setVisible(setVisible);
			submitButton.setEnabled(true);

			// set visibility of all widgets that need to follow the visibility of the input widget
			for (Widget partner : inputPartners) {
				partner.setVisible(setVisible);
			}

			if (setVisible) {
				addStyleDependentName("editing");

				editLabelText = editLabel.getText();
				editLabel.setText("Cancel");
				// focus on the input widget if we can
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (inputWidget instanceof Focusable) {
							((Focusable) inputWidget).setFocus(true);
						} else if (inputWidget instanceof CustomFocusWidget) {
							((CustomFocusWidget) inputWidget).setFocus(true);
						}
					}
				});
			} else {
				removeStyleDependentName("editing");

				editLabel.setText(editLabelText);
				if (errorLabel != null) {
					errorLabel.removeFromParent();
				}
			}
		}

		for (FormField field : subFields) {
			field.setInputVisibility(setVisible, reInitialize);
		}

		for (FormToggleHandler handler : toggleHandlers) {
			handler.onFormToggle(setVisible);
		}
	}

	public void setInputWidget(Widget widget) {
		// a null input widget becomes a blank label, to serve as a place holder
		if (widget == null) {
			widget = new Label();
		}
		widget.setVisible(!(isEmancipated && hasDisplayWidget));
		int currentIndex = hPanel.getWidgetIndex(inputWidget);
		if (currentIndex != -1) {
			hPanel.insert(widget, currentIndex);
			inputWidget.removeFromParent();
		} else {
			hPanel.add(widget);
		}

		inputWidget = widget;

		// emancipated fields have special behavior associated with certain keystrokes
		if (inputWidget instanceof HasKeyDownHandlers) {
			((HasKeyDownHandlers) inputWidget).addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (!isEmancipated) {
						return;
					}

					int keyCode = event.getNativeKeyCode();

					switch (keyCode) {
					case KeyCodes.KEY_ESCAPE:
						if (hasDisplayWidget) {
							toggleInputVisibility();
						}
						break;
					case KeyCodes.KEY_ENTER:
						if (!(inputWidget instanceof TextArea) && !(inputWidget instanceof MaxLengthTextArea)) {
							submitButton.fireEvent(new MouseDownEvent() {
							});
						}
						break;
					default:
						break;
					}
				}
			});
		}

		// change the default button placement for TextAreas
		if (inputWidget instanceof TextArea || inputWidget instanceof ControlledRichTextArea || inputWidget instanceof MaxLengthTextArea) {
			setButtonPlacement(ButtonPlacement.BOTTOM);
		}

		// if the input widget has validation, extract its controller for later use, and mirror its required state
		if (inputWidget instanceof HasValidator) {
			setValidator(((HasValidator) inputWidget).getValidator());
			setRequired(validator.isRequired());
		}
	}

	public void setInstructions(String instructions) {
		fieldLabel.setTitle(instructions);
	}

	public void setLabelText(String text) {
		fieldLabel.setText(text);
	}

	/**
	 * Sets the required property. A required {@link FormWidget} must display the required indicator in its field label when its input widget is shown, and hide
	 * the indicator when the input widget is hidden. If the field has an underlying {@link ValidationTextBox}, it propagates this value to it.
	 * 
	 * @param isRequired
	 */
	public void setRequired(boolean isRequired) {
		setRequired(isRequired, true);
	}

	/**
	 * This will control whether or not the form field will use the confirmation dialog before submitting. If useConfirmation(String) is not called, this will
	 * method will have no effect.
	 * 
	 * @param use
	 */
	public void setUseConfirmation(boolean use) {
		useConfirmation = use;
	}

	public void setValidator(Validator validator) {
		setRequired(validator.isRequired());
		this.validator = validator;

		if (form != null) {
			form.notifyValidator(this);
		}
	}

	public void submit() {
		submit(true);
	}

	public void submit(boolean useValidation) {
		if (!useValidation || validate()) {
			submitButton.setEnabled(false);
			Application.setRpcFailureCommand(new Command() {
				@Override
				public void execute() {
					submitButton.setEnabled(true);
				}
			});
			updateDto();
			for (FormSubmitHandler handler : submitHandlers) {
				handler.onFormSubmit(FormField.this);
			}
		} else if (errorHandler != null) {
			List<FormField> error = new ArrayList<FormField>();
			error.add(FormField.this);
			errorHandler.onError(error);
		}

		displayValidationError();
	}

	public void toggleInputVisibility() {
		setInputVisibility(!inputWidget.isVisible());
	}

	/**
	 * Updates the underlying DTO object using the DTO updater, if one is present.
	 */
	public void updateDto() {
		if (dtoUpdater != null && enabled) {
			dtoUpdater.execute();
			if (!subFields.isEmpty()) {
				for (FormField field : subFields) {
					if (field.getForm() == null) {
						field.updateDto();
					}
				}
			}
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
	 * Returns true if validation does not produce errors.
	 */
	public boolean validate() {
		boolean hasError = false;
		boolean hasEavError = false;

		if (!inputIsCreated()) {
			createInputWidget();
		}

		if (!isVisible()) {
			return true;
		}

		if (validator != null) {
			validator.validate();
			validator.skipNextValidation();
			if (validator.hasError()) {
				hasError = true;
			}
		}

		if (!subFields.isEmpty()) {
			for (FormField field : subFields) {
				if (!field.validate()) {
					hasError = true;
				}
			}
		}

		if (hasError) {
			labelPanel.addStyleName("errorText");
		} else {
			labelPanel.removeStyleName("errorText");
		}

		return !hasError && !hasEavError;
	}

	private void createInputWidget() {
		setInputWidget(inputCreator.createWidget());
		inputCreator = null;
		initialize();
	}

	private void fireCancelHandlers() {
		for (FormCancelHandler handler : cancelHandlers) {
			handler.onFormCancel(this);
		}
	}

	private void placeButton() {
		switch (buttonPlacement) {
		case RIGHT:
			hPanel.add(submitButton);
			hPanel.setCellVerticalAlignment(submitButton, HasVerticalAlignment.ALIGN_MIDDLE);
			break;
		case BOTTOM:
			vPanel.add(submitButton);
			vPanel.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_CENTER);
			break;
		}
	}

	private void setRequired(boolean isRequired, boolean syncValidator) {
		this.isRequired = isRequired;
		requiredLabel.setVisible(isRequired && (inputWidget.isVisible() || alwaysShowAsterisk));

		if (validator != null && syncValidator) {
			if (inputWidget instanceof HasValidator && !(inputWidget instanceof RequiredListBox)) {
				// We can't call this on a RequiredListBox because it would remove the blank item if false.
				((HasValidator) inputWidget).setRequired(isRequired);
			} else {
				validator.setRequired(isRequired);
			}
		}
	}
}
