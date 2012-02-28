package com.areahomeschoolers.baconbits.client.validation;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.FocusWidget;

/**
 * Bulk-operations class used to create and/or manage multiple {@link Validator Validators}. Used when a set of Validators must operate in logical tandem, such
 * as in a form.
 */
public class ValidatorManager {
	private Set<Validator> validators = new HashSet<Validator>();

	/**
	 * Adds a validator.
	 * 
	 * @param validator
	 */
	public void addValidator(Validator validator) {
		validators.add(validator);
	}

	/**
	 * Sets error state to false on all member {@link Validator} items.
	 */
	public void clearErrors() {
		for (Validator validator : validators) {
			validator.setError(false);
		}
	}

	/**
	 * Creates, registers and returns a new member {@link Validator} using the specified FocusWidget and {@link ValidatorCommand}.
	 * 
	 * @param focusWidget
	 * @param validatorCommand
	 * @return
	 */
	public Validator createValidator(FocusWidget focusWidget, ValidatorCommand validatorCommand) {
		Validator validator = new Validator(focusWidget, validatorCommand);
		validators.add(validator);

		return validator;
	}

	/**
	 * @return A set of all error messages in member {@link Validator Validators}. Returns an empty set if there are no error messages.
	 */
	public Set<String> getAllErrorMessages() {
		Set<String> messages = new HashSet<String>();
		for (Validator validator : validators) {
			if (validator.getErrorMessage() != null && validator.hasError()) {
				messages.add(validator.getErrorMessage());
			}
		}

		return messages;
	}

	/**
	 * @return True if any member {@link Validator} has an error, otherwise false.
	 */
	public boolean hasError() {
		for (Validator validator : validators) {
			if (validator.hasError()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Executes {@link Validator#validate()} on every member Validator.
	 */
	public void validateAll() {
		for (Validator validator : validators) {
			validator.validate();
		}
	}
}
