package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.datepicker.client.DateBox;

public class ValidatorDateBox extends DateBox implements HasValidator, CustomFocusWidget {
	private final Validator validator;

	public ValidatorDateBox() {
		validator = new Validator(getTextBox(), new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				validator.setError(hasErrors());
			}
		});

		setFormat(new DefaultFormat(Formatter.DEFAULT_DATE_FORMAT));
		setWidth("60px");
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	public boolean hasErrors() {
		if (isDatePickerShowing()) {
			return false;
		}
		String textValue = getTextBox().getText().trim();
		if (validator.isRequired() && textValue.isEmpty()) {
			return true;
		}

		if (!textValue.isEmpty() && getValue() == null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}
}
