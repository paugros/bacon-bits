package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventParticipantListPage implements Page {
	public EventParticipantListPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.PARENT_ID, Application.getCurrentUser().getId());
		final String title = "My Event Registrations";
		EventParticipantCellTable table = new EventParticipantCellTable(args);
		table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.ADDED_DATE, ParticipantColumn.PRICE,
				ParticipantColumn.FIELDS, ParticipantColumn.STATUS);
		table.setTitle(title);

		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		page.add(WidgetFactory.newSection(table));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
