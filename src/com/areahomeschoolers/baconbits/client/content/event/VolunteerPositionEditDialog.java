package com.areahomeschoolers.baconbits.client.content.event;

import java.util.List;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class VolunteerPositionEditDialog extends EntityEditDialog<EventVolunteerPosition> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public VolunteerPositionEditDialog(final List<EventVolunteerPosition> positions, final Command refreshCommand) {

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				eventService.saveVolunteerPosition(entity, new Callback<EventVolunteerPosition>() {
					@Override
					protected void doOnSuccess(EventVolunteerPosition result) {
						positions.remove(result);
						positions.add(result);
						refreshCommand.execute();
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();

		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.setMaxLength(50);
		FormField titleField = form.createFormField("Job title:", titleInput, null);
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleInput.setText(entity.getJobTitle());
			}
		});
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setJobTitle(titleInput.getText().trim());
			}
		});
		ft.addField(titleField);

		final TextBox descriptionInput = new TextBox();
		descriptionInput.setMaxLength(500);
		FormField descriptionField = form.createFormField("Description:", descriptionInput, null);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionInput.setText(entity.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setDescription(descriptionInput.getText().trim());
			}
		});
		ft.addField(descriptionField);

		final NumericTextBox discountInput = new NumericTextBox(2);
		FormField discountField = form.createFormField("Discount:", discountInput, null);
		discountField.setInitializer(new Command() {
			@Override
			public void execute() {
				discountInput.setValue(entity.getDiscount());
			}
		});
		discountField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setDiscount(discountInput.getDouble());
			}
		});
		ft.addField(discountField);

		final NumericTextBox countInput = new NumericTextBox();
		FormField countField = form.createFormField("Number needed:", countInput, null);
		countField.setRequired(true);
		countField.setInitializer(new Command() {
			@Override
			public void execute() {
				countInput.setValue(entity.getPositionCount());
			}
		});
		countField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setPositionCount(countInput.getInteger());
			}
		});
		ft.addField(countField);

		return ft;
	}

}
