package com.areahomeschoolers.baconbits.client.content.event;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SortDirection;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegistrationManagementPage implements Page {
	private VerticalPanel page;
	private TabPage tabPanel = new TabPage();
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public RegistrationManagementPage(VerticalPanel p) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = p;

		tabPanel.add("Registration Summary", new TabPageCommand() {
			@Override
			public void execute(final VerticalPanel tabBody) {
				GenericCellTable table = new GenericCellTable() {
					@Override
					protected void fetchData() {
						eventService.getRegistrationSummary(getCallback());
					}

					@Override
					protected void setColumns() {
						addCompositeWidgetColumn("Event", new WidgetCellCreator<Data>() {
							@Override
							protected Widget createWidget(Data item) {
								return new Hyperlink(item.get("title"), PageUrl.event(item.getId()));
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

						addWidgetColumn("Age range", new WidgetCellCreator<Data>() {
							@Override
							protected Widget createWidget(Data item) {
								return new HTML(Formatter.formatNoteText(item.get("ageText")));
							}
						});

						addWidgetColumn("Min/max", new WidgetCellCreator<Data>() {
							@Override
							protected Widget createWidget(Data item) {
								return new HTML(Formatter.formatNoteText(item.get("minMaxText")));
							}
						});

						addWidgetColumn("Actual", new WidgetCellCreator<Data>() {
							@Override
							protected Widget createWidget(Data item) {
								return new HTML(Formatter.formatNoteText(item.get("countText")));
							}
						});

						addWidgetColumn("Waiting", new WidgetCellCreator<Data>() {
							@Override
							protected Widget createWidget(Data item) {
								return new HTML(Formatter.formatNoteText(item.get("waitText")));
							}
						});
					}
				};

				table.setDefaultSortColumn("Event date", SortDirection.SORT_ASC);
				table.setTitle("Registration Summary");
				table.populate();

				tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));

				tabPanel.selectTabNow(tabBody);
			}
		});

		tabPanel.add("All Active Registrations", new TabPageCommand() {
			@Override
			public void execute(final VerticalPanel tabBody) {
				ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.INCLUDE_FIELDS);
				args.setStatus(Status.ACTIVE);
				final EventParticipantCellTable table = new EventParticipantCellTable(args);
				table.setDisplayColumns(ParticipantColumn.ATTENDED, ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.REGISTRANT_NAME,
						ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.ADDED_DATE, ParticipantColumn.AGE, ParticipantColumn.PRICE,
						ParticipantColumn.FIELDS, ParticipantColumn.STATUS, ParticipantColumn.EDIT_STATUS);
				table.setDefaultSortColumn(ParticipantColumn.ADDED_DATE, SortDirection.SORT_DESC);
				tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1500PX));
				table.setTitle("Recent Event Registrations");

				table.addStatusFilterBox();
				table.getTitleBar().addExcelControl();
				table.getTitleBar().addSearchControl();

				table.addDataReturnHandler(new DataReturnHandler() {
					@Override
					public void onDataReturn() {
						tabPanel.selectTabNow(tabBody);
					}
				});

				table.populate();
			}
		});

		tabPanel.add("Filled Volunteer Positions", new TabPageCommand() {
			@Override
			public void execute(final VerticalPanel tabBody) {
				final ArgMap<EventArg> args = new ArgMap<EventArg>();
				args.setStatus(Status.ACTIVE);

				EventVolunteerCellTable vt = new EventVolunteerCellTable(args);

				vt.addDataReturnHandler(new DataReturnHandler() {
					@Override
					public void onDataReturn() {
						tabPanel.selectTabNow(tabBody);
					}
				});

				tabBody.add(WidgetFactory.newSection(vt, ContentWidth.MAXWIDTH1200PX));

				vt.populate();
			}
		});

		page.add(tabPanel);

		Application.getLayout().setPage("Registration Management", page);
	}
}
