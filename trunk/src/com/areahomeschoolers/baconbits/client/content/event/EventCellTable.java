package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventCellTable.EventColumn;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class EventCellTable extends EntityCellTable<Event, EventArg, EventColumn> {
	public enum EventColumn implements EntityCellTableColumn<EventColumn> {
		TITLE("Title"), START_DATE("Start"), END_DATE("End"), CATEGORY("Category");

		private String title;

		EventColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventCellTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	private EventCellTable() {
		setDefaultSortColumn(EventColumn.START_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(EventColumn.TITLE, EventColumn.START_DATE, EventColumn.END_DATE, EventColumn.CATEGORY);
	}

	@Override
	protected void fetchData() {
		eventService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (EventColumn col : getDisplayColumns()) {
			switch (col) {
			case TITLE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Event>() {
					@Override
					protected Widget createWidget(Event item) {
						return new Hyperlink(item.getTitle(), PageUrl.event(item.getId()));
					}
				}, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						return item.getTitle();
					}
				});
				break;
			case CATEGORY:
				addTextColumn(col, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						return item.getCategory();
					}
				});
				break;
			case END_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, Event>() {
					@Override
					public Date get(Event item) {
						return item.getEndDate();
					}
				});
				break;
			case START_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, Event>() {
					@Override
					public Date get(Event item) {
						return item.getStartDate();
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
