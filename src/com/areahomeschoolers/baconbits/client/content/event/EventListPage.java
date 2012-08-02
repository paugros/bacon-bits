package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventListPage implements Page {
	public EventListPage(final VerticalPanel page) {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		final String title = "Events";
		EventCellTable table = new EventCellTable(args);
		table.setTitle(title);
		if (Application.isAuthenticated()) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.event(0));
			table.getTitleBar().addLink(addLink);
		}

		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		page.add(WidgetFactory.newSection(table));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});
		table.setWidth("1000px");

		table.populate();
	}
}
