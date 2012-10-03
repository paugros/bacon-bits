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
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventParticipantListPage implements Page {
	private String title;
	private TabPage tabPanel = new TabPage();

	public EventParticipantListPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		title = "My Event Registrations";

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
				Label message = new Label(text);
				message.addStyleName("largeText");
				vp.add(message);
				vp.add(new Label(subText));
				page.add(vp);
			}
		}

		tabPanel.add("Registrations", new TabPageCommand() {
			@Override
			public void execute(final VerticalPanel tabBody) {
				ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.INCLUDE_FIELDS);
				args.setStatus(Status.ACTIVE);
				args.put(EventArg.NOT_STATUS_ID, 5);
				args.put(EventArg.PARENT_ID_PLUS_SELF, Application.getCurrentUser().getId());
				final EventParticipantCellTable table = new EventParticipantCellTable(args);
				table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME,
						ParticipantColumn.ADDED_DATE, ParticipantColumn.PRICE, ParticipantColumn.FIELDS, ParticipantColumn.STATUS);

				tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1500PX));
				table.setTitle(title);

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

		tabPanel.add("Volunteer Positions", new TabPageCommand() {
			@Override
			public void execute(final VerticalPanel tabBody) {
				final ArgMap<EventArg> args = new ArgMap<EventArg>();
				args.put(EventArg.USER_ID, Application.getCurrentUserId());

				final EventVolunteerCellTable vt = new EventVolunteerCellTable(args);

				vt.addDataReturnHandler(new DataReturnHandler() {
					@Override
					public void onDataReturn() {
						vt.removeColumn(4);
						vt.removeColumn(3);
						tabPanel.selectTabNow(tabBody);
					}
				});

				tabBody.add(WidgetFactory.newSection(vt, ContentWidth.MAXWIDTH750PX));

				vt.populate();
			}
		});

		page.add(tabPanel);

		Application.getLayout().setPage(title, page);

	}
}
