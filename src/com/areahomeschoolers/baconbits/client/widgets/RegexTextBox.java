package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashSet;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.TextBox;

/**
 * An extension of {@link ValidationTextBox} that validates its contents against one or more regular expressions provided via the constructor or
 * {@link #addPattern(String)}.
 */
public class RegexTextBox extends TextBox implements HasValidator {
	private final Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			for (String p : patterns) {
				if (!getValue().matches(p)) {
					validator.setError(true);
					break;
				}
			}
		}
	});

	private final Set<String> patterns = new HashSet<String>();

	public RegexTextBox() {
		this(null);
	}

	public RegexTextBox(String regex) {
		if (regex != null) {
			addPattern(regex);
		}
	}

	/**
	 * Add a regular expression for validation. The subject of validation will be validated sequentially against all expressions that have been added.
	 * 
	 * @param regex
	 */
	public void addPattern(String regex) {
		patterns.add(regex);
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
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		validator.setError(false);
	}
}
