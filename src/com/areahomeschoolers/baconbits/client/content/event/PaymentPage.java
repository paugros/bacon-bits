package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookDetailsDialog;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentTable;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentTable.AdjustmentColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public final class PaymentPage implements Page {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private EventParticipantTable eventTable;
	private FieldTable eventTotalTable;
	private SimplePanel eventTotalContainer = new SimplePanel();
	private AdjustmentTable adjustments;
	private SimplePanel eventButtonContainer = new SimplePanel();
	private ClickHandler eventPayClickHandler;
	private Button eventPayPalButton;
	private Button adjustmentButton;
	private FlexTable bookTable = new FlexTable();
	private HorizontalPanel bookTotal = new HorizontalPanel();
	private List<Book> books;
	private VerticalPanel eventVp = new VerticalPanel();
	private VerticalPanel bookVp = new VerticalPanel();
	private boolean noEvents;
	private boolean noBooks;
	private VerticalPanel page;

	public PaymentPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;
		page.add(bookVp);
		page.add(WidgetFactory.wrapForWidth(eventVp, ContentWidth.MAXWIDTH900PX));

		addBookSection();

		addEventSection();

		Application.getLayout().setPage("Shopping Cart / Checkout", page);
	}

	private void addBookSection() {
		bookTotal.addStyleName("largeText");
		bookTotal.setWidth("250px");

		ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
		args.put(BookArg.IN_MY_CART, true);
		final int cols = 4;
		bookTable.setCellPadding(5);
		bookTable.setWidth("650px");

		bookService.list(args, new Callback<ArrayList<Book>>() {
			@Override
			protected void doOnSuccess(ArrayList<Book> result) {
				books = result;

				if (result.isEmpty()) {
					noBooks = true;
					displayEmptyMessageIfNeeded();
					return;
				}

				bookVp.clear();
				bookTable.removeAllRows();

				Label header = new Label("Book Payment / Checkout");
				header.addStyleName("hugeText");
				bookVp.setSpacing(15);

				bookVp.add(header);
				bookVp.add(bookTable);

				int lastUserId = 0;

				for (final Book b : result) {
					int row = bookTable.getRowCount();

					if (lastUserId != b.getUserId()) {
						Label ship = new Label("Shipping from " + b.getShippingFrom());
						ship.addStyleName("bold");
						bookTable.setWidget(row, 0, ship);
						bookTable.getFlexCellFormatter().setColSpan(row, 0, cols);
						row++;
						lastUserId = b.getUserId();
					}

					Image image = new Image(Constants.DOCUMENT_URL_PREFIX + b.getSmallImageId());
					bookTable.setWidget(row, 0, image);

					ClickLabel title = new ClickLabel(b.getTitle(), new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							new BookDetailsDialog(b).center();
						}
					});

					bookTable.setWidget(row, 1, title);

					bookTable.setText(row, 2, Formatter.formatCurrency(b.getPrice()));
					bookTable.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

					ClickLabel delete = new ClickLabel("X", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							ConfirmDialog.confirm("Remove the following book from your cart: " + b.getTitle() + "?", new ConfirmHandler() {
								@Override
								public void onConfirm() {
									bookService.removeBookFromCart(b.getId(), Application.getCurrentUserId(), new Callback<Void>(false) {
										@Override
										protected void doOnSuccess(Void result) {
											addBookSection();
										}
									});
								}
							});
						}
					});

					bookTable.setWidget(row, 3, delete);
				}

				int lastRow = bookTable.getRowCount();
				bookTable.setWidget(lastRow, 0, bookTotal);
				bookTable.getFlexCellFormatter().setColSpan(lastRow, 0, 3);
				bookTable.getFlexCellFormatter().setHorizontalAlignment(lastRow, 0, HasHorizontalAlignment.ALIGN_RIGHT);

				bookTotal.clear();

				double total = 0.00;

				for (Book b : books) {
					total += b.getPrice();
				}

				bookTotal.add(new Label("Amount due"));
				bookTotal.add(new Label(Formatter.formatCurrency(total)));
				bookTotal.setCellHorizontalAlignment(bookTotal.getWidget(1), HasHorizontalAlignment.ALIGN_RIGHT);

				Button bookPayPalButton = new Button("Check Out With PayPal&trade;", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {

					}
				});

				lastRow++;
				bookTable.setWidget(lastRow, 0, bookPayPalButton);
				bookTable.getFlexCellFormatter().setColSpan(lastRow, 0, 3);
				bookTable.getFlexCellFormatter().setHorizontalAlignment(lastRow, 0, HasHorizontalAlignment.ALIGN_RIGHT);
			}
		});
	}

	private void addEventSection() {
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.REGISTRATION_ADDED_BY_ID, Application.getCurrentUser().getId());
		args.put(EventArg.STATUS_ID, 1);

		eventTable = new EventParticipantTable(args);
		eventTable.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME, ParticipantColumn.PRICE,
				ParticipantColumn.EDIT_STATUS);
		eventTable.disablePaging();
		eventTable.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
		eventTable.setWidth("900px");
		eventButtonContainer.getElement().getStyle().setPaddingTop(10, Unit.PX);

		eventPayClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventPayPalButton.setEnabled(false);
				adjustmentButton.setEnabled(false);

				// list of selected items
				List<EventParticipant> items = eventTable.getSelectedItems();

				// at least one is required
				if (items.isEmpty()) {
					AlertDialog.alert("Please select at least one item.");
					return;
				}

				Map<Integer, Boolean> states = new HashMap<Integer, Boolean>();
				for (EventParticipant p : eventTable.getFullList()) {
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

				eventService.payForEvents(Common.asArrayList(eventTable.getSelectedItemIds()), new Callback<PaypalData>() {
					@Override
					protected void doOnFailure(Throwable caught) {
						super.doOnFailure(caught);
						eventPayPalButton.setEnabled(true);
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

		eventPayPalButton = new Button("Check Out With PayPal&trade;", eventPayClickHandler);
		adjustmentButton = new Button("Apply My Adjustments", eventPayClickHandler);

		eventTable.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				if (Common.isNullOrEmpty(eventTable.getFullList())) {
					noEvents = true;
					displayEmptyMessageIfNeeded();
					return;
				}

				eventVp.clear();

				eventVp.setSpacing(15);

				Label header = new Label("Event Payment / Checkout");
				header.addStyleName("hugeText");
				Label subHeader = new Label("Select the events you wish to pay for");

				VerticalPanel headerPanel = new VerticalPanel();
				headerPanel.add(header);
				headerPanel.add(subHeader);

				eventVp.add(headerPanel);
				eventVp.add(eventTable);

				eventVp.add(eventTotalContainer);
				eventVp.setCellHorizontalAlignment(eventTotalContainer, HasHorizontalAlignment.ALIGN_RIGHT);

				eventTotalContainer.clear();

				eventTable.setSelectedItems(eventTable.getFullList());
				updateEventTotal();
				eventTable.getSelectionModel().addSelectionChangeHandler(new Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						updateEventTotal();
					}
				});

				if (!Common.isNullOrEmpty(eventTable.getFullList())) {
					eventTotalTable = new FieldTable();
					eventTotalTable.setWidth("250px");
					eventTotalTable.removeStyleName(eventTotalTable.getStylePrimaryName());
					VerticalPanel pvp = new VerticalPanel();
					pvp.setWidth("100%");
					pvp.add(eventTotalTable);
					pvp.add(eventButtonContainer);
					pvp.setCellHorizontalAlignment(eventTotalTable, HasHorizontalAlignment.ALIGN_RIGHT);
					pvp.setCellHorizontalAlignment(eventButtonContainer, HasHorizontalAlignment.ALIGN_RIGHT);

					eventTotalContainer.setWidget(pvp);
				}
			}
		});

		eventTable.setCancelHandler(new ParameterHandler<EventParticipant>() {
			@Override
			public void execute(EventParticipant item) {
				eventTable.removeItem(item);
				eventTable.refresh();
				updateEventTotal();
			}
		});

		eventTable.populate();

		ArgMap<PaymentArg> adjustmentArgs = new ArgMap<PaymentArg>(PaymentArg.USER_ID, Application.getCurrentUserId());
		adjustmentArgs.put(PaymentArg.STATUS_ID, 1);
		adjustments = new AdjustmentTable(adjustmentArgs);
		adjustments.setDisplayColumns(AdjustmentColumn.TYPE, AdjustmentColumn.TOTALED_AMOUNT);
		adjustments.setWidth("400px");

		adjustments.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				if (!adjustments.getFullList().isEmpty()) {
					updateEventTotal();
					if (!adjustments.isAttached()) {
						eventVp.add(adjustments);
						eventVp.setCellHorizontalAlignment(adjustments, HasHorizontalAlignment.ALIGN_RIGHT);
					}
				}
			}
		});

		adjustments.populate();
	}

	private void displayEmptyMessageIfNeeded() {
		if (noBooks && noEvents) {
			PaddedPanel empty = new PaddedPanel();
			empty.addStyleName("largeText heavyPadding");
			empty.add(new Label("Your cart is empty..."));
			Image cart = new Image(MainImageBundle.INSTANCE.shoppingCart());
			empty.add(cart);
			empty.getElement().getStyle().setMarginLeft(35, Unit.PX);
			empty.getElement().getStyle().setMarginBottom(100, Unit.PX);
			page.add(empty);
		}
	}

	private void updateEventTotal() {
		if (eventTotalTable == null) {
			return;
		}

		eventTotalTable.removeAllRows();

		double totalAmount = 0.00;
		double amountDue = 0.00;
		double totalAdjustments = 0.00;

		for (EventParticipant p : eventTable.getSelectedItems()) {
			totalAmount += p.getAdjustedPrice();
		}

		for (Adjustment adjustment : adjustments.getFullList()) {
			totalAdjustments += adjustment.getAmount();
		}

		amountDue = totalAmount + totalAdjustments;

		if (totalAmount < 0) {
			totalAmount = 0;
			eventButtonContainer.setWidget(adjustmentButton);
		} else if (totalAmount > 0) {
			eventButtonContainer.setWidget(eventPayPalButton);
		} else {
			eventButtonContainer.clear();
		}

		if (totalAdjustments != 0) {
			eventTotalTable.addField(new Label("Sub-total"), Formatter.formatCurrency(totalAmount));
			eventTotalTable.addField(new Label("Adjustments"), Formatter.formatCurrency(totalAdjustments));
		}

		Label l = new Label("Amount due");
		Label v = new Label(Formatter.formatCurrency(amountDue));
		l.addStyleName("largeText");
		v.addStyleName("largeText");
		eventTotalTable.addField(l, v);

		for (int i = 0; i < eventTotalTable.getFlexTable().getRowCount(); i++) {
			eventTotalTable.getFlexTable().getCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		}
	}
}
