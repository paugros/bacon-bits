package com.areahomeschoolers.baconbits.client.content.book;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookTable.BookColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class BookReceiptPage implements Page {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private VerticalPanel page;
	private BookTable table;
	private PaddedPanel totalPanel = new PaddedPanel();
	private static List<String> transactions = new ArrayList<>();
	private static int showTransaction = 0;

	public BookReceiptPage(final VerticalPanel p) {
		if (!Application.administratorOf(17)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}
		totalPanel.addStyleName("bold largeText");

		this.page = p;

		initialize();

		final String title = "Book Receipt";

		Application.getLayout().setPage(title, page);
	}

	private void initialize() {
		final TextBox tb = new TextBox();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tb.setFocus(true);
			}
		});

		Label l = new Label("Enter book IDs, separated by plus signs:");
		HorizontalPanel hp = new PaddedPanel();
		final Button submit = new Button("Submit");
		hp.add(l);
		hp.add(tb);
		hp.add(submit);

		if (!transactions.isEmpty()) {
			PaddedPanel pp = new PaddedPanel(20);
			pp.getElement().getStyle().setMarginLeft(20, Unit.PX);
			if (showTransaction != 0) {
				ClickLabel prev = new ClickLabel("<< Previous sale", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (showTransaction == -1) {
							showTransaction = transactions.size() - 1;
						} else {
							showTransaction -= 1;
						}
						Application.reloadPage();
					}
				});

				pp.add(prev);
			}

			if (showTransaction > -1 && showTransaction < transactions.size()) {
				ClickLabel next = new ClickLabel("Next sale >>", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (showTransaction == transactions.size() - 1) {
							showTransaction = -1;
						} else {
							showTransaction += 1;
						}
						Application.reloadPage();
					}
				});

				pp.add(next);
			}

			if (pp.getWidgetCount() > 0) {
				hp.add(pp);
			}
		}

		tb.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submit.click();
				}
			}
		});

		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				PaddedPanel pp = new PaddedPanel();
				if (table == null) {
					ArgMap<BookArg> args = new ArgMap<BookArg>();
					table = new BookTable(args);
					table.setOnDelete(new Command() {
						@Override
						public void execute() {
							populateTotalPanel();
						}
					});
					table.setDisplayColumns(BookColumn.TITLE, BookColumn.CONDITION, BookColumn.PRICE, BookColumn.DELETE_PURCHASE);
					table.setTitle("Book Receipt");
					page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));

					table.addDataReturnHandler(new DataReturnHandler() {
						@Override
						public void onDataReturn() {
							populateTotalPanel();
						}
					});
					page.add(totalPanel);
					page.add(pp);
				}

				table.getArgMap().put(BookArg.IDS, tb.getText().replaceAll("\\+", ","));
				table.disablePaging();
				table.setSortingEnabled(false);

				table.populate();

				Label l = new Label("Email receipt to:");
				final EmailTextBox emailBox = new EmailTextBox();
				pp.add(l);
				pp.add(emailBox);

				final Button sell = new Button("Mark Books as Sold");

				emailBox.addKeyDownHandler(new KeyDownHandler() {
					@Override
					public void onKeyDown(KeyDownEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							sell.click();
						}
					}
				});

				sell.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (!Common.isNullOrBlank(emailBox.getValue())) {
							emailBox.getValidator().validate();
							if (emailBox.getValidator().hasError()) {
								return;
							}
						}

						sell.setEnabled(false);

						bookService.sellBooks(Common.asArrayList(table.getFullList()), emailBox.getValue(), new Callback<Void>() {
							@Override
							protected void doOnSuccess(Void result) {
								transactions.add(table.getArgMap().getString(BookArg.IDS));
								showTransaction = -1;

								Application.reloadPage();
							}
						});
					}
				});

				pp.add(sell);
			}
		});

		page.add(hp);

		if (!transactions.isEmpty() && showTransaction > -1) {
			tb.setText(transactions.get(showTransaction));
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					submit.click();
				}
			});
		}

	}

	private void populateTotalPanel() {
		totalPanel.clear();
		String totalText = "Total: " + Formatter.formatCurrency(table.getTotalPrice()) + " cash / ";
		double creditTotal = table.getTotalPrice() + (table.getTotalPrice() * .03) + .3;
		totalText += Formatter.formatCurrency(creditTotal) + " credit";
		totalPanel.add(new Label(totalText));
	}
}
