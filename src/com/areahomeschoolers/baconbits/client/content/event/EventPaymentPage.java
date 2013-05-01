package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentCellTable;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentCellTable.AdjustmentColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public final class EventPaymentPage implements Page {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventParticipantCellTable table;
	private Label total;
	private SimplePanel payContainer = new SimplePanel();
	// used to prevent double pay
	private boolean paying;
	private AdjustmentCellTable adjustments;

	public EventPaymentPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Event Payment / Checkout";
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.REGISTRATION_ADDED_BY_ID, Application.getCurrentUser().getId());
		args.put(EventArg.STATUS_ID, 1);

		table = new EventParticipantCellTable(args);
		table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.TOTALED_PRICE,
				ParticipantColumn.EDIT_STATUS);
		table.disablePaging();
		table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
		table.setWidth("850px");

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
		page.add(WidgetFactory.wrapForWidth(payContainer, ContentWidth.MAXWIDTH750PX));
		page.setCellHorizontalAlignment(payContainer, HasHorizontalAlignment.ALIGN_RIGHT);

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				payContainer.clear();

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
					VerticalPanel pvp = new VerticalPanel();
					pvp.setWidth("100%");
					pvp.add(payPanel);
					Image logo = new Image(MainImageBundle.INSTANCE.paypalButton());
					logo.getElement().getStyle().setCursor(Cursor.POINTER);
					logo.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							// list of selected items
							List<EventParticipant> items = table.getSelectedItems();

							// at least one is required
							if (items.isEmpty()) {
								AlertDialog.alert("Please select at least one item.");
								return;
							}

							Map<Integer, Boolean> states = new HashMap<Integer, Boolean>();
							for (EventParticipant p : table.getFullList()) {
								if (p.getRequiredInSeries()) {
									// required series events for the same series must be all of the same state
									if (!states.containsKey(p.getEventSeriesId())) {
										states.put(p.getEventSeriesId(), items.contains(p));
									} else {
										if (!states.get(p.getEventSeriesId()).equals(items.contains(p))) {
											AlertDialog.alert("Event dates in the following series must be paid for together: " + p.getEventTitle());
											return;
										}
									}
								}
							}

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
									if (result.getAuthorizationUrl() != null) {
										Window.Location.replace(result.getAuthorizationUrl());
									} else {
										HistoryToken.set(PageUrl.eventParticipantList());
									}
								}

							});
						}
					});
					pvp.add(logo);

					pvp.setCellHorizontalAlignment(payPanel, HasHorizontalAlignment.ALIGN_RIGHT);
					pvp.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_RIGHT);
					payContainer.setWidget(pvp);
				}

				Application.getLayout().setPage(title, page);
			}
		});

		table.setCancelHandler(new ParameterHandler<EventParticipant>() {
			@Override
			public void execute(EventParticipant item) {
				table.removeItem(item);
				table.refresh();
				updateTotal();
			}
		});

		table.populate();

		ArgMap<PaymentArg> adjustmentArgs = new ArgMap<PaymentArg>(PaymentArg.USER_ID, Application.getCurrentUserId());
		adjustmentArgs.put(PaymentArg.STATUS_ID, 1);
		adjustments = new AdjustmentCellTable(adjustmentArgs);
		adjustments.setDisplayColumns(AdjustmentColumn.TYPE, AdjustmentColumn.TOTALED_AMOUNT);
		adjustments.setWidth("400px");

		adjustments.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				if (!adjustments.getFullList().isEmpty()) {
					updateTotal();
					if (!adjustments.isAttached()) {
						vp.add(adjustments);
						vp.setCellHorizontalAlignment(adjustments, HasHorizontalAlignment.ALIGN_RIGHT);
					}
				}
			}
		});

		adjustments.populate();
	}

	private void updateTotal() {
		if (total == null) {
			return;
		}

		double totalAmount = 0.00;
		for (EventParticipant p : table.getSelectedItems()) {
			totalAmount += p.getPrice();
		}

		for (Adjustment adjustment : adjustments.getFullList()) {
			totalAmount += adjustment.getAmount();
		}

		total.setText(Formatter.formatCurrency(totalAmount));
	}
}
