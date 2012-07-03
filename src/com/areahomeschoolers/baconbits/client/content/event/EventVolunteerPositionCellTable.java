package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.content.event.EventVolunteerPositionCellTable.EventVolunteerPositionColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

public final class EventVolunteerPositionCellTable extends EntityCellTable<EventVolunteerPosition, EventArg, EventVolunteerPositionColumn> {
	public enum EventVolunteerPositionColumn implements EntityCellTableColumn<EventVolunteerPositionColumn> {
		JOB_TITLE("Job title"), DESCRIPTION("Description"), DISCOUNT("Discount"), POSITIONS("Number needed");

		private String title;

		EventVolunteerPositionColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	// private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventVolunteerPositionCellTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	private EventVolunteerPositionCellTable() {
		setDefaultSortColumn(EventVolunteerPositionColumn.JOB_TITLE, SortDirection.SORT_ASC);
		setDisplayColumns(EventVolunteerPositionColumn.JOB_TITLE, EventVolunteerPositionColumn.DESCRIPTION, EventVolunteerPositionColumn.DISCOUNT,
				EventVolunteerPositionColumn.POSITIONS);
	}

	@Override
	protected void fetchData() {
		// eventService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (EventVolunteerPositionColumn col : getDisplayColumns()) {
			switch (col) {
			case JOB_TITLE:
				addTextColumn(col, new ValueGetter<String, EventVolunteerPosition>() {
					@Override
					public String get(EventVolunteerPosition item) {
						return item.getJobTitle();
					}
				});
				break;
			case DESCRIPTION:
				addTextColumn(col, new ValueGetter<String, EventVolunteerPosition>() {
					@Override
					public String get(EventVolunteerPosition item) {
						return item.getDescription();
					}
				});
				break;
			case DISCOUNT:
				addCurrencyColumn(col, new ValueGetter<Double, EventVolunteerPosition>() {
					@Override
					public Double get(EventVolunteerPosition item) {
						return item.getDiscount();
					}
				});
				break;
			case POSITIONS:
				addNumberColumn(col, new ValueGetter<Number, EventVolunteerPosition>() {
					@Override
					public Number get(EventVolunteerPosition item) {
						return item.getPositionCount();
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
