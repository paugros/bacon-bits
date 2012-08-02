package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.MonthYearPicker;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ParticipantEditDialog extends EntityEditDialog<EventParticipant> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;
	private int dialogWidth = 600;
	private MaxHeightScrollPanel fieldsPanel;
	private EventRegistration registration;
	private List<EventField> eventFields;
	private ParameterHandler<EventRegistration> refreshCommand;
	private FieldTable fieldTable;
	private MonthYearPicker birthDateInput;
	private List<User> children;

	public ParticipantEditDialog(EventPageData pd, ParameterHandler<EventRegistration> refreshCommand) {
		setText("Register Participant");
		pageData = pd;
		registration = pd.getRegistration();
		this.refreshCommand = refreshCommand;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				if (!registration.isSaved()) {
					registration.setEventId(pageData.getEvent().getId());

					eventService.saveRegistration(registration, new Callback<EventRegistration>() {
						@Override
						protected void doOnSuccess(EventRegistration result) {
							registration = result;
							pageData.setRegistration(result);
							saveParticipant();
						}
					});
				} else {
					saveParticipant();
				}
			}
		});
	}

	@Override
	public void show() {
		super.show();
		form.getSubmitButton().setEnabled(pageData.getEvent().allowRegistrations());
	}

	private void insertChildListBox(List<User> children) {
		final DefaultListBox childInput = new DefaultListBox();
		childInput.addItem("", 0);
		final Map<Integer, User> childMap = new HashMap<Integer, User>();
		for (User u : children) {
			childInput.addItem(u.getFullName(), u.getId());
			childMap.put(u.getId(), u);
		}
		childInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				User u = childMap.get(childInput.getIntValue());
				if (u == null) {
					u = new User();
				}

				entity.setUser(u);
				entity.setFirstName(u.getFirstName());
				entity.setLastName(u.getLastName());
				entity.setBirthDate(u.getBirthDate());
				entity.setUserId(u.getId());

				form.initialize();
				birthDateInput.fireValueChangeCommands();
			}
		});
		FormField childField = form.createFormField("Quick-select:", childInput, null);
		childField.setInitializer(new Command() {
			@Override
			public void execute() {
				childInput.setValue(entity.getUserId());
			}
		});
		childField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setUserId(childInput.getIntValue());
			}
		});

		if (childInput.getItemCount() > 1) {
			fieldTable.addField(childField, 0);
		}
	}

	private void saveParticipant() {
		if (!Common.isNullOrEmpty(children)) {
			String name = Common.stripWhiteSpace(entity.getFirstName() + entity.getLastName()).toLowerCase();
			for (User u : children) {
				String testName = Common.stripWhiteSpace(u.getFirstName() + u.getLastName()).toLowerCase();
				if (name != null && testName != null && name.equals(testName)) {
					entity.setUser(u);
					entity.setUserId(u.getId());
				}
			}
		}

		entity.setEventRegistrationId(registration.getId());
		entity.setEventFields(eventFields);

		eventService.saveParticipant(entity, new Callback<ArrayList<EventParticipant>>() {
			@Override
			protected void doOnSuccess(ArrayList<EventParticipant> result) {
				registration.setParticipants(result);
				refreshCommand.execute(registration);
			}
		});
	}

	private void setFields(List<EventField> fields) {
		eventFields = fields;
		form.clearPartners();
		if (fields.isEmpty()) {
			fieldsPanel.clear();
		} else {
			final EventFieldTable eventFieldsTable = new EventFieldTable(fields);
			if (entity.isSaved() && entity.getStatusId() != 1) {
				eventFieldsTable.getForm().setEnabled(false);
			}
			form.addPartner(eventFieldsTable.getForm());
			eventFieldsTable.setWidth(dialogWidth + "px");
			fieldsPanel.setWidget(eventFieldsTable);
		}
		centerDeferred();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth(dialogWidth + "px");
		fieldTable = new FieldTable();

		if (!entity.isSaved()) {
			UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
			ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
			args.put(UserArg.PARENT_ID_PLUS_SELF, Application.getCurrentUser().getId());
			args.put(UserArg.NOT_ON_REGISTRATION_ID, registration.getId());
			userService.list(args, new Callback<ArrayList<User>>() {
				@Override
				protected void doOnSuccess(ArrayList<User> result) {
					if (!result.isEmpty()) {
						insertChildListBox(result);
						children = result;
					}
				}
			});
		}

		final boolean canEditUser = !entity.isSaved();
		final Label firstNameDisplay = new Label();
		final RequiredTextBox firstNameInput = new RequiredTextBox();
		firstNameInput.setMaxLength(50);
		final FormField firstNameField = form.createFormField("First name:", firstNameInput, firstNameDisplay);
		firstNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				firstNameDisplay.setText(entity.getFirstName());
				firstNameInput.setText(entity.getFirstName());
				firstNameField.setEnabled(canEditUser);
			}
		});
		firstNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setFirstName(firstNameInput.getText());
			}
		});
		fieldTable.addField(firstNameField);

		final Label lastNameDisplay = new Label();
		final RequiredTextBox lastNameInput = new RequiredTextBox();
		lastNameInput.setMaxLength(50);
		final FormField lastNameField = form.createFormField("Last name:", lastNameInput, lastNameDisplay);
		lastNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				lastNameDisplay.setText(entity.getLastName());
				lastNameInput.setText(entity.getLastName());
				lastNameField.setEnabled(canEditUser);
			}
		});
		lastNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setLastName(lastNameInput.getText());
			}
		});
		fieldTable.addField(lastNameField);

		fieldsPanel = new MaxHeightScrollPanel(200);
		if (entity.isSaved()) {
			eventService.getFields(new ArgMap<EventArg>(EventArg.PARTICIPANT_ID, entity.getId()), new Callback<ArrayList<EventField>>() {
				@Override
				protected void doOnSuccess(ArrayList<EventField> result) {
					setFields(result);
				}
			});
		} else if (pageData.getAgeGroups().isEmpty()) {
			eventService.getFields(new ArgMap<EventArg>(EventArg.EVENT_ID, pageData.getEvent().getId()), new Callback<ArrayList<EventField>>() {
				@Override
				protected void doOnSuccess(ArrayList<EventField> result) {
					setFields(result);
				}
			});
		}

		birthDateInput = new MonthYearPicker();
		birthDateInput.setEarliestMonth(1995, 1);
		birthDateInput.getYearPicker().getListBox().insertItem("Adult", "1994", 1);

		if (!Common.isNullOrEmpty(pageData.getAgeGroups())) {
			birthDateInput.addValueChangeCommand(new Command() {
				@Override
				public void execute() {
					Date d = birthDateInput.getValue();
					if (d != null) {
						int age = (int) (ClientDateUtils.daysBetween(d, new Date()) / 365);

						for (EventAgeGroup g : pageData.getAgeGroups()) {
							if (age >= g.getMinimumAge() && (age <= g.getMaximumAge() || (g.getMaximumAge() == 0))) {
								entity.setAgeGroupId(g.getId());
								eventService.getFields(new ArgMap<EventArg>(EventArg.AGE_GROUP_ID, g.getId()), new Callback<ArrayList<EventField>>() {
									@Override
									protected void doOnSuccess(ArrayList<EventField> result) {
										setFields(result);
									}
								});
								break;
							}
						}
					}
				}
			});

			form.addFormValidatorCommand(new ValidatorCommand() {
				@Override
				public void validate(Validator validator) {
					if (entity.getAgeGroupId() == null || entity.getAgeGroupId() == 0) {
						validator.setError(true);
						validator.setErrorMessage("There is no age group that matches this participant's age.");
					} else {
						validator.setError(false);
					}
				}
			});
		}

		final Label birthDateDisplay = new Label();
		final FormField birthDateField = form.createFormField("Birth month / year:", birthDateInput, birthDateDisplay);
		birthDateField.setRequired(true);
		birthDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				if (entity.getBirthDate() != null) {
					birthDateDisplay.setText(DateTimeFormat.getFormat("M/yyyy").format(entity.getBirthDate()));
				}
				birthDateInput.setValue(entity.getBirthDate());
				birthDateField.setEnabled(canEditUser);
			}
		});
		birthDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setBirthDate(birthDateInput.getValue());
			}
		});
		fieldTable.addField(birthDateField);

		vp.add(fieldTable);
		vp.add(fieldsPanel);

		if (entity.isSaved() && entity.getStatusId() != 1) {
			form.setEnabled(false);
			form.getSubmitButton().removeFromParent();
		}

		return vp;
	}
}
