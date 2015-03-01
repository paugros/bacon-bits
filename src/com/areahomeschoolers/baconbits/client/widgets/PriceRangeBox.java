package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PriceRangeBox extends Composite implements HasValidator {
	public static String getPriceText(double low, double high, boolean notApplicable) {
		if (notApplicable) {
			return "N/A";
		}
		String txt = "";
		if (low > 0 && high > 0) {
			txt = Formatter.formatCurrency(low) + " - " + Formatter.formatCurrency(high);
		} else if (high > 0) {
			txt = "Up to " + Formatter.formatCurrency(high);
		} else if (low > 0) {
			txt = Formatter.formatCurrency(low);
		} else {
			txt = "Free";
		}

		return txt;
	}

	private HorizontalPanel mainPanel = new HorizontalPanel();
	private NumericTextBox lowPrice = new NumericTextBox(2);
	private NumericTextBox highPrice = new NumericTextBox(2);
	private FocusPanel fp = new FocusPanel(mainPanel);

	private Validator validator = new Validator(fp, new ValidatorCommand() {
		@Override
		public void validate(Validator v) {
			double low = lowPrice.getDouble();
			double high = highPrice.getDouble();
			if (lowPrice.isEmpty() && high > 0) {
				v.setError(true);
				v.setErrorMessage("Low must be set if high is set");
			}
			if (high > 0 && high < low) {
				v.setError(true);
				v.setErrorMessage("High must be more than low unless empty");
			}

			if (v.isRequired() && lowPrice.isEmpty()) {
				v.setError(true);
			}

			if (!lowPrice.getValidator().validate() || !highPrice.getValidator().validate()) {
				v.setError(true);
			}
		}
	});

	public PriceRangeBox() {
		validator.addChildValidator(lowPrice.getValidator());
		validator.addChildValidator(highPrice.getValidator());
		lowPrice.setVisibleLength(7);
		highPrice.setVisibleLength(7);
		lowPrice.setMaxLength(10);
		highPrice.setMaxLength(10);
		VerticalPanel lp = new VerticalPanel();
		Label lowLabel = new Label("low");
		lowLabel.addStyleName("smallText");
		lp.add(lowLabel);
		lp.add(lowPrice);

		VerticalPanel hp = new VerticalPanel();
		Label highLabel = new Label("high (optional)");
		highLabel.addStyleName("smallText");
		hp.add(highLabel);
		hp.add(highPrice);

		mainPanel.add(lp);
		mainPanel.add(hp);

		initWidget(fp);
	}

	public double getHigh() {
		return highPrice.getDouble();
	}

	public double getLow() {
		return lowPrice.getDouble();
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	public boolean isEmpty() {
		return lowPrice.getText().trim().isEmpty();
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}

	public void setValues(double low, double high) {
		lowPrice.setValue(low);
		if (high > 0) {
			highPrice.setValue(high);
		}
	}

}
