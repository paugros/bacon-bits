package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistrationParticipant;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public final class EventParticipantCellTable extends EntityCellTable<EventRegistrationParticipant, EventArg, ParticipantColumn> {
	public enum ParticipantColumn implements EntityCellTableColumn<ParticipantColumn> {
		REGISTRANT_NAME("Registrant"), PARTICIPANT_NAME("Participant"), ADDED_DATE("Added"), AGE("Age"), FIELDS("Fields"), STATUS("Status");

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

	public EventParticipantCellTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	private EventParticipantCellTable() {
		setDefaultSortColumn(ParticipantColumn.PARTICIPANT_NAME, SortDirection.SORT_ASC);
		setDisplayColumns(ParticipantColumn.REGISTRANT_NAME, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.ADDED_DATE, ParticipantColumn.AGE,
				ParticipantColumn.FIELDS, ParticipantColumn.STATUS);
	}

	@Override
	protected void fetchData() {
		eventService.getParticipants(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		addCheckboxColumn("Attended", new ValueGetter<Boolean, EventRegistrationParticipant>() {
			@Override
			public Boolean get(EventRegistrationParticipant item) {
				if (item.getStatusId() != 2) {
					// TODO disable checkbox
				}
				return item.getStatusId() == 4;
			}
		}, new FieldUpdater<EventRegistrationParticipant, Boolean>() {
			@Override
			public void update(int index, EventRegistrationParticipant item, Boolean value) {
				if (item.getStatusId() != 2 && item.getStatusId() != 4) {
					return;
				}

				if (item.hasAttended()) {
					item.setStatusId(2);
					item.setStatus("Confirmed");
				} else {
					item.setStatusId(4);
					item.setStatus("Attended");
				}
				eventService.saveParticipant(item, new Callback<ArrayList<EventRegistrationParticipant>>(false) {
					@Override
					protected void doOnSuccess(ArrayList<EventRegistrationParticipant> result) {
						refresh();
					}
				});
			}
		});

		for (ParticipantColumn col : getDisplayColumns()) {
			switch (col) {
			case ADDED_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, EventRegistrationParticipant>() {
					@Override
					public Date get(EventRegistrationParticipant item) {
						return item.getAddedDate();
					}
				});
				break;
			case FIELDS:
				addWidgetColumn(col, new WidgetCellCreator<EventRegistrationParticipant>() {
					@Override
					protected Widget createWidget(EventRegistrationParticipant item) {
						return new HTML(Formatter.formatNoteText(item.getFieldValues()));
					}
				});
				break;
			case PARTICIPANT_NAME:
				addTextColumn(col, new ValueGetter<String, EventRegistrationParticipant>() {
					@Override
					public String get(EventRegistrationParticipant item) {
						return item.getFirstName() + " " + item.getLastName();
					}
				});
				break;
			case REGISTRANT_NAME:
				addTextColumn(col, new ValueGetter<String, EventRegistrationParticipant>() {
					@Override
					public String get(EventRegistrationParticipant item) {
						return item.getParentFirstName() + " " + item.getParentLastName();
					}
				});
				break;
			case STATUS:
				addTextColumn(col, new ValueGetter<String, EventRegistrationParticipant>() {
					@Override
					public String get(EventRegistrationParticipant item) {
						return item.getStatus();
					}
				});
				break;
			case AGE:
				addNumberColumn(col, new ValueGetter<Number, EventRegistrationParticipant>() {
					@Override
					public Number get(EventRegistrationParticipant item) {
						if (item.getBirthDate() == null) {
							return 0;
						}

						return (int) (ClientDateUtils.daysBetween(item.getBirthDate(), new Date()) / 365);
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
