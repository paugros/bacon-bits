package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class NumericRangeBox extends Composite implements HasValidator, CustomFocusWidget {

	public static String getAgeRangeText(int low, int high) {
		String txt = "";
		if (low > 0 && high > 0) {
			txt = low + " - " + high;
		} else if (high > 0) {
			txt = "Up to " + high;
		} else if (low > 0) {
			txt = Integer.toString(low) + " and up";
		} else {
			txt = "N/A";
		}

		return txt;
	}

	public static String getParticipantRangeText(int low, int high) {
		String txt = "";
		if (low > 0 && high > 0) {
			txt = low + " - " + high;
		} else if (high > 0) {
			txt = "Up to " + high;
		} else if (low > 0) {
			txt = "At least " + Integer.toString(low);
		} else {
			txt = "N/A";
		}

		return txt;
	}

	private final PaddedPanel panel = new PaddedPanel();
	private final FocusPanel focusPanel = new FocusPanel();
	private final NumericTextBox fromInput;
	private final NumericTextBox toInput;
	private boolean allowSameVal = true;
	private boolean allowZeroForNoLimit = false;

	private final Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (!isRequired()) {
				return;
			}

			double fromVal = fromInput.getDouble();
			double toVal = toInput.getDouble();

			if (((!allowZeroForNoLimit || toVal != 0) && fromVal > toVal) || (!allowSameVal && fromVal == toVal)) {
				validator.setError(true);
				validator.setErrorMessage("Mininum must be less than " + (allowSameVal ? "or equal to" : "") + " maximum value.");
				return;
			}
		}
	});

	public NumericRangeBox() {
		fromInput = new NumericTextBox();
		toInput = new NumericTextBox();

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

	public boolean getAllowZeroForNoLimit() {
		return allowZeroForNoLimit;
	}

	public int getFromValue() {
		return fromInput.getInteger();
	}

	public int getToValue() {
		return toInput.getInteger();
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

	public void setAllowZeroForNoLimit(boolean allowZeroForNoLimit) {
		this.allowZeroForNoLimit = allowZeroForNoLimit;
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

		fromInput.setMaximumValue(max);
		toInput.setText(Double.toString(max));
	}

	public void setRange(int min, int max) {
		fromInput.setMinimumValue(min);
		fromInput.setText(Integer.toString(min));

		fromInput.setMaximumValue(max);
		toInput.setText(Integer.toString(max));
	}

	@Override
	public void setRequired(boolean required) {
		fromInput.getValidator().setRequired(required);
		toInput.getValidator().setRequired(required);
		validator.setRequired(required);
	}
}
