package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class NumericRangeBox extends Composite implements HasValidator, CustomFocusWidget {

	private final PaddedPanel panel = new PaddedPanel();
	private final FocusPanel focusPanel = new FocusPanel();
	private final NumericTextBox fromInput;
	private final NumericTextBox toInput;
	private boolean allowSameVal = true;
	private boolean hasRange = false;

	private final Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {

			if (!fromInput.getValidator().validate() || !toInput.getValidator().validate()) {
				if (hasRange) {
					String min = fromInput.getPrecision() == 0 ? Long.toString(Math.round(fromInput.getMinimumValue())) : Double.toString(fromInput
							.getMinimumValue());
					String max = fromInput.getPrecision() == 0 ? Long.toString(Math.round(fromInput.getMaximumValue())) : Double.toString(fromInput
							.getMaximumValue());
					validator.setErrorMessage("Must enter values between " + min + " and " + max + ".");
				}
				validator.setError(true);
				return;
			}

			if (!isRequired()) {
				return;
			}

			double fromVal = fromInput.getDouble();
			double toVal = toInput.getDouble();

			if (fromVal > toVal || (!allowSameVal && fromVal == toVal)) {
				validator.setError(true);
				validator.setErrorMessage("Left value must be less than " + (allowSameVal ? "or equal to" : "") + " right value.");
				return;
			}
		}
	});

	public NumericRangeBox() {
		this(0);
	}

	public NumericRangeBox(int precision) {
		fromInput = new NumericTextBox(precision);
		toInput = new NumericTextBox(precision);

		fromInput.setVisibleLength(4);
		toInput.setVisibleLength(4);

		fromInput.getValidator().useErrorBorder(false);
		toInput.getValidator().useErrorBorder(false);

		setRequired(true);

		panel.add(fromInput);
		panel.add(new Label("to"));
		panel.add(toInput);

		focusPanel.setWidget(panel);

		initWidget(focusPanel);
	}

	public double getFromValue() {
		return fromInput.getDouble();
	}

	public double getToValue() {
		return toInput.getDouble();
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	public void setAllowSameValue(boolean allow) {
		this.allowSameVal = allow;
	}

	@Override
	public void setEnabled(boolean enabled) {
		fromInput.setEnabled(enabled);
		toInput.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focus) {
		focusPanel.setFocus(focus);
	}

	public void setRange(double min, double max) {
		fromInput.setMinimumValue(min);
		fromInput.setText(Double.toString(min));
		toInput.setMinimumValue(min);

		fromInput.setMaximumValue(max);
		toInput.setText(Double.toString(max));
		toInput.setMaximumValue(max);

		hasRange = true;
	}

	public void setRange(int min, int max) {
		fromInput.setMinimumValue(min);
		fromInput.setText(Integer.toString(min));
		toInput.setMinimumValue(min);

		fromInput.setMaximumValue(max);
		toInput.setText(Integer.toString(max));
		toInput.setMaximumValue(max);

		hasRange = true;
	}

	@Override
	public void setRequired(boolean required) {
		fromInput.getValidator().setRequired(required);
		toInput.getValidator().setRequired(required);
		validator.setRequired(required);
	}
}
