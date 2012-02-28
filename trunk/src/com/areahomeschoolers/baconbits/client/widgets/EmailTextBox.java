package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashSet;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.user.client.ui.TextBox;

/**
 * A {@link ValidationTextBox} for email addresses. Can contain and validate single or multiple addresses.
 */
public class EmailTextBox extends TextBox implements HasValidator {
	private final Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (!isMultiEmail()) {
				if (!Common.isValidEmail(getValue())) {
					validator.setError(true);
				}
			} else {
				if (!compileTypedEmails()) {
					validator.setError(true);
				}
			}
		}
	});

	private boolean multiEmail;
	private Set<String> typedEmails;

	/**
	 * Retrieve the email addresses that were typed into this box as a {@link Set}
	 */
	public Set<String> getEmailSet() {
		compileTypedEmails();
		return typedEmails;
	}

	@Override
	public String getText() {
		return super.getText().trim();
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	/**
	 * Does this box allow multiple addresses to be typed?
	 */
	public boolean isMultiEmail() {
		return multiEmail;
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	/**
	 * Sets whether this box allows multiple email addresses. If true, addresses in the box will be separated by any of the following: commas, semicolons,
	 * spaces
	 * 
	 * @param multiEmail
	 */
	public void setMultiEmail(boolean multiEmail) {
		if (multiEmail) {
			typedEmails = new HashSet<String>();
		} else {
			typedEmails = null;
		}

		this.multiEmail = multiEmail;
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}

	private boolean compileTypedEmails() {
		typedEmails.clear();

		String[] addresses = getValue().split("[,; ]+");
		if (addresses.length == 0) {
			return true;
		}

		for (int i = 0; i < addresses.length; i++) {
			if (addresses[i].isEmpty()) {
				continue;
			}

			if (!Common.isValidEmail(addresses[i])) {
				return false;
			}

			typedEmails.add(addresses[i]);
		}

		return true;
	}

}
