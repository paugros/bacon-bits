package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.FormField.ButtonPlacement;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.EventField;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
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
		Command initializer = null, dtoUpdater = null;

		switch (field.getTypeId()) {
		case 1:
			if (field.getRequired()) {
				inputWidget = new RequiredTextBox();
			} else {
				inputWidget = new TextBox();
			}

			final TextBox tb = (TextBox) inputWidget;
			initializer = new Command() {
				@Override
				public void execute() {
					tb.setText(field.getValue());
				}
			};

			dtoUpdater = new Command() {
				@Override
				public void execute() {
					field.setValue(tb.getText());
				}
			};
			break;
		case 2:
			if (field.getRequired()) {
				inputWidget = new RequiredListBox();
			} else {
				inputWidget = new DefaultListBox();
			}

			String[] listOptions = field.getOptions().split("\n");
			final DefaultListBox lb = (DefaultListBox) inputWidget;

			initializer = new Command() {
				@Override
				public void execute() {
					lb.setValue(field.getValue());
				}
			};
			dtoUpdater = new Command() {
				@Override
				public void execute() {
					field.setValue(lb.getValue());
				}
			};

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

			final TextArea ta = (TextArea) inputWidget;
			initializer = new Command() {
				@Override
				public void execute() {
					ta.setText(field.getValue());
				}
			};
			dtoUpdater = new Command() {
				@Override
				public void execute() {
					field.setValue(ta.getText());
				}
			};
			break;
		case 4:
			VerticalPanel p = new VerticalPanel();
			p.setSpacing(3);

			String[] checkOptions = field.getOptions().split("\n");

			final List<CheckBox> options = new ArrayList<CheckBox>();
			for (int i = 0; i < checkOptions.length; i++) {
				CheckBox cb = new CheckBox(checkOptions[i]);
				options.add(cb);
				p.add(cb);
			}

			initializer = new Command() {
				@Override
				public void execute() {
					for (CheckBox c : options) {
						String test = Common.getDefaultIfNull(field.getValue(), "");
						c.setValue(test.contains(c.getText() + "\n"));
					}
				}
			};

			dtoUpdater = new Command() {
				@Override
				public void execute() {
					String value = "";
					for (CheckBox c : options) {
						if (c.getValue()) {
							value += c.getText() + "\n";
						}
					}

					field.setValue(value);
				}
			};

			inputWidget = p;
			break;
		case 5:
			final NumericTextBox t = new NumericTextBox();
			t.setRequired(field.getRequired());
			inputWidget = t;

			initializer = new Command() {
				@Override
				public void execute() {
					t.setText(field.getValue());
				}
			};

			dtoUpdater = new Command() {
				@Override
				public void execute() {
					field.setValue(t.getText());
				}
			};
			break;
		default:
			break;
		}

		Label displayWidget = new Label();

		formField = new FormField(field.getName(), inputWidget, displayWidget);
		formField.setInitializer(initializer);
		formField.setDtoUpdater(dtoUpdater);

		if (field.getTypeId() == 4) {
			formField.setButtonPlacement(ButtonPlacement.BOTTOM);
		}

		formField.configureForAdd();
	}
}
