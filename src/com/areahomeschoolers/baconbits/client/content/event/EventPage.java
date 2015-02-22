package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule.AdDirection;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DateTimeRangeBox;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EmailDialog;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.MarkupTextBox;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.NumericRangeBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventPage implements Page {
	private Event calendarEvent = new Event();
	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(final FormField formField) {
			AddressField.validateAddress(calendarEvent, new Command() {
				@Override
				public void execute() {
					if (formField.equals(markupField) && calendarEvent.isSaved()) {
						calendarEvent.setMarkupChanged(true);
					}
					save(formField);
				}
			});
		}
	});
	private VerticalPanel page;
	private final FieldTable fieldTable = new FieldTable();
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;
	private TabPage tabPanel;
	private FlexTable ageTable = new FlexTable();
	private FlexTable volunteerTable = new FlexTable();
	private VolunteerPositionEditDialog volunteerDialog;
	private AgeGroupEditDialog ageDialog;
	private EmailDialog emailDialog;
	private FormField priceField;
	private FormField markupField;
	private TagSection tagSection;
	private FormField tagField;
	private String title;
	private FormField payPalField;

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

				if (Url.getBooleanParameter("details") && !Application.administratorOf(calendarEvent)) {
					new ErrorPage(PageError.NOT_AUTHORIZED);
					return;
				}

				ageDialog = new AgeGroupEditDialog(pageData.getAgeGroups(), calendarEvent, new Command() {
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

				title = calendarEvent.isSaved() ? calendarEvent.getTitle() : "New Event";

				CookieCrumb cc = new CookieCrumb();
				cc.add(new DefaultHyperlink("Events By Type", PageUrl.tagGroup("EVENT")));
				cc.add(new DefaultHyperlink("Events", PageUrl.eventList()));
				if (Url.getBooleanParameter("details")) {
					cc.add(new DefaultHyperlink(title, PageUrl.event(calendarEvent.getId())));
					cc.add("Edit details");
				} else {
					cc.add(title);
				}
				page.add(cc);

				if (calendarEvent.isSaved() && !Url.getBooleanParameter("details")) {
					createViewPage();
				} else {
					createDetailsPage();
				}

				Application.getLayout().setPage(title, page);
			}
		});
	}

	private void createDetailsPage() {
		final String title = calendarEvent.isSaved() ? calendarEvent.getTitle() : "New Event";
		createFieldTable();
		form.initialize();

		if (!calendarEvent.isSaved()) {
			form.configureForAdd(fieldTable);
			page.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH1000PX));
		} else {
			tabPanel = new TabPage();
			form.emancipate();

			tabPanel.add("Event", false, new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					TitleBar tb = new TitleBar("", TitleBarStyle.SECTION);
					if (Application.administratorOf(calendarEvent)) {
						tb.addLink(new ClickLabel("Clone", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
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
							tb.addLink(new ClickLabel("Create series", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									new EventSeriesDialog(calendarEvent).center();
								}
							}));
						}

						tb.addLink(new ClickLabel("Email", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								EmailDialog dialog = new EmailDialog();
								dialog.setShowSubjectBox(true);
								dialog.setAllowEditRecipients(true);
								dialog.setSubject(calendarEvent.getTitle());
								dialog.insertHtml(createEmailHtml());

								dialog.center();
							}
						}));

					}

					if (pageData.getRegistration() != null) {
						for (EventParticipant p : pageData.getRegistration().getParticipants()) {
							if (p.getAdjustedPrice() > 0 && p.getStatusId() == 1) {
								tb.addLink(new DefaultHyperlink("Pay", PageUrl.payment()));
								break;
							}
						}
					}

					tabBody.add(WidgetFactory.newSection(tb, fieldTable, ContentWidth.MAXWIDTH1000PX));

					// we need to do this again in case we started on another tab
					form.initialize();
					form.emancipate();

					tabPanel.selectTabNow(tabBody);
				}
			});

			if (calendarEvent.getSeriesId() != null) {
				tabPanel.add("Series", false, new TabPageCommand() {
					@Override
					public void execute(final VerticalPanel tabBody) {
						ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.SERIES_ID, calendarEvent.getSeriesId());
						args.setStatus(Status.ALL);
						EventTable table = new EventTable(args);

						table.setTitle("Event Series");

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1100PX));
						table.populate(pageData.getEventsInSeries());
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (Application.administratorOf(calendarEvent)) {
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

			if (calendarEvent.getRequiresRegistration() && Application.administratorOf(calendarEvent)) {
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
										if (Application.administratorOf(calendarEvent) && !item.getBoolean("adjustmentApplied")) {
											ClickLabel cl = new ClickLabel("X", new ClickHandler() {
												@Override
												public void onClick(ClickEvent event) {
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
						args.put(EventArg.NOT_STATUS_ID, 5);
						args.put(EventArg.INCLUDE_FIELDS);
						final EventParticipantTable table = new EventParticipantTable(args);
						table.disablePaging();

						table.addStatusFilterBox();
						table.getTitleBar().addExcelControl();
						table.getTitleBar().addSearchControl();
						table.populate();
						table.setTitle("Participants");
						table.addDataReturnHandler(new DataReturnHandler() {
							@Override
							public void onDataReturn() {
								if (emailDialog != null) {
									return;
								}

								table.getTitleBar().addLink(new ClickLabel("Email Registrants", new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										emailDialog = new EmailDialog();
										String subject = calendarEvent.getTitle() + " - " + Formatter.formatDateTime(calendarEvent.getStartDate());
										emailDialog.setSubject(subject);
										emailDialog.setShowSubjectBox(true);
										for (EventParticipant p : table.getFullList()) {
											emailDialog.addBcc(p.getRegistrantEmailAddress());
										}
										emailDialog.center();
									}
								}));
							}
						});

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1200PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (!Application.administratorOf(calendarEvent)) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private String createEmailHtml() {
		String h = "<div style=\"padding: 10px; font-family: arial, helvetica; font-size: 12px;\">";
		h += "<span style=\"font-size: 25px;\">" + calendarEvent.getTitle() + "</span><br>";
		h += "<div style=\"padding: 7px;\"><b>Date/time: </b>";
		h += Formatter.formatDateTime(calendarEvent.getStartDate()) + " to " + Formatter.formatDateTime(calendarEvent.getEndDate()) + "<br>";
		h += "<b>Address: </b>";
		h += calendarEvent.getAddress() + "</div><br><br>";
		h += calendarEvent.getDescription();
		h += "</div>";
		return h;
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleInput.setVisibleLength(50);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setTitle(titleInput.getText().trim());
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

		AddressField af = new AddressField(calendarEvent);
		final FormField addressField = af.getFormField();
		af.getInputPanel().setSpacing(6);
		CheckBox cb = new CheckBox("This is a virtual/online event");
		cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				addressField.setRequired(!event.getValue());
			}
		});
		af.getInputPanel().insert(cb, 0);
		addressField.getInputWidget();
		addressField.setRequired(true);
		form.addField(addressField);
		fieldTable.addField(addressField);

		final Label facilityDisplay = new Label();
		final TextBox facilityInput = new TextBox();
		facilityInput.setMaxLength(200);
		FormField facilityField = form.createFormField("Facility name:", facilityInput, facilityDisplay);
		facilityField.setInitializer(new Command() {
			@Override
			public void execute() {
				facilityDisplay.setText(Common.getDefaultIfNull(calendarEvent.getFacilityName()));
				facilityInput.setText(calendarEvent.getFacilityName());
			}
		});
		facilityField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setFacilityName(facilityInput.getText());
			}
		});
		fieldTable.addField(facilityField);

		final HTML websiteDisplay = new HTML();
		final TextBox websiteInput = new TextBox();
		websiteInput.setMaxLength(512);
		FormField websiteField = form.createFormField("Web site:", websiteInput, websiteDisplay);
		websiteField.setInitializer(new Command() {
			@Override
			public void execute() {
				if (Common.isNullOrBlank(calendarEvent.getWebsite())) {
					websiteDisplay.setText(Common.getDefaultIfNull(null));
				} else {
					websiteDisplay.setHTML("<a href=\"" + calendarEvent.getWebsite() + "\" target=\"_blank\">" + calendarEvent.getWebsite() + "</a>");
				}
				websiteInput.setText(calendarEvent.getWebsite());
				if (Common.isNullOrBlank(calendarEvent.getWebsite())) {
					websiteInput.setText("http://");
				}
			}
		});
		websiteField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				if (websiteInput.getText().equals("http://")) {
					return;
				}
				calendarEvent.setWebsite(websiteInput.getText().trim());
			}
		});
		fieldTable.addField(websiteField);

		final Label phoneDisplay = new Label();
		final PhoneTextBox phoneInput = new PhoneTextBox(true);
		FormField phoneField = form.createFormField("Contact phone:", phoneInput, phoneDisplay);
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

		final Label contactNameDisplay = new Label();
		final TextBox contactNameInput = new TextBox();
		contactNameInput.setMaxLength(100);
		FormField contactNameField = form.createFormField("Contact name:", contactNameInput, contactNameDisplay);
		contactNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				contactNameDisplay.setText(Common.getDefaultIfNull(calendarEvent.getContactName()));
				contactNameInput.setText(calendarEvent.getContactName());
			}
		});
		contactNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setContactName(contactNameInput.getText());
			}
		});
		fieldTable.addField(contactNameField);

		final Label contactEmailDisplay = new Label();
		final TextBox contactEmailInput = new TextBox();
		contactEmailInput.setMaxLength(100);
		FormField contactEmailField = form.createFormField("Contact email:", contactEmailInput, contactEmailDisplay);
		contactEmailField.setInitializer(new Command() {
			@Override
			public void execute() {
				contactEmailDisplay.setText(Common.getDefaultIfNull(calendarEvent.getContactEmail()));
				contactEmailInput.setText(calendarEvent.getContactEmail());
			}
		});
		contactEmailField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setContactEmail(contactEmailInput.getText());
			}
		});
		fieldTable.addField(contactEmailField);

		createTagSection();
		fieldTable.addField(tagField);

		final Label registerDisplay = new Label();
		final RequiredListBox registerInput = new RequiredListBox();
		registerInput.addItem("No registration is required", 0);
		registerInput.addItem("Use this event to collect registrations and fees", 1);
		FormField registerField = form.createFormField("Registration:", registerInput, registerDisplay);
		registerField.setInitializer(new Command() {
			@Override
			public void execute() {
				registerDisplay.setText(Common.yesNo(calendarEvent.getRequiresRegistration()));
				if (calendarEvent.isSaved()) {
					registerInput.setValue(calendarEvent.getRequiresRegistration() ? 1 : 0);
				}
				registerInput.fireEvent(new ChangeEvent() {
				});
			}
		});
		registerField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setRequiresRegistration(registerInput.getIntValue() == 1);
			}
		});
		fieldTable.addField(registerField);

		if (!calendarEvent.isSaved() || calendarEvent.getRequiresRegistration()) {
			final Label priceDisplay = new Label();
			final MarkupTextBox priceInput = new MarkupTextBox(calendarEvent);
			priceField = form.createFormField("Price:", priceInput, priceDisplay);
			priceField.setInitializer(new Command() {
				@Override
				public void execute() {
					String text = Formatter.formatCurrency(calendarEvent.getAdjustedPrice());
					if (calendarEvent.getPrice() == 0) {
						text = "Free";
					}
					priceDisplay.setText(text);
					priceInput.setValue(calendarEvent.getPrice());

					if (!Application.administratorOf(calendarEvent)) {
						priceField.setEnabled(false);
					}
				}
			});
			priceField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setPrice(priceInput.getDouble());
				}
			});
			fieldTable.addField(priceField);

			String groupEmail = Application.getCurrentOrg().getPayPalEmail();
			String selfEmail = Application.getCurrentUser().getPayPalEmail();
			boolean groupPay = !Common.isNullOrBlank(groupEmail) && Application.administratorOfCurrentOrg();
			boolean selfPay = !Common.isNullOrBlank(selfEmail);

			if (groupPay && selfPay && !calendarEvent.isSaved()) {
				final Label payPalDisplay = new Label();
				final DefaultListBox payPalInput = new DefaultListBox();
				payPalInput.addItem(groupEmail);
				payPalInput.addItem(selfEmail);
				payPalField = form.createFormField("Pay to (PayPal email):", payPalInput, payPalDisplay);
				payPalField.setInitializer(new Command() {
					@Override
					public void execute() {
						payPalDisplay.setText(calendarEvent.getPayPalEmail());
						payPalInput.setValue(calendarEvent.getPayPalEmail());
					}
				});
				payPalField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						calendarEvent.setPayPalEmail(payPalInput.getValue());
					}
				});
				fieldTable.addField(payPalField);
			} else {
				final Label payPalEditDisplay = new Label();
				final TextBox payPalEditInput = new TextBox();
				payPalEditInput.setMaxLength(255);
				payPalEditInput.setVisibleLength(30);
				if (!calendarEvent.isSaved()) {
					if (groupPay) {
						calendarEvent.setPayPalEmail(groupEmail);
					} else if (selfPay) {
						calendarEvent.setPayPalEmail(selfEmail);
					}
				}
				payPalField = form.createFormField("Pay to (PayPal email):", payPalEditInput, payPalEditDisplay);
				payPalField.setInitializer(new Command() {
					@Override
					public void execute() {
						payPalEditDisplay.setText(calendarEvent.getPayPalEmail());
						payPalEditInput.setText(calendarEvent.getPayPalEmail());
					}
				});
				payPalField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						calendarEvent.setPayPalEmail(payPalEditInput.getText());
					}
				});
				fieldTable.addField(payPalField);
			}

			priceInput.setChangeCommand(new Command() {
				@Override
				public void execute() {
					payPalField.setRequired(priceInput.getDouble() > 0.00);
				}
			});

			final Label emailDisplay = new Label();
			final EmailTextBox emailInput = new EmailTextBox();
			emailInput.setMultiEmail(true);
			emailInput.setMaxLength(200);
			emailInput.setVisibleLength(60);
			final FormField emailField = form.createFormField("Email registrations to (list with commas):", emailInput, emailDisplay);
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

			final NumericRangeBox participantInput = new NumericRangeBox();
			final Label participantDisplay = new Label();
			participantInput.setAllowZeroForNoLimit(true);
			final FormField participantField = form.createFormField("Min / max participants:", participantInput, participantDisplay);
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

			if (Application.isSystemAdministrator()) {
				MarkupField mf = new MarkupField(calendarEvent);
				markupField = mf.getFormField();
				if (!calendarEvent.isSaved()) {
					mf.setChangeCommand(new Command() {
						@Override
						public void execute() {
							markupField.updateDto();
							priceField.updateDto();
							priceField.initialize();
						}
					});
				}

				form.addField(markupField);
				fieldTable.addField(markupField);
			}

			registerInput.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					boolean show = registerInput.getIntValue() == 1;
					payPalField.setRequired(show && priceInput.getDouble() > 0);
					fieldTable.setFieldVisibility(participantField, show);
					fieldTable.setFieldVisibility(emailField, show);
					fieldTable.setFieldVisibility(priceField, show);
					fieldTable.setFieldVisibility(payPalField, show);
					if (Application.isSystemAdministrator()) {
						fieldTable.setFieldVisibility(markupField, show);
					}
					if (!calendarEvent.isSaved()) {
						priceField.setInputVisibility(true);
						emailField.setInputVisibility(true);
						participantField.setInputVisibility(true);
						payPalField.setInputVisibility(true);
						if (Application.isSystemAdministrator()) {
							markupField.setInputVisibility(true);
						}
					}

				}
			});
		}

		final Label accessDisplay = new Label();
		final ItemVisibilityWidget accessInput = new ItemVisibilityWidget();
		accessInput.showOnlyCurrentOrganization();
		accessInput.removeItem(VisibilityLevel.PRIVATE);
		accessInput.removeItem(VisibilityLevel.MY_GROUPS);
		FormField accessField = form.createFormField("Visible to:", accessInput, accessDisplay);
		accessField.setInitializer(new Command() {
			@Override
			public void execute() {
				String text = calendarEvent.getVisibilityLevel();
				if (calendarEvent.getGroupId() != null && calendarEvent.getGroupId() > 0) {
					text += " - " + calendarEvent.getGroupName();
				}
				accessDisplay.setText(text);
				accessInput.setVisibilityLevelId(calendarEvent.getVisibilityLevelId());
				accessInput.setGroupId(calendarEvent.getGroupId());
			}
		});
		accessField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				calendarEvent.setVisibilityLevelId(accessInput.getVisibilityLevelId());
				calendarEvent.setGroupId(accessInput.getGroupId());
			}
		});
		fieldTable.addField(accessField);

		if (Application.isSystemAdministrator()) {
			final Label priorityDisplay = new Label();
			final DefaultListBox priorityInput = new DefaultListBox();
			priorityInput.addItem("Yes");
			priorityInput.addItem("No");
			FormField priorityField = form.createFormField("Directory listing priority:", priorityInput, priorityDisplay);
			priorityField.setInitializer(new Command() {
				@Override
				public void execute() {
					priorityDisplay.setText(calendarEvent.getDirectoryPriority() ? "Yes" : "No");
					priorityInput.setValue(calendarEvent.getDirectoryPriority() ? "Yes" : "No");
				}
			});
			priorityField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					calendarEvent.setDirectoryPriority(priorityInput.getValue().equals("Yes"));
				}
			});
			fieldTable.addField(priorityField);
		}

		// if (Application.administratorOf(calendarEvent) && Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS)) {
		// final Label costDisplay = new Label();
		// final NumericTextBox costInput = new NumericTextBox(2);
		// costInput.setMaxLength(10);
		// FormField costField = form.createFormField("Cost:", costInput, costDisplay);
		// costField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// costDisplay.setText(Formatter.formatCurrency(calendarEvent.getCost()));
		// costInput.setValue(calendarEvent.getCost());
		// }
		// });
		// costField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// calendarEvent.setCost(costInput.getDouble());
		// }
		// });
		// fieldTable.addField(costField);
		// }

		// final Label categoryDisplay = new Label();
		// final RequiredListBox categoryInput = new RequiredListBox();
		// for (Data item : pageData.getCategories()) {
		// categoryInput.addItem(item.get("category"), item.getId());
		// }
		// categoryInput.addChangeHandler(new ChangeHandler() {
		// @Override
		// public void onChange(ChangeEvent event) {
		// int categoryId = categoryInput.getIntValue();
		// if (categoryId == 6) {
		// calendarEvent.setMarkupOverride(true);
		// calendarEvent.setMarkupDollars(0);
		// calendarEvent.setMarkupPercent(0);
		// } else {
		// calendarEvent.setMarkupOverride(false);
		// }
		// }
		// });
		// FormField categoryField = form.createFormField("Category:", categoryInput, categoryDisplay);
		// categoryField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// categoryDisplay.setText(calendarEvent.getCategory());
		// categoryInput.setValue(calendarEvent.getCategoryId());
		// }
		// });
		// categoryField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// calendarEvent.setCategoryId(categoryInput.getIntValue());
		// }
		// });
		// fieldTable.addField(categoryField);

		if (calendarEvent.getRequiresRegistration()) {
			if (calendarEvent.isSaved() && (Application.administratorOf(calendarEvent) || !Common.isNullOrEmpty(pageData.getAgeGroups()))) {
				ageTable.setWidth("150px");

				populateAgeGroups();

				fieldTable.addField("Pricing:", ageTable);
			}

			if (Common.isNullOrEmpty(pageData.getAgeGroups())) {
				final Label priceDisplay = new Label();
				final MarkupTextBox priceInput = new MarkupTextBox(calendarEvent);
				priceField = form.createFormField("Price:", priceInput, priceDisplay);
				priceField.setInitializer(new Command() {
					@Override
					public void execute() {
						String text = Formatter.formatCurrency(calendarEvent.getAdjustedPrice());
						if (calendarEvent.getPrice() == 0) {
							text = "Free";
						}
						priceDisplay.setText(text);
						priceInput.setValue(calendarEvent.getPrice());

						if (!Application.administratorOf(calendarEvent)) {
							priceField.setEnabled(false);
						}
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

		if (calendarEvent.isSaved()) {
			volunteerTable.setWidth("400px");

			populateVolunteerPositions();

			fieldTable.addField("Volunteer positions:", volunteerTable);
		}

		// final Label instructionsDisplay = new Label();
		// final MaxLengthTextArea instructionsInput = new MaxLengthTextArea(300);
		// instructionsInput.setVisibleLines(2);
		// FormField instructionsField = form.createFormField("Registration instructions:", instructionsInput, instructionsDisplay);
		// instructionsField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// instructionsDisplay.setText(Common.getDefaultIfNull(calendarEvent.getRegistrationInstructions()));
		// instructionsInput.setText(calendarEvent.getRegistrationInstructions());
		// }
		// });
		// instructionsField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// calendarEvent.setRegistrationInstructions(instructionsInput.getText().trim());
		// }
		// });
		// fieldTable.addField(instructionsField);

		// final Label registrationDatesDisplay = new Label();
		// final DateTimeRangeBox registrationDatesInput = new DateTimeRangeBox();
		// FormField registrationDatesField = form.createFormField("Registration open/close:", registrationDatesInput, registrationDatesDisplay);
		// registrationDatesField.setRequired(true);
		// registrationDatesField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// registrationDatesDisplay.setText(Formatter.formatDateTime(calendarEvent.getRegistrationStartDate()) + " to "
		// + Formatter.formatDateTime(calendarEvent.getRegistrationEndDate()));
		// registrationDatesInput.setStartDate(calendarEvent.getRegistrationStartDate());
		// registrationDatesInput.setEndDate(calendarEvent.getRegistrationEndDate());
		// }
		// });
		// registrationDatesField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// calendarEvent.setRegistrationStartDate(registrationDatesInput.getStartDate());
		// calendarEvent.setRegistrationEndDate(registrationDatesInput.getEndDate());
		// }
		// });
		// fieldTable.addField(registrationDatesField);
		//
		// eventDatesInput.addEndValueChangeHandler(new ValueChangeHandler<Date>() {
		// @Override
		// public void onValueChange(ValueChangeEvent<Date> event) {
		// Date d = event.getValue();
		// if (d == null) {
		// return;
		// }
		//
		// registrationDatesInput.setEndDate(ClientDateUtils.addHours(d, (-14 * 24) + 3));
		// registrationDatesInput.setStartDate(ClientDateUtils.addHours(d, (-28 * 24) + 3));
		// }
		// });

		// final Label adultDisplay = new Label();
		// final DefaultListBox adultInput = new DefaultListBox();
		// adultInput.addItem("No", 0);
		// adultInput.addItem("Yes", 1);
		// FormField adultField = form.createFormField("Adult required:", adultInput, adultDisplay);
		// adultField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// adultDisplay.setText(Common.yesNo(calendarEvent.getAdultRequired()));
		// adultInput.setValue(calendarEvent.getAdultRequired() ? 1 : 0);
		// }
		// });
		// adultField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// calendarEvent.setAdultRequired(adultInput.getIntValue() == 1);
		// }
		// });
		// fieldTable.addField(adultField);

		// final Label publishDateDisplay = new Label();
		// final DateTimeBox publishDateInput = new DateTimeBox();
		// FormField publishDateField = form.createFormField("Publish date:", publishDateInput, publishDateDisplay);
		// publishDateField.setRequired(true);
		// publishDateField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// publishDateDisplay.setText(Formatter.formatDate(calendarEvent.getPublishDate()));
		// publishDateInput.setValue(calendarEvent.getPublishDate());
		// }
		// });
		// publishDateField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// calendarEvent.setPublishDate(publishDateInput.getValue());
		// }
		// });
		// fieldTable.addField(publishDateField);

		if (calendarEvent.isSaved()) {
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
		}

		if (calendarEvent.isSaved()) {
			if (Application.administratorOf(calendarEvent)) {
				fieldTable.addField("View count:", Integer.toString(calendarEvent.getViewCount()));
			}
			fieldTable.addField("Added by:", calendarEvent.getAddedByFullName());
			fieldTable.addField("Added date:", Formatter.formatDateTime(calendarEvent.getAddedDate()));
		}

		final HTML descriptionDisplay = new HTML();
		final ControlledRichTextArea descriptionInput = new ControlledRichTextArea();
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionDisplay.getElement().getStyle().setPadding(10, Unit.PX);
		descriptionDisplay.setWidth("800px");
		descriptionDisplay.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
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
		fieldTable.addSpanningWidget(descriptionField);

		if (calendarEvent.isSaved()) {
			DocumentSection ds = new DocumentSection(calendarEvent, Application.administratorOf(calendarEvent));
			ds.init();
			fieldTable.addField("Documents:", ds);
		}

	}

	private void createTagSection() {
		tagSection = new TagSection(TagMappingType.EVENT, calendarEvent.getId());
		tagSection.setEditingEnabled(Application.administratorOf(calendarEvent));
		tagSection.setRequired(true);
		tagSection.populate(pageData.getTags());

		tagField = form.createFormField("Tags:", tagSection);
		tagField.removeEditLabel();
	}

	private void createViewPage() {
		PaddedPanel pp = new PaddedPanel(10);
		pp.setWidth("100%");
		pp.getElement().getStyle().setMarginTop(10, Unit.PX);
		pp.getElement().getStyle().setMarginLeft(10, Unit.PX);

		// EditableImage image = new EditableImage(DocumentLinkType.EVENT, calendarEvent.getId());
		Image image = null;
		if (calendarEvent.getImageId() != null) {
			image = new Image(ClientUtils.createDocumentUrl(calendarEvent.getImageId(), calendarEvent.getImageExtension()));
		} else {
			image = new Image(MainImageBundle.INSTANCE.defaultLarge());
		}
		// image.setEnabled(Application.isSystemAdministrator());
		// image.populate();
		pp.add(image);
		pp.setCellWidth(image, "1%");

		VerticalPanel vp = new VerticalPanel();

		Label titleLabel = new Label(calendarEvent.getTitle());
		titleLabel.addStyleName("hugeText");

		vp.add(titleLabel);

		String time = Formatter.formatDateTime(calendarEvent.getStartDate());
		if (!calendarEvent.getStartDate().equals(calendarEvent.getEndDate())) {
			time += " to " + Formatter.formatDateTime(calendarEvent.getEndDate());
		}
		Label timeLabel = new Label(time);
		timeLabel.getElement().getStyle().setFontSize(14, Unit.PX);
		vp.add(timeLabel);

		if (!Common.isNullOrBlank(calendarEvent.getFacilityName())) {
			vp.add(new Label("At " + calendarEvent.getFacilityName()));
		}

		if (!Common.isNullOrBlank(calendarEvent.getAddress())) {
			Anchor address = new Anchor(calendarEvent.getAddress(), "http://maps.google.com/maps?q=" + calendarEvent.getAddress());
			address.setTarget("_blank");

			vp.add(address);
		}

		if (calendarEvent.getPrice() > 0) {
			String price = "Price: " + Formatter.formatCurrency(calendarEvent.getAdjustedPrice());
			Label priceLabel = new Label(price);
			priceLabel.getElement().getStyle().setFontSize(16, Unit.PX);
			priceLabel.getElement().getStyle().setMarginTop(10, Unit.PX);

			vp.add(priceLabel);
		}

		String text = "";
		if (!Common.isNullOrBlank(calendarEvent.getContactName())) {
			text += Formatter.formatNoteText(calendarEvent.getContactName()) + "<br>";
		}

		if (!Common.isNullOrBlank(calendarEvent.getContactEmail())) {
			text += "<a href=\"mailto:" + calendarEvent.getContactEmail() + "\">" + Formatter.formatNoteText(calendarEvent.getContactEmail()) + "</a><br>";
		}

		if (!Common.isNullOrBlank(calendarEvent.getPhone())) {
			text += Formatter.formatNoteText(calendarEvent.getPhone()) + "<br>";
		}

		PaddedPanel lp = new PaddedPanel();

		if (!Common.isNullOrBlank(calendarEvent.getWebsite())) {
			HTML web = new HTML("<a href=\"" + calendarEvent.getWebsite() + "\" target=_blank>Web site</a>");
			lp.add(web);
			lp.setCellVerticalAlignment(web, HasVerticalAlignment.ALIGN_MIDDLE);
		}

		if (lp.getWidgetCount() > 0) {
			text += lp;
		}

		if (!text.isEmpty()) {
			HTML contactInfo = new HTML();
			contactInfo.getElement().getStyle().setMarginTop(10, Unit.PX);
			contactInfo.setHTML(text);

			vp.add(contactInfo);
		}

		pp.add(vp);

		VerticalPanel ddt = new VerticalPanel();
		ddt.setSpacing(6);

		if (Application.administratorOf(calendarEvent)) {
			DefaultHyperlink edit = new DefaultHyperlink("Edit details", PageUrl.event(calendarEvent.getId()) + "&details=true");
			edit.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
			ddt.add(edit);
		}

		final VerticalPanel oovp = new VerticalPanel();

		System.out.println(pageData.getEvent().getFinished());
		System.out.println(pageData.getEvent().getRegistrationOpen());
		if (pageData.getRegistration() != null && !pageData.getRegistration().getParticipants().isEmpty()) {
			oovp.insert(new EventRegistrationSection(pageData), 0);
		} else if (calendarEvent.getRequiresRegistration() && pageData.getEvent().allowRegistrations()) {
			final ClickLabel register = new ClickLabel("Register");
			register.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!Application.isAuthenticated()) {
						LoginDialog.showLogin();
						return;
					}

					EventParticipant rp = new EventParticipant();
					EventRegistration registration = pageData.getRegistration();
					rp.setEventRegistrationId(registration.getId());
					ParameterHandler<EventRegistration> refreshParticipants = new ParameterHandler<EventRegistration>() {
						@Override
						public void execute(EventRegistration item) {
							pageData.setRegistration(item);
							EventRegistrationSection ers = new EventRegistrationSection(pageData);
							oovp.insert(ers, 0);

							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									register.removeFromParent();
								}
							});
						}
					};
					new ParticipantEditDialog(pageData, refreshParticipants).center(rp);
				}
			});
			register.addStyleName("bold");
			ddt.add(register);
		}

		if (calendarEvent.getRequiresRegistration() && pageData.getEvent().allowRegistrations()) {
			final ClickLabel attend = new ClickLabel("Who's attending?");
			attend.setWordWrap(false);
			attend.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!Application.isAuthenticated()) {
						LoginDialog.showLogin();
						return;
					}
					ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.EVENT_ID, calendarEvent.getId());
					args.put(EventArg.STATUS_ID, 2);
					eventService.getParticipants(args, new Callback<ArrayList<EventParticipant>>() {
						@Override
						protected void doOnSuccess(ArrayList<EventParticipant> result) {
							DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
							VerticalPanel vp = new VerticalPanel();
							MaxHeightScrollPanel sp = new MaxHeightScrollPanel(vp);
							vp.setSpacing(8);
							if (!result.isEmpty()) {
								String s = result.size() > 1 ? "s" : "";
								Label total = new Label(result.size() + " attendee" + s + ". Any not shown below have private profiles.");
								total.getElement().getStyle().setMarginBottom(5, Unit.PX);
								vp.add(total);
							}
							for (EventParticipant p : result) {
								if (p.getFirstName() == null) {
									continue;
								}
								Label name = new Label(p.getFirstName() + " " + p.getLastName());
								vp.add(name);
							}

							if (result.isEmpty()) {
								vp.add(new Label("No confirmed attendees yet."));
							}
							popup.setWidget(sp);
							popup.showRelativeTo(attend);
						}
					});
				}
			});
			ddt.add(attend);
		}

		pp.add(ddt);
		pp.setCellHorizontalAlignment(ddt, HasHorizontalAlignment.ALIGN_RIGHT);

		VerticalPanel ovp = new VerticalPanel();
		oovp.add(ovp);
		ovp.addStyleName("sectionContent");

		ovp.add(pp);

		TagSection ts = new TagSection(TagMappingType.EVENT, calendarEvent.getId());
		ts.setEditingEnabled(false);
		ts.populate();

		ovp.add(ts);

		if (!Common.isNullOrBlank(calendarEvent.getDescription())) {
			HTML desc = new HTML(calendarEvent.getDescription());
			desc.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
			desc.getElement().getStyle().setMarginLeft(15, Unit.PX);
			desc.getElement().getStyle().setMarginRight(15, Unit.PX);
			desc.getElement().getStyle().setMarginBottom(15, Unit.PX);
			desc.getElement().getStyle().setPadding(10, Unit.PX);
			desc.getElement().getStyle().setBackgroundColor("#ffffff");
			desc.getElement().getStyle().setBorderColor("#cccccc");
			desc.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			desc.getElement().getStyle().setBorderWidth(1, Unit.PX);
			desc.getElement().getStyle().setBorderWidth(1, Unit.PX);

			ovp.add(desc);
			ovp.addStyleName(ContentWidth.MAXWIDTH800PX.toString());
		}

		VerticalPanel outerPanel = new VerticalPanel();
		outerPanel.add(oovp);
		outerPanel.add(new AdsMiniModule(AdDirection.HORIZONTAL));

		page.add(outerPanel);
	}

	private void populateAgeGroups() {
		ageTable.removeAllRows();
		if (Application.administratorOf(calendarEvent)) {
			ageTable.setWidget(0, 0, new ClickLabel("Add", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
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
			if (Application.administratorOf(calendarEvent)) {
				ageTable.setWidget(row, 0, new ClickLabel(ageText, new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						ageDialog.setText("Edit Age Group");
						ageDialog.center(g);
					}
				}));
			} else {
				ageTable.setWidget(row, 0, new Label(ageText));
			}

			ageTable.setText(row, 1, Formatter.formatCurrency(g.getAdjustedPrice()));

			if (Application.administratorOf(calendarEvent) && (g.getRegisterCount() + g.getFieldCount()) == 0) {
				ageTable.setWidget(row, 2, new ClickLabel("X", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
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

		if (Application.administratorOf(calendarEvent)) {
			volunteerTable.setWidget(0, 0, new ClickLabel("Add", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
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
			if (Application.administratorOf(calendarEvent)) {
				vp.add(new ClickLabel(v.getJobTitle(), new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
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

			if (Application.administratorOf(calendarEvent) && v.getOpenPositionCount() == v.getPositionCount()) {
				volunteerTable.setWidget(row, 1, new ClickLabel("X", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
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
			protected void doOnSuccess(final Event e) {
				if (!isSaved) {
					tagSection.saveAll(e.getId(), new Callback<Void>() {
						@Override
						protected void doOnSuccess(Void result) {
							HistoryToken.set(PageUrl.event(e.getId()));
						}
					});
				} else {
					calendarEvent = e;
					form.setDto(e);
					field.setInputVisibility(false);

					if (field.equals(priceField)) {
						priceField.initialize();
					}

					if (field.equals(markupField)) {
						if (priceField != null) {
							priceField.initialize();
						}

						if (!Common.isNullOrEmpty(pageData.getAgeGroups())) {
							eventService.getPageData(calendarEvent.getId(), new Callback<EventPageData>() {
								@Override
								protected void doOnSuccess(EventPageData result) {
									pageData = result;
									populateAgeGroups();
								}
							});
						}
					}
				}
			}
		});
	}

}
