package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class EventVolunteerCellTable extends GenericCellTable {
	private ArgMap<EventArg> args;
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventVolunteerCellTable(ArgMap<EventArg> args) {
		this.args = args;

		setDefaultSortColumn(1, SortDirection.SORT_ASC);
		setTitle("Volunteer Positions");

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
		switch (args.getStatus()) {
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
		eventService.getVolunteers(args, getCallback());
	}

	@Override
	protected void setColumns() {
		addCompositeWidgetColumn("Event", new WidgetCellCreator<Data>() {
			@Override
			protected Widget createWidget(Data item) {
				return new Hyperlink(item.get("title"), PageUrl.event(item.getInt("eventId")));
			}
		}, new ValueGetter<String, Data>() {
			@Override
			public String get(Data item) {
				return item.get("title");
			}
		});

		addDateTimeColumn("Event date", new ValueGetter<Date, Data>() {
			@Override
			public Date get(Data item) {
				return item.getDate("startDate");
			}
		});

		addTextColumn("Position", new ValueGetter<String, Data>() {
			@Override
			public String get(Data item) {
				return item.get("jobTitle");
			}
		});

		addCompositeWidgetColumn("Name", new WidgetCellCreator<Data>() {
			@Override
			protected Widget createWidget(Data item) {
				Hyperlink link = new Hyperlink(item.get("firstName") + " " + item.get("lastName"), PageUrl.user(item.getInt("addedById")));
				return link;
			}
		});

		addCheckboxColumn("Fulfilled", new ValueGetter<Boolean, Data>() {
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
	}

}
