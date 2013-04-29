package com.areahomeschoolers.baconbits.client.content.payments;

import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AdjustmentDialog extends EntityEditDialog<Adjustment> {

	public AdjustmentDialog() {

	}

	@Override
	public void center(Adjustment a) {
		if (a.isSaved()) {
			setText("Edit Adjustment");
		} else {
			setText("Add Adjustment");
		}
		super.center(a);
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();

		ft.addField("", "Enter negative values to reduce the user's next payment, and positive values to increase it.");

		final NumericTextBox amountInput = new NumericTextBox(2);
		amountInput.setAllowNegatives(true);
		amountInput.setRequired(true);
		FormField amountField = form.createFormField("Amount:", amountInput, null);
		amountField.setInitializer(new Command() {
			@Override
			public void execute() {
				amountInput.setValue(entity.getAmount());
			}
		});
		amountField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setAmount(amountInput.getDouble());
			}
		});
		ft.addField(amountField);

		final TextBox notesInput = new TextBox();
		notesInput.setMaxLength(100);
		notesInput.setVisibleLength(35);
		FormField notesField = form.createFormField("Notes:", notesInput, null);
		notesField.setInitializer(new Command() {
			@Override
			public void execute() {
				notesInput.setText(entity.getDescription());
			}
		});
		notesField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setDescription(notesInput.getText());
			}
		});
		ft.addField(notesField);

		return ft;
	}

}
