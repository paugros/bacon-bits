package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SortDirection;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventParticipantListPage implements Page {
	private SimplePanel payContainer = new SimplePanel();
	private boolean showAll = Url.getBooleanParameter("all");
	private String title;

	public EventParticipantListPage(final VerticalPanel page) {
		if ((showAll && !Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) || !Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		page.add(payContainer);

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

		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.INCLUDE_FIELDS);
		if (showAll) {
			args.setStatus(Status.ACTIVE);
		} else {
			args.put(EventArg.PARENT_ID_PLUS_SELF, Application.getCurrentUser().getId());
		}
		final EventParticipantCellTable table = new EventParticipantCellTable(args);
		if (showAll) {
			table.setDisplayColumns(ParticipantColumn.ATTENDED, ParticipantColumn.EVENT, ParticipantColumn.REGISTRANT_NAME, ParticipantColumn.PARTICIPANT_NAME,
					ParticipantColumn.ADDED_DATE, ParticipantColumn.AGE, ParticipantColumn.PRICE, ParticipantColumn.FIELDS, ParticipantColumn.STATUS,
					ParticipantColumn.EDIT_STATUS);
			table.setDefaultSortColumn(ParticipantColumn.ADDED_DATE, SortDirection.SORT_DESC);
			title = "Recent Event Registrations";
			page.add(WidgetFactory.newSection(table, ContentWidth.maxWidth1150px));
		} else {
			table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.ADDED_DATE, ParticipantColumn.PRICE,
					ParticipantColumn.FIELDS, ParticipantColumn.STATUS);
			title = "My Event Registrations";
			page.add(WidgetFactory.newSection(table, ContentWidth.maxWidth1000px));
		}
		table.setTitle(title);

		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);

				if (!showAll) {
					int payCount = 0;
					for (EventParticipant p : table.getFullList()) {
						if (p.getPrice() > 0 && p.getStatusId() == 1) {
							payCount++;
						}
					}

					if (payCount > 0) {
						Hyperlink l = new Hyperlink("You have " + payCount + " unpaid items. Click here to pay.", PageUrl.eventPayment());
						l.addStyleName("largeText heavyPadding");
						payContainer.setWidget(l);
					} else {
						payContainer.clear();
					}
				}
			}
		});

		table.populate();
	}
}
