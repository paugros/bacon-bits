package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.TimeBox.TimeIncrement;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class TimeRangeBox extends Composite implements HasValidator, CustomFocusWidget {
	private final PaddedPanel panel = new PaddedPanel();
	private final FocusPanel focusPanel = new FocusPanel(panel);
	private final TimeBox startTimeBox;
	private final TimeBox endTimeBox;
	private final Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (!startTimeBox.getValidator().validate() || !endTimeBox.getValidator().validate()) {
				validator.setError(true);
			}

			if (getStartTime() != null || getEndTime() != null) {
				// one but not the other
				if (getStartTime() == null || getEndTime() == null) {
					validator.setError(true);
				} else {
					// wrong chronology
					if (getStartTime().after(getEndTime())) {
						validator.setError(true);
					}
				}
			} else {
				if (validator.isRequired()) {
					validator.setError(true);
				}
			}

		}
	});

	public TimeRangeBox() {
		startTimeBox = new TimeBox();
		endTimeBox = new TimeBox();
		init();
	}

	public TimeRangeBox(TimeIncrement timeIncrement) {
		startTimeBox = new TimeBox(timeIncrement);
		endTimeBox = new TimeBox(timeIncrement);
		init();
	}

	public void addChangeCommand(final Command command) {
		startTimeBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				command.execute();
			}
		});
		endTimeBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				command.execute();
			}
		});
	}

	public Date getEndTime() {
		return endTimeBox.getValue();
	}

	public TimeBox getEndTimeBox() {
		return endTimeBox;
	}

	public Date getStartTime() {
		return startTimeBox.getValue();
	}

	public TimeBox getStartTimeBox() {
		return startTimeBox;
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
		startTimeBox.setEnabled(enabled);
		endTimeBox.setEnabled(enabled);
	}

	public void setEndTime(Date endTime) {
		endTimeBox.setValue(endTime);
	}

	@Override
	public void setFocus(boolean focus) {
		focusPanel.setFocus(focus);
	}

	public void setRange(Date startTime, Date endTime) {
		setStartTime(startTime);
		setEndTime(endTime);
	}

	@Override
	public void setRequired(boolean required) {
		startTimeBox.getValidator().setRequired(required);
		endTimeBox.getValidator().setRequired(required);
		validator.setRequired(required);
	}

	public void setStartTime(Date startTime) {
		startTimeBox.setValue(startTime);
	}

	private void init() {
		panel.add(startTimeBox);
		panel.add(new Label("to"));
		panel.add(endTimeBox);

		initWidget(focusPanel);

		setRequired(false);
		startTimeBox.getValidator().useErrorBorder(false);
		endTimeBox.getValidator().useErrorBorder(false);
	}
}
