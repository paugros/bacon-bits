package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DateTimeBox extends Composite implements HasValue<Date>, HasValidator, CustomFocusWidget {
	private FocusPanel focusPanel = new FocusPanel();
	private HorizontalPanel boxPanel = new HorizontalPanel();
	private ValidatorDateBox dateBox = new ValidatorDateBox();
	private TimeBox timeBox = new TimeBox();
	private boolean timeValidationEnabled = true;

	private Validator validator = new Validator(focusPanel, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			// if we are not required
			if (!validator.isRequired() && (dateBox.getValue() == null || timeBox.getValue() == null)) {
				return;
			}

			// if either member is in error, we have an error
			if (dateBox.hasErrors() || (timeBox.hasErrors() && timeValidationEnabled)) {
				validator.setError(true);
				return;
			}

			// don't allow times without dates
			if (dateBox.getValue() == null && timeBox.getValue() != null) {
				validator.setError(true);
				return;
			}

			if (timeValidationEnabled) {
				// or dates without times
				if (timeBox.getValue() == null && dateBox.getValue() != null) {
					validator.setError(true);
					return;
				}
			}
		}
	});

	public DateTimeBox() {
		// need to re-investigate blur-based validation of child widgets. Doesn't work b/c moving from one to another causes unnecessary errors.

		dateBox.getValidator().removeFromTarget();
		timeBox.getValidator().removeFromTarget();
		// dateBox.getTextBox().addBlurHandler(new BlurHandler() {
		// @Override
		// public void onBlur(BlurEvent event) {
		// validator.validate();
		// }
		// });
		// timeBox.getTextBox().addBlurHandler(new BlurHandler() {
		// @Override
		// public void onBlur(BlurEvent event) {
		// validator.validate();
		// }
		// });

		focusPanel.setWidget(boxPanel);
		boxPanel.add(dateBox);
		boxPanel.add(timeBox);

		initWidget(focusPanel);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		return dateBox.addHandler(handler, ValueChangeEvent.getType());
	}

	public void enableTimeValidation(boolean enable) {
		timeValidationEnabled = enable;
	}

	public FocusPanel getFocusPanel() {
		return focusPanel;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public Date getValue() {
		Date dateValue = dateBox.getValue();
		Date timeValue = timeBox.getValue();
		String timeString;
		if (timeValue == null) {
			timeString = "12:00 AM";
		} else {
			timeString = Formatter.formatTime(timeValue);
		}

		String compiledDate = Formatter.formatDate(dateValue) + " " + timeString;

		try {
			return Formatter.DEFAULT_DATE_TIME_FORMAT.parse(compiledDate);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	public boolean isTimeValidationEnabled() {
		return timeValidationEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		dateBox.setEnabled(enabled);
		timeBox.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focus) {
		focusPanel.setFocus(focus);
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}

	public void setTimeEnabled(boolean enabled) {
		timeBox.setEnabled(enabled);
		timeBox.setValue(null);
	}

	public void setTimeValidationEnabled(boolean timeValidationEnabled) {
		this.timeValidationEnabled = timeValidationEnabled;
	}

	@Override
	public void setValue(Date value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Date value, boolean fireEvents) {
		dateBox.setValue(value);
		if (timeBox.isEnabled()) {
			timeBox.setValue(value);
		}

		if (fireEvents) {
			fireEvent(new ValueChangeEvent<Date>(value) {
			});
		}
	}

	public void syncDateWith(final DateTimeBox dateTimeBox) {
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date newStart = event.getValue();
				if (newStart != null && dateTimeBox.dateBox.getValue() == null) {
					dateTimeBox.dateBox.setValue(newStart, true);
				}
			}
		});
	}
}
