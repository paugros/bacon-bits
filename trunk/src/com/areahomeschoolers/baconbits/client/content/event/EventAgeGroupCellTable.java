package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventAgeGroupCellTable.EventAgeGroupColumn;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Widget;

public final class EventAgeGroupCellTable extends EntityCellTable<EventAgeGroup, EventArg, EventAgeGroupColumn> {
	public enum EventAgeGroupColumn implements EntityCellTableColumn<EventAgeGroupColumn> {
		AGE_RANGE("Age range"), PARTICIPANTS("Min/Max participants"), PRICE("Price");

		private String title;

		EventAgeGroupColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	// private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private AgeGroupEditDialog dialog = new AgeGroupEditDialog(this);

	public EventAgeGroupCellTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	private EventAgeGroupCellTable() {
		setDefaultSortColumn(EventAgeGroupColumn.AGE_RANGE, SortDirection.SORT_ASC);
		setDisplayColumns(EventAgeGroupColumn.AGE_RANGE, EventAgeGroupColumn.PARTICIPANTS, EventAgeGroupColumn.PRICE);

		dialog.setText("Edit Age Group");
	}

	private String getRangeText(EventAgeGroup item) {
		String range = Integer.toString(item.getMinimumAge());
		if (item.getMaximumAge() == 0) {
			range += "+";
		} else {
			range += "-" + item.getMaximumAge();
		}

		return range;
	}

	@Override
	protected void fetchData() {
		// eventService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (EventAgeGroupColumn col : getDisplayColumns()) {
			switch (col) {
			case AGE_RANGE:
				if (Application.isAdministrator()) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<EventAgeGroup>() {
						@Override
						protected Widget createWidget(final EventAgeGroup item) {
							return new ClickLabel(getRangeText(item), new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
									dialog.center(item);
								}
							});
						}
					});
				} else {
					addTextColumn(col, new ValueGetter<String, EventAgeGroup>() {
						@Override
						public String get(EventAgeGroup item) {
							return getRangeText(item);
						}
					});
				}
				break;
			case PARTICIPANTS:
				addTextColumn(col, new ValueGetter<String, EventAgeGroup>() {
					@Override
					public String get(EventAgeGroup item) {
						String range = "";

						if (item.getMinimumParticipants() > 0) {
							range += "Min " + item.getMinimumParticipants();
							if (item.getMaximumParticipants() > 0) {
								range += " / ";
							}
						}

						if (item.getMaximumParticipants() > 0) {
							range += "Max " + item.getMaximumParticipants();
						}

						return range;
					}
				});
				break;
			case PRICE:
				addCurrencyColumn(col, new ValueGetter<Double, EventAgeGroup>() {
					@Override
					public Double get(EventAgeGroup item) {
						return item.getPrice();
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
