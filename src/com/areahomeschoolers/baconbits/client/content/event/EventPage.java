package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.content.event.EventCellTable.EventColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.AccessLevelListBox;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
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
import com.areahomeschoolers.baconbits.client.widgets.NumericRangeBox;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.client.widgets.WidgetCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventPage implements Page {
	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(final FormField formField) {
			// if (calendarEvent.isSaved() && calendarEvent.getSeriesId() != null) {
			// ConfirmDialog confirm = new ConfirmDialog(null, null)
			// confirm.confirm(ConfirmDialogType.YES_NO, "Make this change to all events in series?", new ConfirmHandler() {
			// @Override
			// public void onConfirm() {
			// calendarEvent.setSaveAllInSeries(true);
			// save(formField);
			// }
			// });
			//
			// confirm.addCancelHandler(new CancelHandler() {
			// @Override
			// public void onCancel() {
			// calendarEvent.setSaveAllInSeries(false);
			// save(formField);
			// }
			// });
			// } else {
			// save(formField);
			// }
			save(formField);
		}
	});
	private VerticalPanel page;
	private final FieldTable fieldTable = new FieldTable();
	private Event calendarEvent = new Event();
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;
	private TabPage tabPanel;
	private FlexTable ageTable = new FlexTable();
	private FlexTable volunteerTable = new FlexTable();
	private VolunteerPositionEditDialog volunteerDialog;
	private AgeGroupEditDialog ageDialog;

	public EventPage(final VerticalPanel page) {
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

				calendarEvent = result.getEvent();
				pageData = result;
				ageDialog = new AgeGroupEditDialog(pageData.getAgeGroups(), new Command() {
					@Override
					public void execute() {
						populateAgeGroups();
					}
				});

				volunteerDialog = new VolunteerPositionEditDialog(pageData.getVolunteerPositions(), new Command() {
					@Override
					public void execute() {
						populateVolunteerPositions();
					}
				});

				HorizontalPanel hp = new HorizontalPanel();
				hp.setWidth("100%");
				Label heading = new Label(calendarEvent.getTitle() + " - " + Formatter.formatDateTime(calendarEvent.getStartDate()));
				heading.addStyleName("hugeText");
				hp.add(heading);
				BalanceBox bb = new BalanceBox();
				bb.populate();
				hp.add(bb);
				hp.setCellHorizontalAlignment(bb, HasHorizontalAlignment.ALIGN_RIGHT);
				page.add(WidgetFactory.wrapForWidth(hp, ContentWidth.MAXWIDTH900PX));

				initializePage();
			}
		});
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleInput.addStyleName("hugeText");
		titleDisplay.addStyleName("hugeText");
		titleInput.setVisibleLength(65);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setTitle(titleInput.getText());
			}
		});
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleDisplay.setText(calendarEvent.getTitle());
				titleInput.setText(calendarEvent.getTitle());
			}
		});
		fieldTable.addField(titleField);

		final Label eventDatesDisplay = new Label();
		final DateTimeRangeBox eventDatesInput = new DateTimeRangeBox();
		FormField eventDatesField = form.createFormField("Event date/time:", eventDatesInput, eventDatesDisplay);
		eventDatesField.setRequired(true);
		eventDatesField.setInitializer(new Command() {
			@Override
			public void execute() {
				eventDatesDisplay.setText(Formatter.formatDateTime(calendarEvent.getStartDate()) + " to "
						+ Formatter.formatDateTime(calendarEvent.getEndDate()));
				eventDatesInput.setStartDate(calendarEvent.getStartDate());
				eventDatesInput.setEndDate(calendarEvent.getEndDate());
			}
		});
		eventDatesField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setStartDate(eventDatesInput.getStartDate());
				calendarEvent.setEndDate(eventDatesInput.getEndDate());
			}
		});
		fieldTable.addField(eventDatesField);

		final Hyperlink addressDisplay = new Hyperlink();
		final MaxLengthTextArea addressInput = new MaxLengthTextArea(200);
		addressInput.setVisibleLines(2);
		addressInput.setCharacterWidth(50);
		FormField addressField = form.createFormField("Address:", addressInput, addressDisplay);
		addressField.setRequired(true);
		addressField.setInitializer(new Command() {
			@Override
			public void execute() {
				addressDisplay.setText(calendarEvent.getAddress());
				addressDisplay.setTargetHistoryToken(PageUrl.event(calendarEvent.getId()) + "&tab=5");
				addressInput.setText(calendarEvent.getAddress());
			}
		});
		addressField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setAddress(addressInput.getText());
			}
		});
		fieldTable.addField(addressField);

		if (Application.administratorOf(calendarEvent.getGroupId()) || !Common.isNullOrBlank(calendarEvent.getWebsite())) {
			final HTML websiteDisplay = new HTML();
			final TextBox websiteInput = new TextBox();
			websiteInput.setMaxLength(512);
			FormField websiteField = form.createFormField("Website:", websiteInput, websiteDisplay);
			websiteField.setInitializer(new Command() {
				@Override
				public void execute() {
					if (Common.isNullOrBlank(calendarEvent.getWebsite())) {
						websiteDisplay.setText(Common.getDefaultIfNull(null));
					} else {
						websiteDisplay.setHTML("<a href=\"" + calendarEvent.getWebsite() + "\" target=\"_blank\">" + calendarEvent.getWebsite() + "</a>");
					}
					websiteInput.setText(calendarEvent.getWebsite());
				}
			});
			websiteField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setWebsite(websiteInput.getText());
				}
			});
			fieldTable.addField(websiteField);
		}

		if (Application.administratorOf(calendarEvent.getGroupId()) || !Common.isNullOrBlank(calendarEvent.getPhone())) {
			final Label phoneDisplay = new Label();
			final PhoneTextBox phoneInput = new PhoneTextBox();
			FormField phoneField = form.createFormField("Phone:", phoneInput, phoneDisplay);
			phoneField.setInitializer(new Command() {
				@Override
				public void execute() {
					phoneDisplay.setText(Common.getDefaultIfNull(calendarEvent.getPhone()));
					phoneInput.setText(calendarEvent.getPhone());
				}
			});
			phoneField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setPhone(phoneInput.getText());
				}
			});
			fieldTable.addField(phoneField);
		}

		if (calendarEvent.getRequiresRegistration()) {
			if (calendarEvent.isSaved() && (Application.administratorOf(calendarEvent.getGroupId()) || !Common.isNullOrEmpty(pageData.getAgeGroups()))) {
				ageTable.setWidth("150px");

				populateAgeGroups();

				fieldTable.addField("Pricing:", ageTable);
			}

			if (Common.isNullOrEmpty(pageData.getAgeGroups())) {
				final Label priceDisplay = new Label();
				final NumericTextBox priceInput = new NumericTextBox(2);
				priceInput.setMaxLength(10);
				FormField priceField = form.createFormField("Price:", priceInput, priceDisplay);
				priceField.setInitializer(new Command() {
					@Override
					public void execute() {
						String text = Formatter.formatCurrency(calendarEvent.getPrice());
						if (calendarEvent.getPrice() == 0) {
							text = "Free";
						}
						priceDisplay.setText(text);
						priceInput.setValue(calendarEvent.getPrice());
					}
				});
				priceField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						calendarEvent.setPrice(priceInput.getDouble());
					}
				});
				fieldTable.addField(priceField);
			}
		}

		if (calendarEvent.isSaved() && (Application.administratorOf(calendarEvent.getGroupId()) || !Common.isNullOrEmpty(pageData.getVolunteerPositions()))) {
			volunteerTable.setWidth("400px");

			populateVolunteerPositions();

			fieldTable.addField("Volunteer positions:", volunteerTable);
		}

		if (Application.administratorOf(calendarEvent.getGroupId())) {
			final Label categoryDisplay = new Label();
			final RequiredListBox categoryInput = new RequiredListBox();
			for (Data item : pageData.getCategories()) {
				categoryInput.addItem(item.get("category"), item.getId());
			}
			FormField categoryField = form.createFormField("Category:", categoryInput, categoryDisplay);
			categoryField.setInitializer(new Command() {
				@Override
				public void execute() {
					categoryDisplay.setText(calendarEvent.getCategory());
					categoryInput.setValue(calendarEvent.getCategoryId());
				}
			});
			categoryField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setCategoryId(categoryInput.getIntValue());
				}
			});
			fieldTable.addField(categoryField);

			final Label instructionsDisplay = new Label();
			final MaxLengthTextArea instructionsInput = new MaxLengthTextArea(300);
			instructionsInput.setVisibleLines(2);
			FormField instructionsField = form.createFormField("Registration instructions:", instructionsInput, instructionsDisplay);
			instructionsField.setInitializer(new Command() {
				@Override
				public void execute() {
					instructionsDisplay.setText(Common.getDefaultIfNull(calendarEvent.getRegistrationInstructions()));
					instructionsInput.setText(calendarEvent.getRegistrationInstructions());
				}
			});
			instructionsField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setRegistrationInstructions(instructionsInput.getText());
				}
			});
			fieldTable.addField(instructionsField);
		}

		if (Application.administratorOf(calendarEvent.getGroupId())) {
			final Label registrationDatesDisplay = new Label();
			final DateTimeRangeBox registrationDatesInput = new DateTimeRangeBox();
			FormField registrationDatesField = form.createFormField("Registration open/close:", registrationDatesInput, registrationDatesDisplay);
			registrationDatesField.setRequired(true);
			registrationDatesField.setInitializer(new Command() {
				@Override
				public void execute() {
					registrationDatesDisplay.setText(Formatter.formatDateTime(calendarEvent.getRegistrationStartDate()) + " to "
							+ Formatter.formatDateTime(calendarEvent.getRegistrationEndDate()));
					registrationDatesInput.setStartDate(calendarEvent.getRegistrationStartDate());
					registrationDatesInput.setEndDate(calendarEvent.getRegistrationEndDate());
				}
			});
			registrationDatesField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setRegistrationStartDate(registrationDatesInput.getStartDate());
					calendarEvent.setRegistrationEndDate(registrationDatesInput.getEndDate());
				}
			});
			fieldTable.addField(registrationDatesField);

			eventDatesInput.addEndValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					Date d = event.getValue();
					if (d == null) {
						return;
					}

					registrationDatesInput.setEndDate(ClientDateUtils.addHours(d, (-14 * 24) + 3));
				}
			});

			final Label adultDisplay = new Label();
			final DefaultListBox adultInput = new DefaultListBox();
			adultInput.addItem("No", 0);
			adultInput.addItem("Yes", 1);
			FormField adultField = form.createFormField("Adult required:", adultInput, adultDisplay);
			adultField.setInitializer(new Command() {
				@Override
				public void execute() {
					adultDisplay.setText(Common.yesNo(calendarEvent.getAdultRequired()));
					adultInput.setValue(calendarEvent.getAdultRequired() ? 1 : 0);
				}
			});
			adultField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setAdultRequired(adultInput.getIntValue() == 1);
				}
			});
			fieldTable.addField(adultField);

			final Label publishDateDisplay = new Label();
			final DateTimeBox publishDateInput = new DateTimeBox();
			FormField publishDateField = form.createFormField("Publish date:", publishDateInput, publishDateDisplay);
			publishDateField.setRequired(true);
			publishDateField.setInitializer(new Command() {
				@Override
				public void execute() {
					publishDateDisplay.setText(Formatter.formatDate(calendarEvent.getPublishDate()));
					publishDateInput.setValue(calendarEvent.getPublishDate());
				}
			});
			publishDateField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setPublishDate(publishDateInput.getValue());
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
					activeDisplay.setText(Common.yesNo(calendarEvent.getActive()));
					activeInput.setValue(calendarEvent.getActive() ? 1 : 0);
				}
			});
			activeField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setActive(activeInput.getIntValue() == 1);
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
					emailDisplay.setText(calendarEvent.getNotificationEmail());
					emailInput.setText(calendarEvent.getNotificationEmail());
				}
			});
			emailField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setNotificationEmail(emailInput.getText());
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
					costDisplay.setText(Formatter.formatCurrency(calendarEvent.getCost()));
					costInput.setValue(calendarEvent.getCost());
				}
			});
			costField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setCost(costInput.getDouble());
				}
			});
			fieldTable.addField(costField);

			if ((Common.isNullOrEmpty(pageData.getAgeGroups()) || Application.administratorOf(calendarEvent.getGroupId()))
					|| !calendarEvent.getRequiresRegistration()) {
				final NumericRangeBox participantInput = new NumericRangeBox();
				final Label participantDisplay = new Label();
				participantInput.setAllowZeroForNoLimit(true);
				FormField participantField = form.createFormField("Min / max participants:", participantInput, participantDisplay);
				participantField.setInitializer(new Command() {
					@Override
					public void execute() {
						participantDisplay.setText(Formatter.formatNumberRange(calendarEvent.getMinimumParticipants(), calendarEvent.getMaximumParticipants()));
						participantInput.setRange(calendarEvent.getMinimumParticipants(), calendarEvent.getMaximumParticipants());
					}
				});
				participantField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						calendarEvent.setMinimumParticipants((int) participantInput.getFromValue());
						calendarEvent.setMaximumParticipants((int) participantInput.getToValue());
					}
				});
				fieldTable.addField(participantField);
			}

			final Label registerDisplay = new Label();
			final DefaultListBox registerInput = new DefaultListBox();
			registerInput.addItem("No", 0);
			registerInput.addItem("Yes", 1);
			FormField registerField = form.createFormField("Requires registration:", registerInput, registerDisplay);
			registerField.setInitializer(new Command() {
				@Override
				public void execute() {
					registerDisplay.setText(Common.yesNo(calendarEvent.getRequiresRegistration()));
					registerInput.setValue(calendarEvent.getRequiresRegistration() ? 1 : 0);
				}
			});
			registerField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setRequiresRegistration(registerInput.getIntValue() == 1);
				}
			});
			fieldTable.addField(registerField);

			final Label accessDisplay = new Label();
			final DefaultListBox accessInput = new AccessLevelListBox(calendarEvent.getGroupId());
			FormField accessField = form.createFormField("Visible to:", accessInput, accessDisplay);
			accessField.setInitializer(new Command() {
				@Override
				public void execute() {
					accessDisplay.setText(calendarEvent.getAccessLevel());
					accessInput.setValue(calendarEvent.getAccessLevelId());
				}
			});
			accessField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setAccessLevelId(accessInput.getIntValue());
				}
			});
			fieldTable.addField(accessField);

			final Label groupDisplay = new Label();
			WidgetCreator groupCreator = new WidgetCreator() {
				@Override
				public Widget createWidget() {
					return new GroupListBox(calendarEvent.getGroupId());
				}
			};
			final FormField groupField = form.createFormField("Group:", groupCreator, groupDisplay);
			groupField.setInitializer(new Command() {
				@Override
				public void execute() {
					groupDisplay.setText(Common.getDefaultIfNull(calendarEvent.getGroupName(), "None"));
					if (groupField.inputIsCreated()) {
						((GroupListBox) groupField.getInputWidget()).setValue(calendarEvent.getGroupId());
					}
				}
			});
			groupField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setGroupId(((GroupListBox) groupField.getInputWidget()).getIntValue());
				}
			});
			fieldTable.addField(groupField);

			if (calendarEvent.isSaved()) {
				fieldTable.addField("Added by:", calendarEvent.getAddedByFullName());
				fieldTable.addField("Added date:", Formatter.formatDateTime(calendarEvent.getAddedDate()));
			}
		}

		final HTML descriptionDisplay = new HTML();
		final ControlledRichTextArea descriptionInput = new ControlledRichTextArea();
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setRequired(true);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setHTML(calendarEvent.getDescription());
				descriptionInput.getTextArea().setHTML(calendarEvent.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setDescription(descriptionInput.getTextArea().getHTML());
			}
		});
		fieldTable.addField(descriptionField);

		if (calendarEvent.isSaved() && (calendarEvent.hasDocuments() || Application.administratorOf(calendarEvent.getGroupId()))) {
			DocumentSection ds = new DocumentSection(calendarEvent, Application.administratorOf(calendarEvent.getGroupId()));
			ds.populate();
			fieldTable.addField("Documents:", ds);
		}

	}

	private void initializePage() {
		final String title = calendarEvent.isSaved() ? calendarEvent.getTitle() : "New Event";
		createFieldTable();
		form.initialize();

		if (!calendarEvent.isSaved()) {
			form.configureForAdd(fieldTable);
			page.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH1100PX));
		} else {
			tabPanel = new TabPage();
			form.emancipate();

			tabPanel.add("Event", false, new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					TitleBar tb = new TitleBar(title, TitleBarStyle.SECTION);
					if (Application.administratorOf(calendarEvent.getGroupId())) {
						tb.addLink(new ClickLabel("Clone", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								ConfirmDialog.confirm("Clone this event?", new ConfirmHandler() {
									@Override
									public void onConfirm() {
										calendarEvent.setCloneFromId(calendarEvent.getId());
										calendarEvent.setId(0);
										save(form.getFirstFormField());
									}
								});
							}
						}));

						if (calendarEvent.getSeriesId() == null) {
							tb.addLink(new ClickLabel("Create series", new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
									new EventSeriesDialog(calendarEvent).center();
								}
							}));
						}
					}

					if (pageData.getRegistration() != null) {
						for (EventParticipant p : pageData.getRegistration().getParticipants()) {
							if (p.getPrice() > 0 && p.getStatusId() == 1) {
								tb.addLink(new Hyperlink("Pay", PageUrl.payment()));
								break;
							}
						}
					}

					tabBody.add(WidgetFactory.newSection(tb, fieldTable, ContentWidth.MAXWIDTH1100PX));

					// we need to do this again in case we started on another tab
					form.initialize();
					form.emancipate();

					tabPanel.selectTabNow(tabBody);
				}
			});

			if (calendarEvent.getRequiresRegistration()) {
				tabPanel.add("Register", false, new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						tabBody.add(new EventRegistrationSection(pageData));

						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (calendarEvent.getSeriesId() != null) {
				tabPanel.add("Series", false, new TabPageCommand() {
					@Override
					public void execute(final VerticalPanel tabBody) {
						ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.SERIES_ID, calendarEvent.getSeriesId());
						args.setStatus(Status.ALL);
						EventCellTable table = new EventCellTable(args);
						table.removeColumn(EventColumn.REGISTER);

						table.setTitle("Event Series");

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1100PX));
						table.populate(pageData.getEventsInSeries());
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (Application.administratorOf(calendarEvent.getGroupId())) {
				tabPanel.add("Fields", false, new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						tabBody.add(new EventFieldsTab(pageData));

						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (calendarEvent.getRequiresRegistration() && Application.administratorOf(calendarEvent.getGroupId())) {
				tabPanel.add("Participants", false, new TabPageCommand() {
					@Override
					public void execute(final VerticalPanel tabBody) {
						if (!Common.isNullOrEmpty(pageData.getVolunteerPositions())) {
							ArgMap<EventArg> volunteerArgs = new ArgMap<EventArg>(EventArg.EVENT_ID, calendarEvent.getId());
							eventService.getVolunteers(volunteerArgs, new Callback<ArrayList<Data>>() {
								@Override
								protected void doOnSuccess(ArrayList<Data> result) {
									VerticalPanel vp = new VerticalPanel();
									Label label = new Label("Volunteers");
									label.addStyleName("largeText");

									final FlexTable ft = new FlexTable();

									for (final Data item : result) {
										final int row = ft.getRowCount();

										CheckBox cb = new CheckBox();
										cb.setValue(item.getBoolean("fulfilled"));
										cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
											@Override
											public void onValueChange(ValueChangeEvent<Boolean> event) {
												eventService.setVolunteerFulFilled(item.getId(), event.getValue(), new Callback<Void>(false) {
													@Override
													protected void doOnSuccess(Void result) {
													}
												});
											}
										});

										// if they've already used the corresponding discount, don't allow it to be deleted
										if (item.getBoolean("adjustmentApplied")) {
											cb.setEnabled(false);
										}
										ft.setWidget(row, 0, cb);
										Label name = new Label(item.get("firstName") + " " + item.get("lastName"));
										name.getElement().getStyle().setMarginRight(30, Unit.PX);
										ft.setWidget(row, 1, name);
										Label title = new Label(item.get("jobTitle"));
										title.getElement().getStyle().setMarginRight(20, Unit.PX);
										ft.setWidget(row, 2, title);
										if (Application.administratorOf(calendarEvent.getGroupId()) && !item.getBoolean("adjustmentApplied")) {
											ClickLabel cl = new ClickLabel("X", new MouseDownHandler() {
												@Override
												public void onMouseDown(MouseDownEvent event) {
													ConfirmDialog.confirm("Remove this volunteer?", new ConfirmHandler() {
														@Override
														public void onConfirm() {
															eventService.deleteVolunteerPositionMapping(item.getId(), new Callback<Void>() {
																@Override
																protected void doOnSuccess(Void result) {
																	ft.removeRow(row);
																	if (ft.getRowCount() == 0) {
																		ft.setText(0, 0, "None");
																	}
																}
															});
														}
													});
												}
											});

											ft.setWidget(row, 3, cl);
										}
									}

									vp.add(label);
									if (!result.isEmpty()) {
										vp.add(ft);
									} else {
										Label none = new Label("None");
										none.addStyleName("mediumPadding");
										vp.add(none);
									}
									tabBody.insert(vp, 0);
								}
							});
						}

						ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.EVENT_ID, calendarEvent.getId());
						args.put(EventArg.INCLUDE_FIELDS);
						EventParticipantCellTable table = new EventParticipantCellTable(args);
						table.addStatusFilterBox();
						table.getTitleBar().addExcelControl();
						table.getTitleBar().addSearchControl();
						table.populate();
						table.setTitle("Participants");
						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1200PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			tabPanel.add("Map", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					tabBody.add(new GoogleMap(calendarEvent.getAddress()));
					tabPanel.selectTabNow(tabBody);
				}
			});

			if (!Application.administratorOf(calendarEvent.getGroupId())) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private void populateAgeGroups() {
		ageTable.removeAllRows();
		if (Application.administratorOf(calendarEvent.getGroupId())) {
			ageTable.setWidget(0, 0, new ClickLabel("Add", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					ageDialog.setText("Add Age Group");
					EventAgeGroup a = new EventAgeGroup();
					a.setEventId(calendarEvent.getId());
					ageDialog.center(a);
				}
			}));
			ageTable.getCellFormatter().addStyleName(0, 0, "bold");
		}

		for (final EventAgeGroup g : pageData.getAgeGroups()) {
			int row = ageTable.getRowCount();
			String ageText = "Ages " + Formatter.formatNumberRange(g.getMinimumAge(), g.getMaximumAge());
			if (Application.administratorOf(calendarEvent.getGroupId())) {
				ageTable.setWidget(row, 0, new ClickLabel(ageText, new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						ageDialog.setText("Edit Age Group");
						ageDialog.center(g);
					}
				}));
			} else {
				ageTable.setWidget(row, 0, new Label(ageText));
			}

			ageTable.setText(row, 1, Formatter.formatCurrency(g.getPrice()));

			if (Application.administratorOf(calendarEvent.getGroupId()) && (g.getRegisterCount() + g.getFieldCount()) == 0) {
				ageTable.setWidget(row, 2, new ClickLabel("X", new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						ConfirmDialog.confirm("Really delete this age group?", new ConfirmHandler() {
							@Override
							public void onConfirm() {
								eventService.deleteAgeGroup(g, new Callback<Void>() {
									@Override
									protected void doOnSuccess(Void result) {
										pageData.getAgeGroups().remove(g);
										populateAgeGroups();
									}
								});
							}
						});
					}
				}));
			}

		}
	}

	private void populateVolunteerPositions() {
		volunteerTable.removeAllRows();

		if (Application.administratorOf(calendarEvent.getGroupId())) {
			volunteerTable.setWidget(0, 0, new ClickLabel("Add", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					volunteerDialog.setText("Add Volunteer Position");
					EventVolunteerPosition v = new EventVolunteerPosition();
					v.setEventId(calendarEvent.getId());
					volunteerDialog.center(v);
				}
			}));
			volunteerTable.getCellFormatter().addStyleName(0, 0, "bold");
		}

		for (final EventVolunteerPosition v : pageData.getVolunteerPositions()) {
			int row = volunteerTable.getRowCount();
			VerticalPanel vp = new VerticalPanel();
			if (Application.administratorOf(calendarEvent.getGroupId())) {
				vp.add(new ClickLabel(v.getJobTitle(), new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						volunteerDialog.setText("Edit Volunteer Position");
						volunteerDialog.center(v);
					}
				}));
			} else {
				vp.add(new Label(v.getJobTitle()));
			}
			Label description = new Label(v.getDescription());
			description.getElement().getStyle().setMarginLeft(5, Unit.PX);
			vp.add(description);
			volunteerTable.setWidget(row, 0, vp);

			if (Application.administratorOf(calendarEvent.getGroupId()) && v.getOpenPositionCount() == v.getPositionCount()) {
				volunteerTable.setWidget(row, 1, new ClickLabel("X", new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						ConfirmDialog.confirm("Really delete this volunteer position?", new ConfirmHandler() {
							@Override
							public void onConfirm() {
								eventService.deleteVolunteerPosition(v, new Callback<Void>() {
									@Override
									protected void doOnSuccess(Void result) {
										pageData.getVolunteerPositions().remove(v);
										populateVolunteerPositions();
									}
								});
							}
						});
					}
				}));

				volunteerTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
			}

		}
	}

	private void save(final FormField field) {
		final boolean isSaved = calendarEvent.isSaved();

		eventService.save(calendarEvent, new Callback<Event>() {
			@Override
			protected void doOnSuccess(Event e) {
				if (!isSaved) {
					HistoryToken.set(PageUrl.event(e.getId()));
				} else {
					calendarEvent = e;
					form.setDto(e);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
