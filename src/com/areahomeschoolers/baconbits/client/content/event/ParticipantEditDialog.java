package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistrationParticipant;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ParticipantEditDialog extends EntityEditDialog<EventRegistrationParticipant> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;
	private int dialogWidth = 600;
	private MaxHeightScrollPanel fieldsPanel;
	private EventRegistration registration;
	private List<EventField> eventFields;

	public ParticipantEditDialog(EventPageData pd) {
		setText("Register Attendee");
		pageData = pd;
		registration = pd.getRegistration();

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				if (!registration.isSaved()) {
					registration.setEventId(pageData.getEvent().getId());

					eventService.saveRegistration(registration, new Callback<ServerResponseData<EventRegistration>>() {
						@Override
						protected void doOnSuccess(ServerResponseData<EventRegistration> result) {
							registration = result.getData();
							saveParticipant();
						}
					});
				} else {
					saveParticipant();
				}
			}
		});
	}

	private void saveParticipant() {
		entity.setEventRegistrationId(registration.getId());
		entity.setEventFields(eventFields);

		eventService.saveParticipant(entity, new Callback<EventRegistrationParticipant>() {
			@Override
			protected void doOnSuccess(EventRegistrationParticipant result) {
				Application.reloadPage();
			}
		});
	}

	private void setFields(List<EventField> fields) {
		eventFields = fields;
		form.clearPartners();
		final EventFieldTable eventFieldsTable = new EventFieldTable(fields);
		form.addPartner(eventFieldsTable.getForm());
		eventFieldsTable.setWidth(dialogWidth + "px");
		fieldsPanel.setWidget(eventFieldsTable);
		centerDeferred();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth(dialogWidth + "px");
		FieldTable fieldTable = new FieldTable();

		final RequiredTextBox firstNameInput = new RequiredTextBox();
		firstNameInput.setMaxLength(50);
		FormField firstNameField = form.createFormField("First name:", firstNameInput, null);
		firstNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				firstNameInput.setText(entity.getFirstName());
			}
		});
		firstNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setFirstName(firstNameInput.getText());
			}
		});
		fieldTable.addField(firstNameField);

		final RequiredTextBox lastNameInput = new RequiredTextBox();
		lastNameInput.setMaxLength(50);
		FormField lastNameField = form.createFormField("Last name:", lastNameInput, null);
		lastNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				lastNameInput.setText(entity.getLastName());
			}
		});
		lastNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setLastName(lastNameInput.getText());
			}
		});
		fieldTable.addField(lastNameField);

		final NumericTextBox ageInput = new NumericTextBox();
		ageInput.setMaxLength(2);
		FormField ageField = form.createFormField("Age:", ageInput, null);
		ageField.setRequired(true);
		ageField.setInitializer(new Command() {
			@Override
			public void execute() {
				ageInput.setValue(entity.getAge());
			}
		});
		ageField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setAge(ageInput.getInteger());
			}
		});
		fieldTable.addField(ageField);

		final RequiredListBox ageGroupInput = new RequiredListBox();
		FormField ageGroupField = form.createFormField("Age group:", ageGroupInput, null);
		ageGroupField.setInitializer(new Command() {
			@Override
			public void execute() {
				ageGroupInput.setValue(entity.getAgeGroupId());
			}
		});
		ageGroupField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setAgeGroupId(ageGroupInput.getIntValue());
			}
		});
		fieldTable.addField(ageGroupField);

		fieldsPanel = new MaxHeightScrollPanel(200);

		for (EventAgeGroup a : pageData.getAgeGroups()) {
			ageGroupInput.addItem(a.getMinimumAge() + ((a.getMaximumAge() == 0) ? "+" : "-" + a.getMaximumAge()) + " yrs", a.getId());
		}

		ageGroupInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				eventService.getFields(new ArgMap<EventArg>(EventArg.AGE_GROUP_ID, ageGroupInput.getIntValue()), new Callback<ArrayList<EventField>>() {
					@Override
					protected void doOnSuccess(ArrayList<EventField> result) {
						setFields(result);
					}
				});
			}
		});

		if (entity.isSaved()) {
			eventService.getFields(new ArgMap<EventArg>(EventArg.REGISTRATION_PARTICIPANT_ID, entity.getId()), new Callback<ArrayList<EventField>>() {
				@Override
				protected void doOnSuccess(ArrayList<EventField> result) {
					setFields(result);
				}
			});
		}

		vp.add(fieldTable);
		vp.add(fieldsPanel);

		return vp;
	}

}
