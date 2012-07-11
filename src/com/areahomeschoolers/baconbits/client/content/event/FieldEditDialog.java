package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.EventField;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FieldEditDialog extends EntityEditDialog<EventField> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private List<Data> fieldTypes = new ArrayList<Data>();

	public FieldEditDialog(final Command completionCommand) {
		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				eventService.saveEventField(entity, new Callback<EventField>() {
					@Override
					protected void doOnSuccess(EventField result) {
						hide();
						completionCommand.execute();
					}
				});
			}
		});
	}

	public void setFieldTypes(List<Data> fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();
		ft.setWidth("600px");
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);

		final RequiredTextBox nameInput = new RequiredTextBox();
		nameInput.setMaxLength(100);
		FormField nameField = form.createFormField("Field name:", nameInput, null);
		nameField.setInitializer(new Command() {
			@Override
			public void execute() {
				nameInput.setText(entity.getName());
			}
		});
		nameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setName(nameInput.getText());
			}
		});
		ft.addField(nameField);

		final RequiredListBox typeInput = new RequiredListBox();
		for (Data item : fieldTypes) {
			typeInput.addItem(item.get("type"), item.getId());
		}
		FormField typeField = form.createFormField("Type:", typeInput, null);
		typeField.setInitializer(new Command() {
			@Override
			public void execute() {
				typeInput.setValue(entity.getTypeId());
			}
		});
		typeField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setTypeId(typeInput.getIntValue());
			}
		});
		ft.addField(typeField);

		final CheckBox requiredInput = new CheckBox("Required");
		FormField requiredField = form.createFormField("", requiredInput, null);
		requiredField.setInitializer(new Command() {
			@Override
			public void execute() {
				requiredInput.setValue(entity.getRequired());
			}
		});
		requiredField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setRequired(requiredInput.getValue());
			}
		});
		ft.addField(requiredField);

		final TextArea optionsInput = new TextArea();
		optionsInput.setVisibleLines(10);
		optionsInput.setCharacterWidth(35);
		final FormField optionsField = form.createFormField("Options:", optionsInput, null);
		optionsField.setInitializer(new Command() {
			@Override
			public void execute() {
				optionsInput.setText(entity.getOptions());
				optionsInput.setEnabled(typeInput.getIntValue() == 2 || typeInput.getIntValue() == 4);
			}
		});
		optionsField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setOptions(optionsInput.getText());
			}
		});
		ft.addField(optionsField);

		typeInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				optionsField.initialize();
			}
		});

		return ft;
	}

	@Override
	protected void setEntity(EventField entity) {
		super.setEntity(entity);

		if (entity.isSaved()) {
			setText("Edit Event Field");
		} else {
			setText("Add Event Field");
		}
	}
}
