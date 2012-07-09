package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.TextArea;

public class RequiredTextArea extends TextArea implements HasValidator {
	private final Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (getValue().trim().isEmpty()) {
				validator.setError(true);
			}
		}
	});

	public RequiredTextArea() {
		setRequired(true);
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);

		if (required) {
			setCharacterWidth(WidgetFactory.DEFAULT_TEXT_AREA_WIDTH);
			setVisibleLines(WidgetFactory.DEFAULT_TEXT_AREA_HEIGHT);
		}
	}
}
