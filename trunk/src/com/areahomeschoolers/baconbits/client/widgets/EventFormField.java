package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.FormField.ButtonPlacement;
import com.areahomeschoolers.baconbits.shared.dto.EventField;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventFormField {
	private FormField formField;
	private EventField field;

	public EventFormField(EventField field) {
		this.field = field;

		createFormField();
	}

	public FormField getFormField() {
		return formField;
	}

	private void createFormField() {
		Widget inputWidget = new Label();

		switch (field.getTypeId()) {
		case 1:
			if (field.getRequired()) {
				inputWidget = new RequiredTextBox();
			} else {
				inputWidget = new TextBox();
			}
			break;
		case 2:
			if (field.getRequired()) {
				inputWidget = new RequiredListBox();
			} else {
				inputWidget = new DefaultListBox();
			}

			String[] listOptions = field.getOptions().split("\n");
			ListBox lb = (ListBox) inputWidget;

			for (int i = 0; i < listOptions.length; i++) {
				lb.addItem(listOptions[i]);
			}

			break;
		case 3:
			if (field.getRequired()) {
				inputWidget = new RequiredTextArea();
			} else {
				inputWidget = WidgetFactory.createStandardTextArea();
			}
			break;
		case 4:
			VerticalPanel p = new VerticalPanel();
			p.setSpacing(3);

			String[] checkOptions = field.getOptions().split("\n");

			for (int i = 0; i < checkOptions.length; i++) {
				p.add(new CheckBox(checkOptions[i]));
			}

			inputWidget = p;
			break;
		case 5:
			NumericTextBox t = new NumericTextBox();
			t.setRequired(field.getRequired());
			inputWidget = t;
			break;
		default:
			break;
		}

		Label displayWidget = new Label();

		formField = new FormField(field.getName(), inputWidget, displayWidget);

		if (field.getTypeId() == 4) {
			formField.setButtonPlacement(ButtonPlacement.BOTTOM);
		}

		if (field.getValueId() == 0) {
			formField.configureForAdd();
		}
	}
}
