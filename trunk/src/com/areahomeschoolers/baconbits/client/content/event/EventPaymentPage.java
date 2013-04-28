package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentService;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
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
	private PaymentServiceAsync paymentService = (PaymentServiceAsync) ServiceCache.getService(PaymentService.class);
	private EventParticipantCellTable table;
	private Label total;
	private SimplePanel payContainer = new SimplePanel();
	// used to prevent double pay
	private boolean paying;
	private GenericCellTable adjustments;

	public EventPaymentPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Event Payment / Checkout";
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.PARENT_ID_PLUS_SELF, Application.getCurrentUser().getId());
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

		adjustments = new GenericCellTable() {
			@Override
			protected void fetchData() {
				ArgMap<PaymentArg> args = new ArgMap<PaymentArg>(PaymentArg.USER_ID, Application.getCurrentUserId());
				args.put(PaymentArg.ADJUSTMENT_STATUS_ID, 1);
				paymentService.getAdjustments(args, getCallback());
			}

			@Override
			protected void setColumns() {
				addTextColumn("Adjustment type", new ValueGetter<String, Data>() {
					@Override
					public String get(Data item) {
						return item.get("adjustmentSource");
					}
				});

				addTotaledCurrencyColumn("Amount", new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getDouble("amount");
					}
				});
			}
		};
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

		for (Data adjustment : adjustments.getFullList()) {
			totalAmount += adjustment.getDouble("amount");
		}

		total.setText(Formatter.formatCurrency(totalAmount));
	}
}
