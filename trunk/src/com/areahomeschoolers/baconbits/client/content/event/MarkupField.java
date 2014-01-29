package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.HasMarkup;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarkupField {
	private FormField markupField;

	public MarkupField(final HasMarkup markup) {

		final Label overrideDisplay = new Label();
		PaddedPanel inputPanel = new PaddedPanel();
		final DefaultListBox overrideInput = new DefaultListBox();
		overrideInput.addItem("Use default");
		overrideInput.addItem("Specify");

		inputPanel.add(overrideInput);

		VerticalPanel textBoxPanel = new VerticalPanel();
		inputPanel.add(textBoxPanel);

		PaddedPanel ppp = new PaddedPanel();
		textBoxPanel.add(ppp);
		final NumericTextBox percentInput = new NumericTextBox(2);
		percentInput.setMaxLength(5);
		percentInput.setVisibleLength(5);
		ppp.add(percentInput);
		ppp.add(new Label("percent"));

		PaddedPanel dpp = new PaddedPanel();
		textBoxPanel.add(dpp);
		final NumericTextBox dollarsInput = new NumericTextBox(2);
		dollarsInput.setMaxLength(5);
		dollarsInput.setVisibleLength(5);
		dpp.add(dollarsInput);
		dpp.add(new Label("dollars"));

		overrideInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				boolean override = overrideInput.getSelectedIndex() == 1;
				percentInput.setEnabled(override);
				dollarsInput.setEnabled(override);
			}
		});

		markupField = new FormField("Event markup:", inputPanel, overrideDisplay);
		markupField.setInitializer(new Command() {
			@Override
			public void execute() {
				overrideInput.setSelectedIndex(markup.getMarkupOverride() ? 1 : 0);
				if (markup.getMarkupOverride()) {
					percentInput.setValue(markup.getMarkupPercent());
					dollarsInput.setValue(markup.getMarkupDollars());
				} else {
					percentInput.setText("");
					dollarsInput.setText("");
				}

				overrideInput.fireEvent(new ChangeEvent() {
				});

				if (!markup.getMarkupOverride()) {
					overrideDisplay.setText("Use default");
				} else {
					String text = Formatter.formatNumber(markup.getMarkupPercent(), "0.00") + "%, plus $";
					text += Formatter.formatNumber(markup.getMarkupDollars(), "0.00");
					overrideDisplay.setText(text);
				}
			}
		});
		markupField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				markup.setMarkupOverride(overrideInput.getSelectedIndex() == 1);
				markup.setMarkupDollars(dollarsInput.getDouble());
				markup.setMarkupPercent(percentInput.getDouble());
			}
		});
	}

	public FormField getFormField() {
		return markupField;
	}

}
