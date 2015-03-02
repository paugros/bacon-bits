package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventTable.EventColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventManagementPage implements Page {
	private ArgMap<EventArg> args = new ArgMap<EventArg>();

	public EventManagementPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Events";
		args.setStatus(Status.ACTIVE);
		final EventTable table = new EventTable(args);
		table.setDisplayColumns(EventColumn.TITLE, EventColumn.DESCRIPTION, EventColumn.START_DATE, EventColumn.LOCATION, EventColumn.TAGS, EventColumn.VIEWS);
		table.setTitle(title);
		table.getTitleBar().addLink(new DefaultHyperlink("Add", PageUrl.event(0)));
		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		table.addStatusFilterBox();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
