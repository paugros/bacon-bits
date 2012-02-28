package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;

public class DateTimeRangeBox extends Composite implements HasValidator, CustomFocusWidget {
	private final PaddedPanel panel = new PaddedPanel();
	private final FocusPanel focusPanel = new FocusPanel(panel);
	private final DateTimeBox startDateBox = new DateTimeBox();
	private final DateTimeBox endDateBox = new DateTimeBox();
	private final Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (!startDateBox.getValidator().validate() || !endDateBox.getValidator().validate()) {
				validator.setError(true);
				return;
			}

			if (getStartDate() == null || getEndDate() == null) {
				validator.setError(true);
				return;
			}

			// wrong chronology
			if (getStartDate().after(getEndDate())) {
				validator.setError(true);
			}
		}
	});

	public DateTimeRangeBox() {
		panel.add(startDateBox);
		panel.add(endDateBox);

		initWidget(focusPanel);

		setRequired(false);
		startDateBox.getValidator().useErrorBorder(false);
		endDateBox.getValidator().useErrorBorder(false);
	}

	public Date getEndDate() {
		return endDateBox.getValue();
	}

	public Date getStartDate() {
		return startDateBox.getValue();
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
	public void setEnabled(boolean enabled) {
		startDateBox.setEnabled(enabled);
		endDateBox.setEnabled(enabled);
	}

	public void setEndDate(Date endDate) {
		endDateBox.setValue(endDate);
	}

	@Override
	public void setFocus(boolean focus) {
		focusPanel.setFocus(focus);
	}

	public void setRange(Date startDate, Date endDate) {
		setStartDate(startDate);
		setEndDate(endDate);
	}

	@Override
	public void setRequired(boolean required) {
		startDateBox.getValidator().setRequired(required);
		endDateBox.getValidator().setRequired(required);
		validator.setRequired(required);
	}

	public void setStartDate(Date startDate) {
		startDateBox.setValue(startDate);
	}
}
