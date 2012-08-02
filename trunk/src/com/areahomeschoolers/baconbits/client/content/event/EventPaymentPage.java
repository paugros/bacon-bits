package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public final class EventPaymentPage implements Page {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventParticipantCellTable table;
	private Label total;
	// used to prevent double pay
	private boolean paying;

	public EventPaymentPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Event Payment / Checkout";
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.PARENT_ID_PLUS_SELF, Application.getCurrentUser().getId());
		args.put(EventArg.ONLY_PAYABLE_PARTICIPANTS);
		args.put(EventArg.STATUS_ID, 1);

		table = new EventParticipantCellTable(args);
		table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.PRICE,
				ParticipantColumn.EDIT_STATUS);
		table.disablePaging();
		table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
		table.setWidth("750px");

		final VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(15);

		Label header = new Label(title);
		header.addStyleName("hugeText");
		Label subHeader = new Label("Select the events you wish to pay for");

		VerticalPanel headerPanel = new VerticalPanel();
		headerPanel.add(header);
		headerPanel.add(subHeader);

		vp.add(headerPanel);
		vp.add(table);

		page.add(vp);

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				table.setSelectedItems(table.getFullList());
				updateTotal();
				table.getSelectionModel().addSelectionChangeHandler(new Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						updateTotal();
					}
				});

				if (!Common.isNullOrEmpty(table.getFullList())) {
					PaddedPanel payPanel = new PaddedPanel(15);
					Label l = new Label("Total:");
					l.addStyleName("hugeText");
					payPanel.add(l);
					total = new Label();
					total.addStyleName("hugeText");
					payPanel.add(total);
					vp.add(payPanel);
					Image logo = new Image(MainImageBundle.INSTANCE.paypalButton());
					logo.getElement().getStyle().setCursor(Cursor.POINTER);
					logo.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (paying) {
								return;
							}

							paying = true;

							eventService.payForEvents(Common.asArrayList(table.getSelectedItemIds()), new Callback<PaypalData>() {
								@Override
								protected void doOnFailure(Throwable caught) {
									super.doOnFailure(caught);
									paying = false;
								}

								@Override
								protected void doOnSuccess(PaypalData result) {
									Window.Location.replace(result.getAuthorizationUrl());
								}

							});
						}
					});
					vp.add(logo);

					vp.setCellHorizontalAlignment(payPanel, HasHorizontalAlignment.ALIGN_RIGHT);
					vp.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_RIGHT);
				}

				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}

	private void updateTotal() {
		if (total == null) {
			return;
		}

		double totalAmount = 0.00;
		for (EventParticipant p : table.getSelectedItems()) {
			totalAmount += p.getPrice();
		}

		total.setText(Formatter.formatCurrency(totalAmount));
	}
}
