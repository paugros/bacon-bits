package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventTable.EventColumn;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class EventTable extends EntityCellTable<Event, EventArg, EventColumn> {
	public enum EventColumn implements EntityCellTableColumn<EventColumn> {
		REGISTERED(""), TITLE("Title"), DESCRIPTION("Description"), START_DATE("Date"), END_DATE("End"), PRICE("Price"), AGES("Ages"), CATEGORY("Category"), REGISTER(
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

	public EventTable(ArgMap<EventArg> args) {
		this();
		setArgMap(args);
	}

	private EventTable() {
		setDefaultSortColumn(EventColumn.START_DATE, SortDirection.SORT_ASC);
		setDisplayColumns(EventColumn.REGISTERED, EventColumn.TITLE, EventColumn.DESCRIPTION, EventColumn.START_DATE, EventColumn.PRICE, EventColumn.AGES,
				EventColumn.REGISTER);
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
			case AGES:
				addTextColumn(col, new ValueGetter<String, Event>() {
					@Override
					public String get(Event item) {
						String text = "";
						if (!Common.isNullOrBlank(item.getAgeRanges())) {
							String[] p = item.getAgeRanges().split(",");
							for (int i = 0; i < p.length; i++) {
								text += p[i].replace("-0", "+");
								if (i < p.length - 1) {
									text += " / ";
								}
							}

						} else {
							text = "All ages";
						}
						return text;
					}
				});
				break;
			case DESCRIPTION:
				addWidgetColumn(col, new WidgetCellCreator<Event>() {
					@Override
					protected Widget createWidget(Event item) {
						HTML h = new HTML();
						h.setHTML(item.getDescription().replaceAll("<br>", " "));
						String text = h.getText().replaceAll("\\s+", " ");
						if (text.length() > 85) {
							text = text.substring(0, 86) + "...";
						}
						h.setTitle(text);

						Label l = new Label(text);
						l.addStyleName("smallText");
						l.setTitle(h.getText());
						return l;
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
								if ("0.00".equals(p[i])) {
									text += "Free";
								} else {
									text += Formatter.formatCurrency(p[i]);
								}
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
						Hyperlink h = new Hyperlink(item.getTitle(), PageUrl.event(item.getId()));
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
				setColumnWidth(addDateTimeColumn(col, new ValueGetter<Date, Event>() {
					@Override
					public Date get(Event item) {
						return item.getStartDate();
					}
				}), "130px");
				break;
			case REGISTER:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Event>() {
					@Override
					protected Widget createWidget(Event item) {
						if (!item.allowRegistrations() || !item.getRequiresRegistration()) {
							String text = "";
							if (item.getRegistrationStartDate().after(new Date())) {
								text = Formatter.formatDate(item.getRegistrationStartDate());
							}
							return new Label(text);
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
