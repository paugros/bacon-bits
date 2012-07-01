package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.DateTimeBox;
import com.areahomeschoolers.baconbits.client.widgets.DateTimeRangeBox;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.GoogleMap;
import com.areahomeschoolers.baconbits.client.widgets.GroupListBox;
import com.areahomeschoolers.baconbits.client.widgets.MaxLengthTextArea;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.WidgetCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventPage implements Page {
	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save(formField);
		}
	});
	private VerticalPanel page;
	private final FieldTable fieldTable = new FieldTable();
	private Event event = new Event();
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;
	private TabPage tabPanel;

	public EventPage(VerticalPanel page) {
		int eventId = Url.getIntegerParameter("eventId");
		if (!Application.isAuthenticated() && eventId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		eventService.getPageData(eventId, new Callback<EventPageData>() {
			@Override
			protected void doOnSuccess(EventPageData result) {
				if (result == null) {
					new ErrorPage(PageError.PAGE_NOT_FOUND);
					return;
				}

				event = result.getEvent();
				pageData = result;
				initializePage();
			}
		});
	}

	private FormField createDescriptionField() {
		final HTML descriptionDisplay = new HTML();
		final ControlledRichTextArea descriptionInput = new ControlledRichTextArea();
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setRequired(true);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setHTML(event.getDescription());
				descriptionInput.getTextArea().setHTML(event.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setDescription(descriptionInput.getTextArea().getHTML());
			}
		});

		return descriptionField;
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		if (!event.isSaved()) {
			fieldTable.addField(createTitleField());
		}

		final Label categoryDisplay = new Label();
		final RequiredListBox categoryInput = new RequiredListBox();
		for (Data item : pageData.getCategories()) {
			categoryInput.addItem(item.get("category"), item.getId());
		}
		FormField categoryField = form.createFormField("Category:", categoryInput, categoryDisplay);
		categoryField.setInitializer(new Command() {
			@Override
			public void execute() {
				categoryDisplay.setText(event.getCategory());
				categoryInput.setValue(event.getCategoryId());
			}
		});
		categoryField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setCategoryId(categoryInput.getIntValue());
			}
		});
		fieldTable.addField(categoryField);

		final Label addressDisplay = new Label();
		final MaxLengthTextArea addressInput = new MaxLengthTextArea(200);
		addressInput.setVisibleLines(3);
		addressInput.setCharacterWidth(50);
		FormField addressField = form.createFormField("Address:", addressInput, addressDisplay);
		addressField.setRequired(true);
		addressField.setInitializer(new Command() {
			@Override
			public void execute() {
				addressDisplay.setText(event.getAddress());
				addressInput.setText(event.getAddress());
			}
		});
		addressField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setAddress(addressInput.getText());
			}
		});
		fieldTable.addField(addressField);

		final Label eventDatesDisplay = new Label();
		final DateTimeRangeBox eventDatesInput = new DateTimeRangeBox();
		FormField eventDatesField = form.createFormField("Event date/time:", eventDatesInput, eventDatesDisplay);
		eventDatesField.setRequired(true);
		eventDatesField.setInitializer(new Command() {
			@Override
			public void execute() {
				eventDatesDisplay.setText(Formatter.formatDateTime(event.getStartDate()) + " to " + Formatter.formatDateTime(event.getEndDate()));
				eventDatesInput.setStartDate(event.getStartDate());
				eventDatesInput.setEndDate(event.getEndDate());
			}
		});
		eventDatesField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setStartDate(eventDatesInput.getStartDate());
				event.setEndDate(eventDatesInput.getEndDate());
			}
		});
		fieldTable.addField(eventDatesField);

		final Label registrationDatesDisplay = new Label();
		final DateTimeRangeBox registrationDatesInput = new DateTimeRangeBox();
		FormField registrationDatesField = form.createFormField("Registration open/close:", registrationDatesInput, registrationDatesDisplay);
		registrationDatesField.setRequired(true);
		registrationDatesField.setInitializer(new Command() {
			@Override
			public void execute() {
				registrationDatesDisplay.setText(Formatter.formatDateTime(event.getRegistrationStartDate()) + " to "
						+ Formatter.formatDateTime(event.getRegistrationEndDate()));
				registrationDatesInput.setStartDate(event.getRegistrationStartDate());
				registrationDatesInput.setEndDate(event.getRegistrationEndDate());
			}
		});
		registrationDatesField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setRegistrationStartDate(registrationDatesInput.getStartDate());
				event.setRegistrationEndDate(registrationDatesInput.getEndDate());
			}
		});
		fieldTable.addField(registrationDatesField);

		if (Application.isAuthenticated()) {
			final Label publishDateDisplay = new Label();
			final DateTimeBox publishDateInput = new DateTimeBox();
			FormField publishDateField = form.createFormField("Publish date:", publishDateInput, publishDateDisplay);
			publishDateField.setRequired(true);
			publishDateField.setInitializer(new Command() {
				@Override
				public void execute() {
					publishDateDisplay.setText(Formatter.formatDate(event.getPublishDate()));
					publishDateInput.setValue(event.getPublishDate());
				}
			});
			publishDateField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setPublishDate(publishDateInput.getValue());
				}
			});
			fieldTable.addField(publishDateField);

			final Label activeDisplay = new Label();
			final DefaultListBox activeInput = new DefaultListBox();
			activeInput.addItem("No", 0);
			activeInput.addItem("Yes", 1);
			FormField activeField = form.createFormField("Active:", activeInput, activeDisplay);
			activeField.setInitializer(new Command() {
				@Override
				public void execute() {
					activeDisplay.setText(Common.yesNo(event.getActive()));
					activeInput.setValue(event.getActive() ? 1 : 0);
				}
			});
			activeField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setActive(activeInput.getIntValue() == 1);
				}
			});
			fieldTable.addField(activeField);

			final Label emailDisplay = new Label();
			final EmailTextBox emailInput = new EmailTextBox();
			emailInput.setMultiEmail(true);
			emailInput.setMaxLength(200);
			emailInput.setVisibleLength(60);
			FormField emailField = form.createFormField("Notification email(s) (separate with commas):", emailInput, emailDisplay);
			emailField.setRequired(true);
			emailField.setInitializer(new Command() {
				@Override
				public void execute() {
					emailDisplay.setText(event.getNotificationEmail());
					emailInput.setText(event.getNotificationEmail());
				}
			});
			emailField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setNotificationEmail(emailInput.getText());
				}
			});
			fieldTable.addField(emailField);

			final Label costDisplay = new Label();
			final NumericTextBox costInput = new NumericTextBox(2);
			costInput.setMaxLength(10);
			FormField costField = form.createFormField("Cost:", costInput, costDisplay);
			costField.setInitializer(new Command() {
				@Override
				public void execute() {
					costDisplay.setText(Formatter.formatCurrency(event.getCost()));
					costInput.setValue(event.getCost());
				}
			});
			costField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setCost(costInput.getDouble());
				}
			});
			fieldTable.addField(costField);

			final Label minParticipantsDisplay = new Label();
			final NumericTextBox minParticipantsInput = new NumericTextBox();
			FormField minParticipantsField = form.createFormField("Minimum participants:", minParticipantsInput, minParticipantsDisplay);
			minParticipantsField.setInitializer(new Command() {
				@Override
				public void execute() {
					minParticipantsDisplay.setText(Integer.toString(event.getMinimumParticipants()));
					minParticipantsInput.setValue(event.getMinimumParticipants());
				}
			});
			minParticipantsField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setMinimumParticipants(minParticipantsInput.getInteger());
				}
			});
			fieldTable.addField(minParticipantsField);

			final Label maxParticipantsDisplay = new Label();
			final NumericTextBox maxParticipantsInput = new NumericTextBox();
			FormField maxParticipantsField = form.createFormField("Maximum participants:", maxParticipantsInput, maxParticipantsDisplay);
			maxParticipantsField.setInitializer(new Command() {
				@Override
				public void execute() {
					maxParticipantsDisplay.setText(Integer.toString(event.getMaximumParticipants()));
					maxParticipantsInput.setValue(event.getMaximumParticipants());
				}
			});
			maxParticipantsField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setMaximumParticipants(maxParticipantsInput.getInteger());
				}
			});
			fieldTable.addField(maxParticipantsField);
		}

		final Label adultDisplay = new Label();
		final DefaultListBox adultInput = new DefaultListBox();
		adultInput.addItem("No", 0);
		adultInput.addItem("Yes", 1);
		FormField adultField = form.createFormField("Adult required:", adultInput, adultDisplay);
		adultField.setInitializer(new Command() {
			@Override
			public void execute() {
				adultDisplay.setText(Common.yesNo(event.getAdultRequired()));
				adultInput.setValue(event.getAdultRequired() ? 1 : 0);
			}
		});
		adultField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setAdultRequired(adultInput.getIntValue() == 1);
			}
		});
		fieldTable.addField(adultField);

		if (Application.isAuthenticated()) {
			final Label publicDisplay = new Label();
			final DefaultListBox publicInput = new DefaultListBox();
			publicInput.addItem("No", 0);
			publicInput.addItem("Yes", 1);
			FormField publicField = form.createFormField("Public event:", publicInput, publicDisplay);
			publicField.setInitializer(new Command() {
				@Override
				public void execute() {
					publicDisplay.setText(Common.yesNo(event.getPublicEvent()));
					publicInput.setValue(event.getPublicEvent() ? 1 : 0);
				}
			});
			publicField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					event.setPublicEvent(publicInput.getIntValue() == 1);
				}
			});
			fieldTable.addField(publicField);
		}

		final Label groupDisplay = new Label();
		WidgetCreator groupCreator = new WidgetCreator() {
			@Override
			public Widget createWidget() {
				return new GroupListBox(event.getGroupId());
			}
		};
		final FormField groupField = form.createFormField("Group:", groupCreator, groupDisplay);
		groupField.setInitializer(new Command() {
			@Override
			public void execute() {
				groupDisplay.setText(Common.getDefaultIfNull(event.getGroupName(), "All groups"));
				if (groupField.inputIsCreated()) {
					((GroupListBox) groupField.getInputWidget()).setValue(event.getGroupId());
				}
			}
		});
		groupField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setGroupId(((GroupListBox) groupField.getInputWidget()).getIntValue());
			}
		});
		fieldTable.addField(groupField);

		if (event.isSaved() && Application.isAuthenticated()) {
			fieldTable.addField("Added by:", event.getAddedByFullName());
			fieldTable.addField("Added date:", Formatter.formatDateTime(event.getAddedDate()));
		}

		if (!event.isSaved()) {
			fieldTable.addField(createDescriptionField());
		}
	}

	private FormField createTitleField() {
		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleInput.addStyleName("hugeText");
		titleInput.setVisibleLength(65);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				event.setTitle(titleInput.getText());
			}
		});
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleDisplay.setText(event.getTitle());
				titleInput.setText(event.getTitle());
			}
		});

		return titleField;
	}

	private void initializePage() {
		final String title = event.isSaved() ? event.getTitle() : "New Event";
		createFieldTable();
		form.initialize();

		if (!event.isSaved()) {
			form.configureForAdd(fieldTable);
			page.add(WidgetFactory.newSection(title, fieldTable));
		} else {
			tabPanel = new TabPage();
			form.emancipate();

			tabPanel.add("Event", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					if (!Application.isAuthenticated()) {
						Label title = new Label(event.getTitle());
						title.addStyleName("hugeText bold");
						tabBody.add(title);

						HTML h = new HTML(event.getDescription());
						tabBody.add(h);
						tabBody.setSpacing(10);
					} else {
						FieldTable ft = new FieldTable();
						ft.addField(createTitleField());
						ft.addField(createDescriptionField());
						tabBody.add(WidgetFactory.newSection(title, ft));
						// we need to do this again in case we started on another tab
						form.initialize();
						form.emancipate();
					}
					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Information", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					tabBody.add(WidgetFactory.newSection(title, fieldTable));

					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Map", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					tabBody.add(new GoogleMap(event.getAddress()));
					tabPanel.selectTabNow(tabBody);
				}
			});

			if (!Application.isAuthenticated()) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private void save(final FormField field) {
		eventService.save(event, new Callback<Event>() {
			@Override
			protected void doOnSuccess(Event e) {
				if (!Url.isParamValidId("eventId")) {
					HistoryToken.set(PageUrl.event(e.getId()));
				} else {
					event = e;
					form.setDto(e);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
