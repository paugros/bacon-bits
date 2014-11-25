package com.areahomeschoolers.baconbits.client.content.tag;

import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TagEditDialog extends EntityEditDialog<Tag> {

	public TagEditDialog() {

	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();
		ft.setLabelColumnWidth(LabelColumnWidth.NONE);

		final TextBox nameInput = new TextBox();
		nameInput.setMaxLength(25);
		nameInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					form.submit();
				}
			}
		});
		FormField nameField = form.createFormField("Name:", nameInput);
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

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				nameInput.setFocus(true);
				if (!Common.isNullOrBlank(entity.getName())) {
					nameInput.setCursorPos(entity.getName().length());
				}
			}
		});

		return ft;
	}

}
