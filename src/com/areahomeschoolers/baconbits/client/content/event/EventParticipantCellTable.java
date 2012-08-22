package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
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
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class EventParticipantCellTable extends EntityCellTable<EventParticipant, EventArg, ParticipantColumn> {
	public enum ParticipantColumn implements EntityCellTableColumn<ParticipantColumn> {
		ATTENDED("Attended"), EVENT("Event"), EVENT_DATE("Event date"), REGISTRANT_NAME("Registrant"), PARTICIPANT_NAME("Participant"), ADDED_DATE("Added"), AGE(
				"Age"), PRICE("Price"), FIELDS("Fields"), STATUS("Status"), EDIT_STATUS("");

		private String title;

		ParticipantColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	private ParameterHandler<EventParticipant> cancelHandler;

	public EventParticipantCellTable() {
		init();
	}

	public EventParticipantCellTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl();
		final ArgMap<EventArg> args = getArgMap();

		filterBox.addItem("Future");
		filterBox.addItem("Past");
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
					args.remove(EventArg.NOT_STATUS_ID);
					args.put(EventArg.STATUS_ID, 5);
					break;
				case 3:
					args.setStatus(Status.ALL);
					if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
						getArgMap().put(EventArg.SHOW_INACTIVE);
					}
					break;
				}

				populate();
			}
		});

		int defaultIndex = 0;
		switch (args.getStatus()) {
		case ACTIVE:
			defaultIndex = 0;
			break;
		case INACTIVE:
			defaultIndex = 1;
			break;
		case ALL:
			defaultIndex = 3;
			break;
		}

		if (args.getInt(EventArg.STATUS_ID) == 5) {
			defaultIndex = 2;
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
						return item.getEventDate();
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
				addTextColumn(col, new ValueGetter<String, EventParticipant>() {
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
						if (uid == 0) {
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

						ClickLabel cl = new ClickLabel(editText, new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
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
