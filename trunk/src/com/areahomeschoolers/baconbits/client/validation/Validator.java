package com.areahomeschoolers.baconbits.client.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.util.ErrorLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Used to determine and store error information regarding user input.
 */
public class Validator {
	private HasAllFocusHandlers target;
	private boolean hasError;
	private boolean required;
	private ErrorLevel errorLevel;
	private String errorMessage;
	private final Set<ValidatorCommand> validatorCommands = new HashSet<ValidatorCommand>();
	private static final String errorStyle = "gwt-TextBoxError";
	private HandlerRegistration handlerRegistration;
	private boolean useErrorBorder = true;
	private final List<Validator> childValidators = new ArrayList<Validator>();
	private boolean enabled = true;

	private boolean skipNextValidation = false;

	/**
	 * Records and adds focus-validation to a target, and pairs it with a {@link ValidatorCommand}.
	 * 
	 * @param target
	 * @param validatorCommand
	 */
	public Validator(HasAllFocusHandlers target, ValidatorCommand validatorCommand) {
		addValidatorCommand(validatorCommand);
		setTarget(target);
	}

	public Validator(HasAllFocusHandlers target, ValidatorCommand validatorCommand, boolean useErrorBorder) {
		this(target, validatorCommand);
		useErrorBorder(useErrorBorder);
	}

	/**
	 * Adds a reference to a Validator that will be considered a child of this one. Useful for error synchronization and dependency.
	 * 
	 * @param child
	 */
	public void addChildValidator(Validator child) {
		childValidators.add(child);
	}

	/**
	 * Adds a {@link ValidatorCommand} to be executed upon validation.
	 * 
	 * @param command
	 */
	public void addValidatorCommand(ValidatorCommand command) {
		validatorCommands.add(command);
	}

	/**
	 * Removes all ValidatorCommands associated with this Validator.
	 */
	public void clearValidatorCommands() {
		validatorCommands.clear();
	}

	/**
	 * @return The error level of the current error, if any, otherwise null.
	 */
	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}

	/**
	 * @return The current error message, if any, or null.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return The target currently associated with this Validator, the content of which is to be validated.
	 */
	public HasAllFocusHandlers getTarget() {
		return target;
	}

	/**
	 * @return Whether there currently exists a validation error.
	 */
	public boolean hasError() {
		return hasError;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return Whether the target of this {@link Validator} requires a value. When false, validation code will not execute on any target of type
	 *         {@link TextBoxBase} type when it is empty.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Removes this validator from its associated target
	 */
	public void removeFromTarget() {
		if (handlerRegistration != null) {
			handlerRegistration.removeHandler();
		}
	}

	/**
	 * Sets the error message produced by validating the user input contained in this Validator's target. This method automatically sets the error state to
	 * true, so a call to {@link #setError(boolean)} is not required afterwards.
	 * 
	 * @param errorMessage
	 */
	public void reportError(String errorMessage) {
		setError(true);
		this.errorMessage = errorMessage;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Sets the error state of this Validator. This can be used to programmatically set or clear an error for the associated target. This method is responsible
	 * for setting and clearing the error style. This method is automatically invoked by {@link #reportError(String)}. Therefore, it only needs to be invoked
	 * from within validation code when there is an error that has no message.
	 * 
	 * @param hasError
	 */
	public void setError(boolean hasError) {
		this.hasError = hasError;

		if (target != null) {
			if (hasError) {
				if (useErrorBorder) {
					((UIObject) target).addStyleName(errorStyle);
				}
			} else {
				// errorMessage = null;
				errorLevel = null;
				((UIObject) target).removeStyleName(errorStyle);
			}
		}

		// clear errors on child validators when this validator is cleared
		if (!hasError && !childValidators.isEmpty()) {
			for (Validator validator : childValidators) {
				validator.setError(false);
			}
		}
	}

	/**
	 * Sets the error level for the current error.
	 * 
	 * @param errorLevel
	 */
	public void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}

	/**
	 * Sets the error message that will be used in the event of a validation error.
	 * 
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Sets whether the target of this {@link Validator} requires a value. When false, validation code will not execute on any target of type
	 * {@link TextBoxBase} type when it is empty. Useful in cases where a value is not required, but must comply with a standard if it is specified.
	 * 
	 * @param required
	 */
	public void setRequired(boolean required) {
		this.required = required;
		if (!required) {
			setError(false);
		}
	}

	/**
	 * Changes the target associated with this Validator.
	 * 
	 * @param focusWidget
	 */
	public void setTarget(HasAllFocusHandlers focusWidget) {
		this.target = focusWidget;
		if (target == null) {
			return;
		}

		removeFromTarget();

		handlerRegistration = focusWidget.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (!skipNextValidation) {
							validate();
						} else {
							skipNextValidation = !skipNextValidation;
						}
					}
				});
			}
		});
	}

	public void skipNextValidation() {
		skipNextValidation = true;
	}

	/**
	 * @param useErrorBorder
	 *            Whether to apply an error border style to its target upon error.
	 */
	public void useErrorBorder(boolean useErrorBorder) {
		this.useErrorBorder = useErrorBorder;
		if (target == null) {
			return;
		}

		if (useErrorBorder && hasError) {
			((UIObject) target).addStyleName(errorStyle);
		} else if (!useErrorBorder && hasError) {
			((UIObject) target).removeStyleName(errorStyle);
		}
	}

	/**
	 * @return Whether this Validator applies an error border style to its target upon error.
	 */
	public boolean usesErrorBorder() {
		return useErrorBorder;
	}

	/**
	 * Programmatically validates the content of the target using the {@link ValidatorCommand}.
	 * 
	 * @return Whether validation executed without error.
	 */
	public boolean validate() {
		// we need to clear the current state in order to discover whether the validation code produces an error
		hasError = false;

		// can't have errors if we're disabled or the input widget is disabled
		if (!enabled) {
			return true;
		}

		if (target != null && target instanceof FocusWidget) {
			if (!((FocusWidget) target).isEnabled()) {
				return true;
			}
		}

		int totalErrors = 0;
		if (needsValidation()) {
			for (ValidatorCommand command : validatorCommands) {
				command.validate(this);
				if (hasError) {
					totalErrors++;
				}
			}
			// if we don't have an error, ensure that the proper action is taken
			if (totalErrors == 0) {
				setError(false);
			}
		} else {
			setError(false);
		}

		if (!hasError && !childValidators.isEmpty()) {
			for (Validator validator : childValidators) {
				if (!validator.validate()) {
					hasError = true;
					totalErrors++;
				}
			}
		}

		return !hasError;
	}

	/**
	 * False if the target is both optional <b>and</b> empty.
	 * 
	 * @return boolean
	 */
	private boolean needsValidation() {
		if (target != null && target instanceof TextBoxBase) {
			String value = ((TextBoxBase) target).getValue();
			if (!required && value.isEmpty()) {
				return false;
			}
		}

		// commented out the below because it was preventing custom validation on the status ListBox of the WtPage
		// need to assess exactly what was depending on this and find a work-around
		// if (target instanceof ListBox && !required) {
		// return false;
		// }

		return true;
	}
}
