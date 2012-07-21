package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TimeBox extends Composite implements HasValue<Date>, HasValidator, CustomFocusWidget {

	public enum TimeIncrement {
		HOUR, HALF_HOUR
	}

	private TextBox textBox = new TextBox();
	private PopupPanel popup = new PopupPanel(true);
	private ScrollPanel scrollPanel = new ScrollPanel();
	private VerticalPanel optionsPanel = new VerticalPanel();
	private Label selectedOption;
	private boolean freezeMouseOverEvents = false;
	private Date setDate; // This stores the day, month, year data of the date set in setValue

	private MouseOverHandler optionOver = new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			if (freezeMouseOverEvents) {
				return;
			}
			Label option = (Label) event.getSource();
			option.addStyleName("selectedWidget");
			selectOption(option);
		}
	};

	private ClickHandler optionClick = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Label option = (Label) event.getSource();
			textBox.setValue(option.getText(), true);
			validator.validate();
			popup.hide();
		}
	};

	private Validator validator = new Validator(textBox, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			validator.setError(hasErrors());
		}
	});

	public TimeBox() {
		this(TimeIncrement.HALF_HOUR);
	}

	public TimeBox(TimeIncrement timeIncrement) {
		validator.setRequired(true);
		textBox.setVisibleLength(7);
		textBox.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				initializePopup();
			}
		});
		textBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				initializePopup();
			}
		});

		textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					selectedOption.fireEvent(new ClickEvent() {
					});
					break;
				case KeyCodes.KEY_ESCAPE:
					popup.hide();
					break;
				case KeyCodes.KEY_DOWN:
					moveSelection(1);
					break;
				case KeyCodes.KEY_UP:
					moveSelection(-1);
					break;
				case KeyCodes.KEY_PAGEDOWN:
					moveSelection(7);
					break;
				case KeyCodes.KEY_PAGEUP:
					moveSelection(-7);
					break;
				case KeyCodes.KEY_HOME:
					moveSelection(-optionsPanel.getWidgetIndex(selectedOption));
					break;
				case KeyCodes.KEY_END:
					moveSelection((optionsPanel.getWidgetCount() - 1) - optionsPanel.getWidgetIndex(selectedOption));
					break;
				case KeyCodes.KEY_TAB:
					popup.hide();
					break;
				}
			}
		});

		popup.getElement().getStyle().setPadding(0, Unit.PX);
		scrollPanel.setWidget(optionsPanel);
		popup.setWidget(scrollPanel);
		scrollPanel.setHeight("140px");

		scrollPanel.setWidth("102px");

		for (int i = 0; i < 24; i++) {
			int hour = i;
			String ampm;
			if (hour >= 12) {
				if (hour > 12) {
					hour -= 12;
				}
				ampm = "PM";
			} else {
				ampm = "AM";
			}
			if (hour == 0) {
				hour = 12;
			}

			addOption(hour + ":00 " + ampm);
			if (timeIncrement == TimeIncrement.HALF_HOUR) {
				addOption(hour + ":30 " + ampm);
			}
		}

		initWidget(textBox);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public TextBox getTextBox() {
		return textBox;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public Date getValue() {
		Date date = null;
		try {
			Date tempSetDate = setDate;
			if (tempSetDate == null) {
				tempSetDate = new Date(0);
			}
			date = DateTimeFormat.getFormat("M/d/yyyy h:mm a").parse(Formatter.formatDate(tempSetDate, "M/d/yyyy") + " " + textBox.getText().trim());
			setValue(date);
		} catch (Exception e) {
		}

		return date;
	}

	public boolean hasErrors() {
		String textValue = textBox.getValue().trim();
		if (validator.isRequired() && textValue.isEmpty()) {
			return true;
		}

		if (!textValue.isEmpty() && getValue() == null) {
			return true;
		}

		return false;
	}

	public boolean isEnabled() {
		return textBox.isEnabled();
	}

	@Override
	public boolean isRequired() {
		return validator.isRequired();
	}

	@Override
	public void setEnabled(boolean enabled) {
		textBox.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focus) {
		textBox.setFocus(focus);
	}

	@Override
	public void setRequired(boolean required) {
		validator.setRequired(required);
	}

	@Override
	public void setValue(Date value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Date value, boolean fireEvents) {
		String time = Formatter.formatTime(value);
		if (!time.equals("12:00 AM")) {
			textBox.setValue(time);
		}
		setDate = value;

		if (fireEvents) {
			fireEvent(new ValueChangeEvent<Date>(value) {
			});
		}
	}

	private void addOption(String optionValue) {
		Label option = new Label(optionValue);
		option.addStyleName("timeBoxOption");
		option.addClickHandler(optionClick);
		option.addMouseOverHandler(optionOver);
		optionsPanel.add(option);
	}

	private void initializePopup() {
		if (popup.isShowing()) {
			return;
		}
		popup.setPopupPosition(textBox.getAbsoluteLeft(), textBox.getAbsoluteTop() + textBox.getOffsetHeight());
		popup.show();
		boolean foundIt = false;
		for (int i = 0; i < optionsPanel.getWidgetCount(); i++) {
			Widget w = optionsPanel.getWidget(i);
			Label option = (Label) w;
			if (textBox.getText().equals(option.getText())) {
				scrollPanel.setVerticalScrollPosition((i - 3) * option.getOffsetHeight());
				selectOption(option);
				foundIt = true;
			}
		}

		if (!foundIt) {
			Label option = (Label) optionsPanel.getWidget(27);
			scrollPanel.setVerticalScrollPosition(option.getOffsetHeight() * 24);
			selectOption(option);
		}
	}

	private void moveSelection(int steps) {
		if (selectedOption == null) {
			selectOption((Label) optionsPanel.getWidget(0));
		}
		int currentIndex = optionsPanel.getWidgetIndex(selectedOption);
		int newIndex = currentIndex + steps;
		freezeMouseOverEvents = true;
		if (newIndex > optionsPanel.getWidgetCount() - 1) {
			newIndex = 0;
			scrollPanel.scrollToTop();
		} else if (newIndex < 0) {
			newIndex = optionsPanel.getWidgetCount() - 1;
			scrollPanel.scrollToBottom();
		} else {
			int height = selectedOption.getOffsetHeight();
			int scrollTop = scrollPanel.getVerticalScrollPosition() + 1;
			int scrollBottom = scrollTop + scrollPanel.getOffsetHeight();
			int selectionLocation = height * (newIndex + 1);
			if (selectionLocation < scrollTop || selectionLocation > scrollBottom) {
				scrollPanel.setVerticalScrollPosition(scrollPanel.getVerticalScrollPosition() + (height * steps));
			}
		}
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				freezeMouseOverEvents = false;
			}
		});

		Label option = (Label) optionsPanel.getWidget(newIndex);
		selectOption(option);
	}

	private void selectOption(Label option) {
		if (selectedOption != null) {
			selectedOption.removeStyleName("selectedWidget");
		}
		option.addStyleName("selectedWidget");
		selectedOption = option;
	}
}
