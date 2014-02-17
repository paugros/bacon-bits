package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MaxLengthTextArea extends Composite implements HasValue<String>, HasText, HasValidator, HasKeyDownHandlers, Focusable, CustomFocusWidget {
	private final RequiredTextArea textArea = new RequiredTextArea();
	private final VerticalPanel panel = new VerticalPanel();
	private final Label statusLabel = new Label();
	private int maxLength;

	public MaxLengthTextArea(int max) {
		if (max < 1) {
			new RuntimeException("Max length must be greater than zero.");
		}
		setMaxLength(max);
		setRequired(false);

		textArea.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int charCount = textArea.getText().length();
				if (charCount >= maxLength) {
					Character currentCharacter = (char) event.getNativeEvent().getKeyCode();

					if (!ClientUtils.ALLOWED_KEY_CODES.contains(currentCharacter)) {
						event.preventDefault();
					}
				}
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						enforceMaxLength();
					}
				});
			}
		});
		textArea.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				enforceMaxLength();
			}
		});

		textArea.fireEvent(new KeyUpEvent() {
		});
		panel.add(textArea);
		panel.add(statusLabel);
		initWidget(panel);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return textArea.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return textArea.addValueChangeHandler(handler);
	}

	@Override
	public int getTabIndex() {
		return textArea.getTabIndex();
	}

	@Override
	public String getText() {
		return textArea.getText();
	}

	public RequiredTextArea getTextArea() {
		return textArea;
	}

	@Override
	public Validator getValidator() {
		return textArea.getValidator();
	}

	@Override
	public String getValue() {
		return textArea.getValue();
	}

	public boolean isEnabled() {
		return textArea.isEnabled();
	}

	@Override
	public boolean isRequired() {
		return textArea.isRequired();
	}

	@Override
	public void setAccessKey(char key) {
		textArea.setAccessKey(key);
	}

	public void setCharacterWidth(int width) {
		textArea.setCharacterWidth(width);
	}

	@Override
	public void setEnabled(boolean enabled) {
		textArea.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focused) {
		textArea.setFocus(focused);
	}

	@Override
	public void setHeight(String height) {
		textArea.setHeight(height);
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void setRequired(boolean required) {
		textArea.setRequired(required);
	}

	@Override
	public void setTabIndex(int index) {
		textArea.setTabIndex(index);
	}

	@Override
	public void setText(String text) {
		textArea.setText(text);
		enforceMaxLength();
	}

	@Override
	public void setValue(String value) {
		textArea.setValue(value);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		textArea.setValue(value, fireEvents);
	}

	public void setVisibleLines(int lines) {
		textArea.setVisibleLines(lines);
	}

	private void enforceMaxLength() {
		int charCount = textArea.getText().length();
		if (charCount > maxLength) {
			textArea.setText(textArea.getText().substring(0, maxLength));
			charCount = maxLength;
		}
		statusLabel.setText(charCount + "/" + maxLength);
	}
}
