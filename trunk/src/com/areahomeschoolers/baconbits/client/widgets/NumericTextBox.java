package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.TextBox;

/**
 * An extension of {@link ValidationTextBox} that: <b>a)</b> allows numeric characters into it, and <b>b)</b> formats input according to the specified
 * precision. This class will not produce validation errors because it does not allow malformed data to be entered.
 */
public class NumericTextBox extends TextBox implements HasValidator {
	private final List<Character> allowedChars = new ArrayList<Character>();
	private boolean allowNegatives = false;
	private double minimumValue;
	private double maximumValue;
	private int minumumLength;

	private final Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			// because we've overridden setText, this will format the value
			setText(getValue());

			if (precision > 0) {
				if (!Common.isDouble(getValue())) {
					validator.setError(true);
				}
			} else if (!Common.isNumeric(getValue())) {
				validator.setError(true);
			}

			double doubleValue = getDouble();

			if (minimumValue > 0) {
				if (doubleValue < minimumValue) {
					validator.setError(true);
				}
			}

			if (maximumValue > 0) {
				if (doubleValue > maximumValue) {
					validator.setError(true);
				}
			}

			if (minumumLength > 0) {
				if (getText().length() < minumumLength) {
					validator.setError(true);
				}
			}
		}
	});

	private final int precision;

	/**
	 * An instance using the default precision (zero).
	 */
	public NumericTextBox() {
		this(0);
	}

	/**
	 * An instance using the specified precision
	 * 
	 * @param precision
	 *            The precision to use in formatting the contents
	 */
	public NumericTextBox(int precision) {
		this.precision = precision;

		allowedChars.addAll(ClientUtils.ALLOWED_KEY_CODES);

		allowedChars.add(new Character('-'));
		allowedChars.add(new Character('.'));

		// this prevents anything other than numbers, the minus sign, and decimals from going into the box,
		// which guarantees us a graunchable value when we go to format it
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				Character characterCode = (char) event.getNativeEvent().getCharCode();
				Character keyCode = (char) event.getNativeEvent().getKeyCode();

				if ((!Character.isDigit(characterCode) && !allowedChars.contains(characterCode) && !allowedChars.contains(keyCode)) || event.isShiftKeyDown()) {
					event.preventDefault();
				}
			}
		});
	}

	public boolean allowsNegatives() {
		return allowNegatives;
	}

	public double getDouble() {
		String value = getText();
		if (Common.isDouble(value)) {
			return Double.parseDouble(value);
		}

		return 0;
	}

	public Integer getInteger() {
		String value = getText();
		if (Common.isInteger(value)) {
			return Integer.parseInt(value);
		}

		return null;
	}

	public double getMaximumValue() {
		return maximumValue;
	}

	public double getMinimumValue() {
		return minimumValue;
	}

	public int getMinumumLength() {
		return minumumLength;
	}

	/**
	 * @return The precision currently being used for formatting
	 */
	public int getPrecision() {
		return precision;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	public boolean isEmpty() {
		return getText().trim().isEmpty();
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	public void setAllowNegatives(boolean allowNegatives) {
		this.allowNegatives = allowNegatives;
	}

	public void setMaximumValue(double maximumValue) {
		this.maximumValue = maximumValue;
	}

	public void setMinimumValue(double minimumValue) {
		this.minimumValue = minimumValue;
	}

	public void setMinumumLength(int minumumLength) {
		this.minumumLength = minumumLength;
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}

	// we override setText so as to properly format numbers going into the box
	@Override
	public void setText(String text) {
		String newValue = "";
		boolean hasDecimal = false;
		char currentCharacter;

		if (text == null) {
			text = "";
		}

		// loop through string backwards, so we can ignore all but the last decimal
		for (int i = text.length() - 1; i >= 0; i--) {
			currentCharacter = text.charAt(i);
			// allow one decimal for precisions higher than zero
			if (!hasDecimal && currentCharacter == '.' && precision > 0) {
				hasDecimal = true;
				newValue = currentCharacter + newValue;
			}

			// numbers and the minus sign are also preserved
			if (Character.isDigit(currentCharacter) || i == 0 && currentCharacter == '-') {
				newValue = currentCharacter + newValue;
			}

		}

		if (precision > 0) {
			String zeros = "";
			for (int i = 0; i < precision; i++) {
				zeros += "0";
			}
			if (!newValue.isEmpty()) {
				newValue = NumberFormat.getFormat("0." + zeros + ";-0." + zeros).format(Double.parseDouble(newValue));
			}
		}

		if (!allowNegatives) {
			if (newValue.startsWith("-")) {
				newValue = newValue.substring(1);
			}
		}

		super.setText(newValue);
	}

	public void setValidRange(int minumumValue, int maximumValue) {
		this.maximumValue = maximumValue;
		this.minimumValue = minumumValue;
	}

	public void setValue(Number value) {
		String strValue = "";

		if (value != null) {
			strValue = value.toString();
		}

		setText(strValue);
	}
}
