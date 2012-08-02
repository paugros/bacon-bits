package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventParticipantListPage implements Page {
	public EventParticipantListPage(final VerticalPanel page) {
		String paymentAction = Url.getParameter("ps");
		if (paymentAction != null) {
			String text = null;
			String subText = "";
			if ("return".equals(paymentAction)) {
				text = "Thank you for your purchase.";
				subText = "Below are the events you've registered to attend. Any events in Registered/Pending status are still awaiting payment.";
			} else if ("cancel".equals(paymentAction)) {
				text = "We're sorry you canceled your purchase.";
				subText += "You can change your mind at any time.";
			}

			if (text != null) {
				VerticalPanel vp = new VerticalPanel();
				vp.addStyleName("heavyPadding");
				Label message = new Label(text);
				message.addStyleName("largeText");
				vp.add(message);
				vp.add(new Label(subText));
				page.add(vp);
			}
		}

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
		page.add(WidgetFactory.newSection(table, "1000px"));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
