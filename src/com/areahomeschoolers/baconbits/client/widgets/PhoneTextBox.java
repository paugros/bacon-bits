package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.TextBox;

/**
 * An subclass of {@link TextBox} that validates its contents as a phone number, and corrects its formatting if possible. The phone format is: (NPA) NXX-XXXX
 * [xXXX]
 */
public class PhoneTextBox extends TextBox implements HasValidator {

	private final RegExp internationalRegExp = RegExp.compile("^(?:[0-9] ?){6,14}[0-9]$");
	private final RegExp internationalCheck = RegExp.compile("^011.*");
	private final char[] PHONE_FILLERS = { '(', ')', '-', ' ', '.' };
	private final char[] NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private final String domesticRegEx = "[(][0-9]{3}[)] [0-9]{3}-[0-9]{4}( x\\d+)?";
	private final boolean supportExtensions;
	private final String DEFAULT_TEXT = "ex: (555) 555-5555 x55";

	private final Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {

			if (internationalCheck.test(getValue())) {
				if (!internationalRegExp.test(getValue())) {
					validator.setError(true);
				}
			} else {
				String attempt = graunchToFormat(getValue());
				if (attempt.matches(domesticRegEx)) {
					setValue(attempt);
				} else {
					validator.setError(true);
				}
			}
		}
	});

	public PhoneTextBox() {
		this(true);
	}

	public PhoneTextBox(boolean supportExtensions) {
		this.supportExtensions = supportExtensions;
		int length;

		if (supportExtensions) {
			length = 25;
			addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					if (getText().isEmpty()) {
						setText("");
					}
					getElement().getStyle().setColor("#000000");
					getElement().getStyle().setFontStyle(FontStyle.NORMAL);
				}
			});
			addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (getText().isEmpty()) {
						reset();
					}
				}
			});

			addAttachHandler(new Handler() {
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					if (getText().isEmpty()) {
						reset();
					}
				}
			});
		} else {
			length = 14;
		}

		setMaxLength(length);

		setVisibleLength(length);
	}

	@Override
	public String getText() {
		String text = super.getText();
		if (DEFAULT_TEXT.equals(text)) {
			return "";
		}

		return text;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public String getValue() {
		String value = super.getValue();
		if (DEFAULT_TEXT.equals(value)) {
			return "";
		}
		return value;
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

	private String graunchToFormat(String s) {

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (i == 0 && cur == '1') {
				continue;
			} else if (inArray(cur, PHONE_FILLERS)) {
				continue;
			} else if (inArray(cur, NUMBERS)) {
				buf.append(cur);
			} else if (supportExtensions && cur == 'x') {
				buf.append(" x");
			} else if (supportExtensions && cur == 'e') {
				if (s.length() > i + 2 && s.charAt(i + 1) == 'x' && s.charAt(i + 2) == 't') {
					buf.append(" x");
					i = i + 2;
				} else {
					// bad char
					return "";
				}
			} else {
				// bad char
				return "";
			}
		}
		if (buf.length() >= 10) {
			buf.insert(0, "(");
			buf.insert(4, ") ");
			buf.insert(9, "-");
		}

		return buf.toString();
	}

	private boolean inArray(char a, char[] array) {
		for (int i = 0; i < array.length; i++) {
			if (a == array[i]) {
				return true;
			}
		}
		return false;
	}

	private void reset() {
		setText(DEFAULT_TEXT);
		getElement().getStyle().setColor("#666666");
		getElement().getStyle().setFontStyle(FontStyle.ITALIC);
	}

}
