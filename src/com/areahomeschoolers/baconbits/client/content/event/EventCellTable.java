package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventCellTable.EventColumn;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class EventCellTable extends EntityCellTable<Event, EventArg, EventColumn> {
	public enum EventColumn implements EntityCellTableColumn<EventColumn> {
		REGISTERED(""), TITLE("Title"), DESCRIPTION("Description"), START_DATE("Start"), END_DATE("End"), PRICE("Price"), CATEGORY("Category"), REGISTER(
				"Register");

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
		setDefaultSortColumn(EventColumn.START_DATE, SortDirection.SORT_ASC);
		setDisplayColumns(EventColumn.REGISTERED, EventColumn.TITLE, EventColumn.DESCRIPTION, EventColumn.START_DATE, EventColumn.END_DATE, EventColumn.PRICE,
				EventColumn.CATEGORY, EventColumn.REGISTER);
	}

	@Override
	protected void fetchData() {
		eventService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (EventColumn col : getDisplayColumns()) {
			switch (col) {
			case REGISTERED:
				if (Application.isAuthenticated()) {
					addWidgetColumn(col, new WidgetCellCreator<Event>() {
						@Override
						protected Widget createWidget(Event item) {
							if (item.getCurrentUserParticipantCount() == 0) {
								return new Label();
							}

							PaddedPanel pp = new PaddedPanel();
							Image check = new Image(MainImageBundle.INSTANCE.checkMark());
							pp.add(check);
							pp.add(new Label(Integer.toString(item.getCurrentUserParticipantCount())));
							return pp;
						}
					});
				}
				break;
			case DESCRIPTION:
				addTextColumn(col, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						HTML h = new HTML();
						h.setHTML(item.getDescription().replaceAll("<br>", " "));
						String text = h.getText();
						if (text.length() > 100) {
							text = text.substring(0, 101) + "...";
						}
						return text;
					}
				});
				break;
			case PRICE:
				addTextColumn(col, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						String text = "";
						if (!Common.isNullOrBlank(item.getAgePrices())) {
							String[] p = item.getAgePrices().split(",");
							for (int i = 0; i < p.length; i++) {
								text += Formatter.formatCurrency(p[i]);
								if (i < p.length - 1) {
									text += " / ";
								}
							}

						} else {
							if (item.getPrice() == 0) {
								text = "Free";
							} else {
								text = Formatter.formatCurrency(item.getPrice());
							}
						}
						return text;
					}
				});
				break;
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
			case REGISTER:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Event>() {
					@Override
					protected Widget createWidget(Event item) {
						if (!item.allowRegistrations() || !item.getRequiresRegistration()) {
							return new Label("");
						}

						return new Hyperlink("Register", PageUrl.event(item.getId()) + "&tab=1");
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
