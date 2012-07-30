package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MonthYearPicker extends Composite implements CustomFocusWidget, HasValidator {

	private final FocusPanel focusPanel = new FocusPanel();
	private final MonthPicker monthPicker = new MonthPicker();
	private final YearPicker yearPicker = new YearPicker();
	private final List<Command> changeCommands = new ArrayList<Command>();
	private Validator validator;

	private int earliestMonth = 0;
	private int earliestYear = 0;
	private int latestMonth = 0;
	private int latestYear = 0;
	private boolean required;

	public MonthYearPicker() {
		validator = new Validator(focusPanel, new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				validator.setError(required && getValue() == null);
			}
		});

		HorizontalPanel hp = new HorizontalPanel();

		hp.add(monthPicker);
		hp.add(yearPicker);

		focusPanel.setWidget(hp);
		initWidget(focusPanel);

		yearPicker.getListBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				for (Command command : changeCommands) {
					command.execute();
				}
			}
		});

		monthPicker.addValueChangeCommand(new Command() {
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

	/**
	 * This sets a maximum date for the picker. This logic should only be called once on this picker.
	 * 
	 * @param year
	 * @param month
	 */
	public void disallowFutureMonths() {
		latestYear = ClientDateUtils.getYear(new Date());
		latestMonth = ClientDateUtils.getMonth(new Date());
		setAllowedMonths();
	}

	public void fireValueChangeCommands() {
		for (Command command : changeCommands) {
			command.execute();
		}
	}

	public int getMonth() {
		return monthPicker.getMonth();
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	// @Override
	public Date getValue() {
		if (yearPicker.getYear() == 0 || monthPicker.getMonth() == 0) {
			return null;
		}

		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy/M/d");
		String dateStr = yearPicker.getYear() + "/" + monthPicker.getMonth() + "/1";

		return dtf.parse(dateStr);
	}

	public int getYear() {
		return yearPicker.getYear();
	}

	public YearPicker getYearPicker() {
		return yearPicker;
	}

	@Override
	public boolean isRequired() {
		return required;
	}

	/**
	 * This sets a minimum date for the picker. This logic should only be called once on this picker.
	 * 
	 * @param year
	 * @param month
	 */
	public void setEarliestMonth(int year, int month) {
		earliestYear = year;
		earliestMonth = month;

		setAllowedMonths();

		yearPicker.setStartYear(earliestYear);
	}

	@Override
	public void setEnabled(boolean enabled) {
		monthPicker.setEnabled(enabled);
		yearPicker.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focus) {
		focusPanel.setFocus(focus);
	}

	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setValue(Date value) {
		monthPicker.setMonth(value);
		yearPicker.setYear(value);
	}

	/**
	 * This has the picker show the provided number previous months and the current month. 1 will show last month and this month, 2 will show last two months
	 * and this month, etc.
	 */
	public void showPreviousMonths(int months) {
		Date today = new Date();
		int year = ClientDateUtils.getYear(today);
		int month = ClientDateUtils.getMonth(today);

		for (int i = 0; i < months; i++) {
			month--;

			if (month == 0) {
				month = 12;
				year--;
			}
		}

		setEarliestMonth(year, month);
	}

	private void setAllowedMonths() {
		int firstMonth = 1;
		int lastMonth = 12;
		int selectedYear = yearPicker.getYear();

		if (selectedYear == earliestYear) {
			firstMonth = earliestMonth;
		}

		if (selectedYear == latestYear) {
			lastMonth = latestMonth;
		}

		monthPicker.setMonthRange(firstMonth, lastMonth);
	}
}
