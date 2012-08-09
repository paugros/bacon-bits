package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventParticipantListPage implements Page {
	private String title;

	public EventParticipantListPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		EventBalanceBox eb = new EventBalanceBox();
		page.add(WidgetFactory.wrapForWidth(eb, ContentWidth.MAXWIDTH1500PX));
		eb.populate();
		page.setCellHorizontalAlignment(eb, HasHorizontalAlignment.ALIGN_RIGHT);

		String paymentAction = Url.getParameter("ps");
		if (paymentAction != null) {
			String text = null;
			String subText = "";
			if ("return".equals(paymentAction)) {
				text = "Thank you for your purchase.";
				subText = "Below are the events you've registered to attend. Payments may take a few minutes to be reflected here.";
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

		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.INCLUDE_FIELDS);
		args.put(EventArg.NOT_STATUS_ID, 5);
		args.put(EventArg.PARENT_ID_PLUS_SELF, Application.getCurrentUser().getId());
		final EventParticipantCellTable table = new EventParticipantCellTable(args);
		table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.ADDED_DATE,
				ParticipantColumn.PRICE, ParticipantColumn.FIELDS, ParticipantColumn.STATUS);
		title = "My Event Registrations";
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1500PX));
		table.setTitle(title);

		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
