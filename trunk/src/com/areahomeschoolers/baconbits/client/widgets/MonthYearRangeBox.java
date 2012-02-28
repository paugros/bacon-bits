package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class MonthYearRangeBox extends Composite implements HasValidator, CustomFocusWidget {

	private final PaddedPanel panel = new PaddedPanel();
	private final FocusPanel focusPanel = new FocusPanel(panel);
	private final MonthYearPicker startPicker = new MonthYearPicker();
	private final MonthYearPicker endPicker = new MonthYearPicker();
	private final List<Command> changeCommands = new ArrayList<Command>();

	private final Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			// Check for wrong chronology
			if (getStartDate().after(getEndDate())) {
				validator.setError(true);
			}
		}
	});

	public MonthYearRangeBox() {
		panel.add(startPicker);
		panel.add(new Label("to"));
		panel.add(endPicker);

		initWidget(focusPanel);

		startPicker.addValueChangeCommand(new Command() {
			@Override
			public void execute() {
				Date newStart = startPicker.getValue();
				if (newStart != null && endPicker.getValue() == null) {
					endPicker.setValue(newStart);
				}

				for (Command command : changeCommands) {
					command.execute();
				}
			}
		});

		endPicker.addValueChangeCommand(new Command() {
			@Override
			public void execute() {
				for (Command command : changeCommands) {
					command.execute();
				}
			}
		});
	}

	public void addValueChangeCommand(Command command) {
		changeCommands.add(command);
	}

	public void disallowFutureMonths() {
		startPicker.disallowFutureMonths();
		endPicker.disallowFutureMonths();
	}

	public Date getEndDate() {
		return endPicker.getValue();
	}

	public Date getStartDate() {
		return startPicker.getValue();
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
		startPicker.setEnabled(enabled);
		endPicker.setEnabled(enabled);
	}

	public void setEndDate(Date endDate) {
		endPicker.setValue(endDate);
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
		validator.setRequired(required);
	}

	public void setStartDate(Date startDate) {
		startPicker.setValue(startDate);
	}

	public void showPreviousMonths(int months) {
		startPicker.showPreviousMonths(months);
		endPicker.showPreviousMonths(months);
	}

	public void useCurrentMonth() {
		Date today = new Date();
		startPicker.setValue(today);
		endPicker.setValue(today);
	}
}
