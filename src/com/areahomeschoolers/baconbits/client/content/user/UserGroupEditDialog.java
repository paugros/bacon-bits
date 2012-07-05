package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserGroupEditDialog extends EntityEditDialog<UserGroup> {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private UserGroupCellTable table;

	public UserGroupEditDialog(UserGroupCellTable groupTable) {
		this.table = groupTable;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				userService.saveUserGroup(entity, new Callback<UserGroup>() {
					@Override
					protected void doOnSuccess(UserGroup result) {
						hide();
						table.addItem(result);
						table.refresh();
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();

		final RequiredTextBox nameInput = new RequiredTextBox();
		nameInput.setMaxLength(50);
		FormField nameField = form.createFormField("Name:", nameInput, null);
		nameField.setInitializer(new Command() {
			@Override
			public void execute() {
				nameInput.setText(entity.getGroupName());
			}
		});
		nameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setGroupName(nameInput.getText());
			}
		});
		ft.addField(nameField);

		final TextBox descriptionInput = new TextBox();
		descriptionInput.setMaxLength(100);
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
				entity.setDescription(descriptionInput.getText());
			}
		});
		ft.addField(descriptionField);

		final ValidatorDateBox startInput = new ValidatorDateBox();
		FormField startField = form.createFormField("Start date:", startInput, null);
		startField.setInitializer(new Command() {
			@Override
			public void execute() {
				startInput.setValue(entity.getStartDate());
			}
		});
		startField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setStartDate(startInput.getValue());
			}
		});
		ft.addField(startField);

		final ValidatorDateBox endInput = new ValidatorDateBox();
		FormField endField = form.createFormField("End date:", endInput, null);
		endField.setInitializer(new Command() {
			@Override
			public void execute() {
				endInput.setValue(entity.getEndDate());
			}
		});
		endField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setEndDate(endInput.getValue());
			}
		});
		ft.addField(endField);

		return ft;
	}

}
