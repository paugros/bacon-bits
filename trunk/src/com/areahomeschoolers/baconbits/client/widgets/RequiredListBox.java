package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class RequiredListBox extends DefaultListBox implements HasValidator {

	protected boolean isRequired = true;
	private final String defaultItemText;
	private final int defaultMinItemWidth;

	private final Validator validator = new Validator(this, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (getSelectedIndex() == 0) {
				validator.setError(true);
			}
		}
	});

	public RequiredListBox() {
		this("", 100);
	}

	public RequiredListBox(String defaultItemText, int defaultMinItemWidth) {
		super();
		this.defaultItemText = defaultItemText;
		this.defaultMinItemWidth = defaultMinItemWidth;
		addDefaultItem();
		validator.setRequired(true);
		addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (getSelectedIndex() != 0) {
					validator.setError(false);
				}
			}
		});
	}

	@Override
	public void clear() {
		super.clear();
		addDefaultItem();
		validator.setError(false);
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public boolean isRequired() {
		return isRequired;
	}

	@Override
	public void setRequired(boolean required) {
		// this method isn't called much, if at all. ideally, it would be rewritten to always remove the "special" option, then add the appropriate one in its
		// place:
		// 1. if required, add an option with the -999999999 value (to avoid collisions)
		// 2. if not required, add an option with an empty string value
		if (isRequired == required) {
			return;
		}

		isRequired = required;

		if (isRequired) {
			addDefaultItem();
		} else {
			removeItem(0);
		}

		getValidator().setEnabled(isRequired);
		getValidator().setRequired(isRequired);
	}

	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
		validator.setError(false);
	}

	private void addDefaultItem() {
		insertItem(defaultItemText, "-999999999", 0);
		OptionElement option = (OptionElement) getElement().getFirstChild();
		option.getStyle().setWidth(defaultMinItemWidth, Unit.PX);
	}
}
