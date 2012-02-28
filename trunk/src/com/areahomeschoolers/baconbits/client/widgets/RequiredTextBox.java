package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.TextBox;

/**
 * An extension of {@link ValidationTextBox} that has no particular format requirements, except that the contents be non-empty.
 */
public class RequiredTextBox extends TextBox implements HasValidator {
	private int minumumLength;

	Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (getValue().trim().isEmpty()) {
				validator.setError(true);
			}

			if (minumumLength > 0) {
				if (getText().length() < minumumLength) {
					validator.setError(true);
				}
			}
		}
	});

	public RequiredTextBox() {
		setRequired(true);
	}

	public int getMinumumLength() {
		return minumumLength;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	public void setMinumumLength(int minumumLength) {
		this.minumumLength = minumumLength;
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		validator.setError(false);
	}
}
