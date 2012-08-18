package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class DateRangeBox extends Composite implements HasValidator, CustomFocusWidget {

	private final PaddedPanel panel = new PaddedPanel();
	private final FocusPanel focusPanel = new FocusPanel(panel);
	private final ValidatorDateBox startDateBox = new ValidatorDateBox();
	private final ValidatorDateBox endDateBox = new ValidatorDateBox();
	private final Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (!startDateBox.getValidator().validate() || !endDateBox.getValidator().validate()) {
				validator.setError(true);
				return;
			}

			if (isRequired()) {
				if (getStartDate() == null || getEndDate() == null) {
					validator.setError(true);
					return;
				}
			}

			// wrong chronology
			if (getStartDate() != null && getEndDate() != null) {
				if (getStartDate().after(getEndDate())) {
					validator.setError(true);
				}
			}
		}
	});

	public DateRangeBox() {
		panel.add(startDateBox);
		panel.add(new Label("to"));
		panel.add(endDateBox);

		initWidget(focusPanel);

		setRequired(false);
		startDateBox.getValidator().useErrorBorder(false);
		endDateBox.getValidator().useErrorBorder(false);

		startDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date newStart = event.getValue();
				if (newStart != null && endDateBox.getValue() == null) {
					endDateBox.setValue(newStart);
				}
			}
		});
	}

	public void clear() {
		startDateBox.setValue(null);
		endDateBox.setValue(null);
	}

	public Date getEndDate() {
		Date date = endDateBox.getValue();

		// Set the time to 23:59:59
		if (date != null) {
			date = ClientDateUtils.addHours(date, 23);
			date = ClientDateUtils.addMinutes(date, 59);
			date = ClientDateUtils.addSeconds(date, 59);
		}

		return date;
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

	public void setFirstAndLastDayOfMonth() {
		startDateBox.setValue(ClientDateUtils.getFirstDayOfMonth(new Date()));
		endDateBox.setValue(ClientDateUtils.getLastDayOfMonth(new Date()));
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

	public void useCurrentMonth() {
		Date today = new Date();
		startDateBox.setValue(ClientDateUtils.getFirstDayOfMonth(today));
		endDateBox.setValue(ClientDateUtils.getLastDayOfMonth(today));
	}
}
