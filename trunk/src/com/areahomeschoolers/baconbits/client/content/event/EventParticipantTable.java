package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class EventParticipantTable extends EntityCellTable<EventParticipant, EventArg, ParticipantColumn> {
	public enum ParticipantColumn implements EntityCellTableColumn<ParticipantColumn> {
		ATTENDED("Attended"), EVENT("Event"), EVENT_DATE("Event Date"), REGISTRANT_NAME("Registrant"), PARTICIPANT_NAME("Participant"), ADDED_DATE("Added"), AGE(
				"Age"), PRICE("Price"), TOTALED_PRICE("Price"), FIELDS("Fields"), STATUS("Status"), EDIT_STATUS("");

		private String title;

		ParticipantColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private List<Data> statusList;

	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	private ParameterHandler<EventParticipant> cancelHandler;

	public EventParticipantTable() {
		init();
	}

	public EventParticipantTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl(false);
		final ArgMap<EventArg> args = getArgMap();

		filterBox.addItem("Future");
		filterBox.addItem("Past");
		filterBox.addItem("Registered/Pending");
		filterBox.addItem("Confirmed/Paid");
		filterBox.addItem("Waiting");
		filterBox.addItem("Attended");
		filterBox.addItem("Canceled");
		filterBox.addItem("All");

		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				// reset state
				args.remove(EventArg.SHOW_INACTIVE);
				args.remove(EventArg.STATUS_ID);
				args.put(EventArg.NOT_STATUS_ID, 5);

				switch (filterBox.getSelectedIndex()) {
				case 0:
					args.setStatus(Status.ACTIVE);
					break;
				case 1:
					args.setStatus(Status.INACTIVE);
					break;
				case 2:
					args.put(EventArg.STATUS_ID, 1);
					break;
				case 3:
					args.put(EventArg.STATUS_ID, 2);
					break;
				case 4:
					args.put(EventArg.STATUS_ID, 3);
					break;
				case 5:
					args.put(EventArg.STATUS_ID, 4);
					break;
				case 6:
					args.remove(EventArg.NOT_STATUS_ID);
					args.put(EventArg.STATUS_ID, 5);
					break;
				case 7:
					args.setStatus(Status.ALL);
					if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
						getArgMap().put(EventArg.SHOW_INACTIVE);
					}
					break;
				}

				populate();
			}
		});

		int defaultIndex = 7;

		if (args.getStatus() == Status.INACTIVE) {
			defaultIndex = 1;
		}

		switch (args.getInt(EventArg.STATUS_ID)) {
		case 1:
			defaultIndex = 2;
			break;
		case 2:
			defaultIndex = 3;
			break;
		case 3:
			defaultIndex = 4;
			break;
		case 4:
			defaultIndex = 5;
			break;
		case 5:
			defaultIndex = 6;
			break;
		}

		filterBox.setSelectedIndex(defaultIndex);
	}

	public void setCancelHandler(ParameterHandler<EventParticipant> cancelHandler) {
		this.cancelHandler = cancelHandler;
	}

	private void init() {
		setDefaultSortColumn(ParticipantColumn.EVENT_DATE, SortDirection.SORT_ASC);
		setDisplayColumns(ParticipantColumn.ATTENDED, ParticipantColumn.REGISTRANT_NAME, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.ADDED_DATE,
				ParticipantColumn.AGE, ParticipantColumn.PRICE, ParticipantColumn.FIELDS, ParticipantColumn.STATUS, ParticipantColumn.EDIT_STATUS);
	}

	private void populateStatusList(DefaultListBox lb) {
		for (Data item : statusList) {
			lb.addItem(item.get("status"), item.getId());
		}
	}

	@Override
	protected void fetchData() {
		eventService.getParticipants(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (ParticipantColumn col : getDisplayColumns()) {
			switch (col) {
			case EVENT_DATE:
				setColumnWidth(addDateTimeColumn(col, new ValueGetter<Date, EventParticipant>() {
					@Override
					public Date get(EventParticipant item) {
						return item.getEventStartDate();
					}
				}), "140px");
				break;
			case EVENT:
				addCompositeWidgetColumn(col, new WidgetCellCreator<EventParticipant>() {
					@Override
					protected Widget createWidget(EventParticipant item) {
						return new Hyperlink(item.getEventTitle(), PageUrl.event(item.getEventId()));
					}
				});
				break;
			case ATTENDED:
				addCheckboxColumn("Attended", new ValueGetter<Boolean, EventParticipant>() {
					@Override
					public Boolean get(EventParticipant item) {
						if (item.getStatusId() != 2) {
							// TODO disable checkbox
						}
						return item.getStatusId() == 4;
					}
				}, new FieldUpdater<EventParticipant, Boolean>() {
					@Override
					public void update(int index, EventParticipant item, Boolean value) {
						if (item.getStatusId() != 2 && item.getStatusId() != 4) {
							return;
						}

						if (item.hasAttended()) {
							item.setStatusId(2);
						} else {
							item.setStatusId(4);
						}
						eventService.saveParticipant(item, new Callback<ServerResponseData<ArrayList<EventParticipant>>>(false) {
							@Override
							protected void doOnSuccess(ServerResponseData<ArrayList<EventParticipant>> d) {
								removeItems(d.getData());
								addItems(d.getData());
								refresh();
							}
						});
					}
				});
				break;
			case PRICE:
				addCurrencyColumn(col, new ValueGetter<Double, EventParticipant>() {
					@Override
					public Double get(EventParticipant item) {
						return item.getPrice();
					}
				});
				break;
			case TOTALED_PRICE:
				addTotaledCurrencyColumn("Price", new ValueGetter<Number, EventParticipant>() {
					@Override
					public Number get(EventParticipant item) {
						return item.getPrice();
					}
				});
				break;
			case ADDED_DATE:
				setColumnWidth(addDateTimeColumn(col, new ValueGetter<Date, EventParticipant>() {
					@Override
					public Date get(EventParticipant item) {
						return item.getAddedDate();
					}
				}), "140px");
				break;
			case FIELDS:
				addWidgetColumn(col, new WidgetCellCreator<EventParticipant>() {
					@Override
					protected Widget createWidget(EventParticipant item) {
						return new HTML(Formatter.formatNoteText(item.getFieldValues()));
					}
				});
				break;
			case PARTICIPANT_NAME:
				addCompositeWidgetColumn(col, new WidgetCellCreator<EventParticipant>() {
					@Override
					protected Widget createWidget(EventParticipant item) {
						Hyperlink link = new Hyperlink(item.getFirstName() + " " + item.getLastName(), PageUrl.user(item.getUserId()));
						return link;
					}
				});
				break;
			case REGISTRANT_NAME:
				addCompositeWidgetColumn(col, new WidgetCellCreator<EventParticipant>() {
					@Override
					protected Widget createWidget(EventParticipant item) {
						Hyperlink link = new Hyperlink(item.getAddedByFirstName() + " " + item.getAddedByLastName(), PageUrl.user(item.getAddedById()));
						return link;
					}
				});
				break;
			case STATUS:
				addCompositeWidgetColumn(col, new WidgetCellCreator<EventParticipant>() {
					@Override
					protected Widget createWidget(final EventParticipant item) {
						if (!Application.administratorOfAny(item.getEventGroupId(), item.getEventOrganizationId())) {
							return new Label(item.getStatus());
						}

						ClickLabel cl = new ClickLabel(item.getStatus(), new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								EntityEditDialog<EventParticipant> dialog = new EntityEditDialog<EventParticipant>() {
									@Override
									protected Widget createContent() {
										FieldTable ft = new FieldTable();

										final DefaultListBox statusInput = new DefaultListBox();
										if (statusList == null) {
											eventService.getParticipantStatusList(new Callback<ArrayList<Data>>() {
												@Override
												protected void doOnSuccess(ArrayList<Data> result) {
													statusList = result;
													populateStatusList(statusInput);
													statusInput.setValue(item.getStatusId());
												}
											});
										} else {
											populateStatusList(statusInput);
											statusInput.setValue(item.getStatusId());
										}
										FormField statusField = form.createFormField("Status:", statusInput);
										statusField.setInitializer(new Command() {
											@Override
											public void execute() {
												statusInput.setValue(item.getStatusId());
											}
										});
										statusField.setDtoUpdater(new Command() {
											@Override
											public void execute() {
												item.setStatusId(statusInput.getIntValue());
												item.setStatus(statusInput.getSelectedText());
											}
										});
										ft.addField(statusField);

										return ft;
									}
								};

								dialog.addFormSubmitHandler(new FormSubmitHandler() {
									@Override
									public void onFormSubmit(FormField formField) {
										eventService.overrideParticipantStatus(item, new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void result) {
												refresh();
											}
										});
									}
								});

								dialog.setText("Set Status");
								dialog.center(item);
							}
						});

						return cl;
					}
				}, new ValueGetter<String, EventParticipant>() {
					@Override
					public String get(EventParticipant item) {
						return item.getStatus();
					}
				});
				break;
			case AGE:
				addTextColumn(col, new ValueGetter<String, EventParticipant>() {
					@Override
					public String get(EventParticipant item) {
						if (item.getBirthDate() == null) {
							return "0";
						}

						int age = (int) (ClientDateUtils.daysBetween(item.getBirthDate(), new Date()) / 365);

						if (age >= 18) {
							return "Adult";
						}

						return Integer.toString(age);
					}
				}, new ValueGetter<Date, EventParticipant>() {
					@Override
					public Date get(EventParticipant item) {
						return item.getBirthDate();
					}
				});
				break;
			case EDIT_STATUS:
				addCompositeWidgetColumn(col, new WidgetCellCreator<EventParticipant>() {
					@Override
					protected Widget createWidget(final EventParticipant item) {
						int uid = Application.getCurrentUserId();
						if (uid == 0 || item.getRequiredInSeries()) {
							return new Label("");
						}
						if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
							if (uid != item.getUserId() && uid != item.getAddedById() && uid != item.getParentId()) {
								return new Label("");
							}
						}

						String editText = "";
						if (item.isCanceled()) {
							editText = "Restore";
						} else {
							editText = "X";
						}

						ClickLabel cl = new ClickLabel(editText, new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								String confirmText = "";
								if (item.isCanceled()) {
									confirmText = "Restore registration for " + item.getFirstName() + "?";
								} else {
									confirmText = "Really remove " + item.getFirstName() + " from the attendee list?";
								}
								ConfirmDialog.confirm(confirmText, new ConfirmHandler() {
									@Override
									public void onConfirm() {
										item.setStatusId(item.isCanceled() ? 1 : 5);

										eventService.saveParticipant(item, new Callback<ServerResponseData<ArrayList<EventParticipant>>>() {
											@Override
											protected void doOnSuccess(ServerResponseData<ArrayList<EventParticipant>> result) {
												removeItems(result.getData());
												addItems(result.getData());
												refresh();

												if (item.isCanceled() && cancelHandler != null) {
													cancelHandler.execute(item);
												}
											}
										});
									}
								});
							}
						});

						return cl;
					}
				});
				break;
			default:
				new AssertionError();
				break;
			}
		}

	}
}
