package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericRangeBox;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public class AgeGroupEditDialog extends EntityEditDialog<EventAgeGroup> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventAgeGroupCellTable table;

	public AgeGroupEditDialog(EventAgeGroupCellTable groupTable) {
		this.table = groupTable;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				eventService.saveAgeGroup(entity, new Callback<EventAgeGroup>() {
					@Override
					protected void doOnSuccess(EventAgeGroup result) {
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

		final NumericRangeBox ageInput = new NumericRangeBox();
		FormField ageField = form.createFormField("Age range:", ageInput, null);
		ageField.setInitializer(new Command() {
			@Override
			public void execute() {
				ageInput.setRange(entity.getMinimumAge(), entity.getMaximumAge());
			}
		});
		ageField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setMinimumAge((int) ageInput.getFromValue());
				entity.setMaximumAge((int) ageInput.getToValue());
			}
		});
		ft.addField(ageField);

		final NumericRangeBox participantsInput = new NumericRangeBox();
		FormField participantsField = form.createFormField("Min/max participants:", participantsInput, null);
		participantsField.setInitializer(new Command() {
			@Override
			public void execute() {
				participantsInput.setRange(entity.getMinimumParticipants(), entity.getMaximumParticipants());
			}
		});
		participantsField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setMinimumParticipants((int) participantsInput.getFromValue());
				entity.setMaximumParticipants((int) participantsInput.getToValue());
			}
		});
		ft.addField(participantsField);

		final NumericTextBox priceInput = new NumericTextBox(2);
		FormField priceField = form.createFormField("Price:", priceInput, null);
		priceField.setRequired(true);
		priceField.setInitializer(new Command() {
			@Override
			public void execute() {
				priceInput.setValue(entity.getPrice());
			}
		});
		priceField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setPrice(priceInput.getDouble());
			}
		});
		ft.addField(priceField);

		return ft;
	}

}
