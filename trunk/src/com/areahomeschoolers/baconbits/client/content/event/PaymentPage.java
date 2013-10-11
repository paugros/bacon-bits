package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentTable;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentTable.AdjustmentColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public final class PaymentPage implements Page {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventParticipantTable table;
	private Label total;
	private SimplePanel payContainer = new SimplePanel();
	private AdjustmentTable adjustments;
	private SimplePanel buttonContainer = new SimplePanel();
	private ClickHandler payClickHandler;
	private Button payPalButton;
	private Button adjustmentButton;

	public PaymentPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Event Payment / Checkout";
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.REGISTRATION_ADDED_BY_ID, Application.getCurrentUser().getId());
		args.put(EventArg.STATUS_ID, 1);

		table = new EventParticipantTable(args);
		table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.TOTALED_PRICE,
				ParticipantColumn.EDIT_STATUS);
		table.disablePaging();
		table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
		table.setWidth("850px");
		buttonContainer.getElement().getStyle().setPaddingTop(10, Unit.PX);

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

		payClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				payPalButton.setEnabled(false);
				adjustmentButton.setEnabled(false);

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

				eventService.payForEvents(Common.asArrayList(table.getSelectedItemIds()), new Callback<PaypalData>() {
					@Override
					protected void doOnFailure(Throwable caught) {
						super.doOnFailure(caught);
						payPalButton.setEnabled(true);
						adjustmentButton.setEnabled(true);
					}

					@Override
					protected void doOnSuccess(PaypalData result) {
						if (result.getAuthorizationUrl() != null) {
							Window.Location.replace(result.getAuthorizationUrl());
						} else {
							HistoryToken.set(PageUrl.user(Application.getCurrentUserId()) + "&tab=1");
						}
					}

				});
			}
		};

		payPalButton = new Button("Check Out With PayPal&trade;", payClickHandler);
		adjustmentButton = new Button("Apply My Adjustments", payClickHandler);

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
					Label l = new Label("Amount due:");
					l.addStyleName("hugeText");
					payPanel.add(l);
					total = new Label();
					total.addStyleName("hugeText");
					payPanel.add(total);
					VerticalPanel pvp = new VerticalPanel();
					pvp.setWidth("100%");
					pvp.add(payPanel);
					pvp.add(buttonContainer);
					pvp.setCellHorizontalAlignment(payPanel, HasHorizontalAlignment.ALIGN_RIGHT);
					pvp.setCellHorizontalAlignment(buttonContainer, HasHorizontalAlignment.ALIGN_RIGHT);

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
		adjustments = new AdjustmentTable(adjustmentArgs);
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
			totalAmount += p.getAdjustedPrice();
		}

		for (Adjustment adjustment : adjustments.getFullList()) {
			totalAmount += adjustment.getAmount();
		}

		// if (Application.isCitrus()) {
		// Label msg = new Label("Payments disabled on this site for now.  Please visit your group(s) site(s) to make payments.");
		// buttonContainer.setWidget(msg);
		// } else
		if (totalAmount < 0) {
			totalAmount = 0;
			buttonContainer.setWidget(adjustmentButton);
		} else if (totalAmount > 0) {
			buttonContainer.setWidget(payPalButton);
		} else {
			buttonContainer.clear();
		}

		total.setText(Formatter.formatCurrency(totalAmount));
	}
}
