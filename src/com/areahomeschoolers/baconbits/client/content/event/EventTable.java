package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventTable.EventColumn;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class EventTable extends EntityCellTable<Event, EventArg, EventColumn> {
	public enum EventColumn implements EntityCellTableColumn<EventColumn> {
		REGISTERED(""), TITLE("Title"), DESCRIPTION("Description"), START_DATE("Date"), END_DATE("End"), LOCATION("Location"), TAGS("Tags"), CATEGORY(
				"Category"), VIEWS("Views");

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

	public EventTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	private EventTable() {
		setDefaultSortColumn(EventColumn.START_DATE, SortDirection.SORT_ASC);
		setDisplayColumns(EventColumn.TITLE, EventColumn.DESCRIPTION, EventColumn.START_DATE, EventColumn.LOCATION, EventColumn.TAGS, EventColumn.VIEWS);
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl(false);
		filterBox.addItem("Future");
		filterBox.addItem("Past");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				getArgMap().remove(EventArg.SHOW_INACTIVE);

				switch (filterBox.getSelectedIndex()) {
				case 0:
					getArgMap().setStatus(Status.ACTIVE);
					break;
				case 1:
					getArgMap().setStatus(Status.INACTIVE);
					break;
				case 2:
					getArgMap().setStatus(Status.ALL);
					if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
						getArgMap().put(EventArg.SHOW_INACTIVE);
					}
					break;
				}

				populate();
			}
		});

		filterBox.setSelectedIndex(0);
	}

	@Override
	protected void fetchData() {
		eventService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (EventColumn col : getDisplayColumns()) {
			switch (col) {
			case VIEWS:
				addNumberColumn(col, new ValueGetter<Number, Event>() {
					@Override
					public Number get(Event item) {
						return item.getViewCount();
					}
				});
				break;
			case REGISTERED:
				if (Application.isAuthenticated()) {
					addWidgetColumn(col, new WidgetCellCreator<Event>() {
						@Override
						protected Widget createWidget(Event item) {
							if (item.getCurrentUserParticipantCount() == 0) {
								return new Label();
							}

							return new Image(MainImageBundle.INSTANCE.checkMark());
						}
					});
				}
				break;
			case TAGS:
				addTextColumn(col, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						return item.getTags();
					}
				});
				break;
			case DESCRIPTION:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Event>() {
					@Override
					protected Widget createWidget(final Event item) {
						final ClickLabel preview = new ClickLabel("Preview");
						preview.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								DecoratedPopupPanel p = new DecoratedPopupPanel(true);
								p.setModal(true);
								int spHeight = 350;
								MaxHeightScrollPanel sp = new MaxHeightScrollPanel(spHeight);
								sp.alwaysUseMaxHeight(true);
								HTML h = new HTML(item.getDescription());
								h.setWidth("500px");
								sp.setWidget(h);
								p.setWidget(sp);
								int y = event.getY();
								if (y + spHeight + 15 > Window.getClientHeight()) {
									y -= spHeight + 15;
								}
								p.setPopupPosition(event.getX(), y);
								p.show();
							}
						});

						return preview;
					}
				});
				break;
			case LOCATION:
				addTextColumn(col, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						String text = "";
						if (!Common.isNullOrBlank(item.getCity())) {
							text += item.getCity();
							if (!Common.isNullOrBlank(item.getState())) {
								text += ", ";
							}
						}
						if (!Common.isNullOrBlank(item.getState())) {
							text += item.getState();
						}
						return text;
					}
				});
				break;
			case TITLE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Event>() {
					@Override
					protected Widget createWidget(Event item) {
						DefaultHyperlink h = new DefaultHyperlink(item.getTitle(), PageUrl.event(item.getId()));

						h.addStyleName("bold");

						if (!item.isNewlyAdded()) {
							return h;
						}

						PaddedPanel pp = new PaddedPanel();
						pp.add(h);
						Label l = new Label("NEW!");
						l.addStyleName("errorText bold smallText");
						pp.add(l);
						return pp;
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
				setColumnWidth(addDateTimeColumn(col, new ValueGetter<Date, Event>() {
					@Override
					public Date get(Event item) {
						return item.getEndDate();
					}
				}), "130px");
				break;
			case START_DATE:
				setColumnWidth(addDateColumn(col, new ValueGetter<Date, Event>() {
					@Override
					public Date get(Event item) {
						return item.getStartDate();
					}
				}), "130px");
				break;
			default:
				new AssertionError();
				break;
			}
		}

	}
}
