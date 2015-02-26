package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class DateTimeRangeBox extends Composite implements HasValidator, CustomFocusWidget {
	private PaddedPanel panel = new PaddedPanel();
	private FocusPanel focusPanel = new FocusPanel(panel);
	private DateTimeBox startDateBox = new DateTimeBox();
	private DateTimeBox endDateBox = new DateTimeBox();
	private Validator validator = new Validator(focusPanel, new ValidatorCommand() {
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

	private boolean allDay = false;

	public DateTimeRangeBox() {
		panel.add(new Label("from "));
		panel.add(startDateBox);
		panel.add(new Label(" to "));
		panel.add(endDateBox);

		initWidget(focusPanel);

		setRequired(false);
		startDateBox.getValidator().useErrorBorder(false);
		endDateBox.getValidator().useErrorBorder(false);

		startDateBox.syncDateWith(endDateBox);
	}

	public void addEndValueChangeHandler(ValueChangeHandler<Date> handler) {
		endDateBox.addValueChangeHandler(handler);
	}

	public void addStartValueChangeHandler(ValueChangeHandler<Date> handler) {
		startDateBox.addValueChangeHandler(handler);
	}

	public void clear() {
		startDateBox.setValue(null);
		endDateBox.setValue(null);
	}

	public void clearDatesKeepTimes() {
		startDateBox.clearDate();
		endDateBox.clearDate();
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

	public boolean isAllDay() {
		return allDay;
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	public void setAllDay(boolean allDay) {
		endDateBox.setValue(null);
		endDateBox.setEnabled(!allDay);
		startDateBox.setTimeEnabled(!allDay);
		this.allDay = allDay;
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

	public void setTimeEnabled(boolean enabled) {
		startDateBox.setTimeEnabled(enabled);
		endDateBox.setTimeEnabled(enabled);
	}
}
