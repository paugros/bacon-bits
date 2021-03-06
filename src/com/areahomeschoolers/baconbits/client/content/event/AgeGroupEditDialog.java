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
import com.areahomeschoolers.baconbits.client.widgets.MarkupTextBox;
import com.areahomeschoolers.baconbits.client.widgets.NumericRangeBox;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public class AgeGroupEditDialog extends EntityEditDialog<EventAgeGroup> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private Event event;

	public AgeGroupEditDialog(final List<EventAgeGroup> ageGroups, Event e, final Command refreshCommand) {
		this.event = e;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				eventService.saveAgeGroup(entity, event, new Callback<EventAgeGroup>() {
					@Override
					protected void doOnSuccess(EventAgeGroup result) {
						ageGroups.remove(result);
						ageGroups.add(result);
						refreshCommand.execute();
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();

		final NumericRangeBox ageInput = new NumericRangeBox();
		ageInput.setAllowZeroForNoLimit(true);
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
				entity.setMinimumAge(ageInput.getFromValue());
				entity.setMaximumAge(ageInput.getToValue());
			}
		});
		ft.addField(ageField);

		final NumericRangeBox participantsInput = new NumericRangeBox();
		participantsInput.setAllowZeroForNoLimit(true);
		FormField participantsField = form.createFormField("Min / max participants:", participantsInput, null);
		participantsField.setInitializer(new Command() {
			@Override
			public void execute() {
				participantsInput.setRange(entity.getMinimumParticipants(), entity.getMaximumParticipants());
			}
		});
		participantsField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setMinimumParticipants(participantsInput.getFromValue());
				entity.setMaximumParticipants(participantsInput.getToValue());
			}
		});
		ft.addField(participantsField);

		final MarkupTextBox priceInput = new MarkupTextBox(event);
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
