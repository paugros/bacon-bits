package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventVolunteerTable.VolunteerColumn;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EventVolunteerTable extends EntityCellTable<Data, EventArg, VolunteerColumn> {
	public enum VolunteerColumn implements EntityCellTableColumn<VolunteerColumn> {
		FULFILLED("Fulfilled"), FULFILLED_READ_ONLY("Fulfilled"), EVENT("Event"), EVENT_DATE("Event Date"), POSITION("Position"), NAME("Name");

		private String title;

		VolunteerColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventVolunteerTable() {
		setTitle("Volunteer Positions");
		setDefaultSortColumn(1, SortDirection.SORT_ASC);
		setDisplayColumns(VolunteerColumn.values());
	}

	public EventVolunteerTable(ArgMap<EventArg> args) {
		this();

		setArgMap(args);

		if (args.getInt(EventArg.USER_ID) > 0) {
			addStatusFilterBox();
		}
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl(true);

		filterBox.addItem("Future");
		filterBox.addItem("Past");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				int index = filterBox.getSelectedIndex();

				if (index == 2) {
					showAllItems();
					return;
				}

				for (Data item : getFullList()) {
					if (index == 0) {
						setItemVisible(item, item.getDate("endDate").after(new Date()), false, false, false);
					} else if (index == 1) {
						setItemVisible(item, item.getDate("endDate").before(new Date()), false, false, false);
					}
				}

				refreshForCurrentState();
			}
		});

		int defaultIndex = 0;
		switch (getArgMap().getStatus()) {
		case ACTIVE:
			defaultIndex = 0;
			break;
		case INACTIVE:
			defaultIndex = 1;
			break;
		case ALL:
			defaultIndex = 2;
			break;
		}

		filterBox.setSelectedIndex(defaultIndex);
	}

	@Override
	protected void fetchData() {
		eventService.getVolunteers(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (VolunteerColumn col : getDisplayColumns()) {
			switch (col) {
			case EVENT:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Data>() {
					@Override
					protected Widget createWidget(Data item) {
						return new DefaultHyperlink(item.get("title"), PageUrl.event(item.getInt("eventId")));
					}
				}, new ValueGetter<String, Data>() {
					@Override
					public String get(Data item) {
						return item.get("title");
					}
				});
				break;
			case EVENT_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, Data>() {
					@Override
					public Date get(Data item) {
						return item.getDate("startDate");
					}
				});
				break;
			case FULFILLED_READ_ONLY:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Data>() {
					@Override
					protected Widget createWidget(Data item) {
						if (item.getBoolean("fulfilled")) {
							return new Image(MainImageBundle.INSTANCE.checkMark());
						}

						return new Label("");
					}
				});
				break;
			case FULFILLED:
				addCheckboxColumn(col.getTitle(), new ValueGetter<Boolean, Data>() {
					@Override
					public Boolean get(Data item) {
						return item.getBoolean("fulfilled");
					}
				}, new FieldUpdater<Data, Boolean>() {
					@Override
					public void update(int index, Data item, Boolean value) {
						eventService.setVolunteerFulFilled(item.getId(), value, new Callback<Void>(false) {
							@Override
							protected void doOnSuccess(Void result) {
							}
						});
					}
				});
				break;
			case NAME:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Data>() {
					@Override
					protected Widget createWidget(Data item) {
						DefaultHyperlink link = new DefaultHyperlink(item.get("firstName") + " " + item.get("lastName"), PageUrl.user(item.getInt("addedById")));
						return link;
					}
				});
				break;
			case POSITION:
				addTextColumn(col, new ValueGetter<String, Data>() {
					@Override
					public String get(Data item) {
						return item.get("jobTitle");
					}
				});
				break;
			default:
				break;
			}
		}
	}

}
