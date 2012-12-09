package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public final class BookManagementPage implements Page {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);

	public BookManagementPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Books";
		GenericCellTable table = new GenericCellTable() {
			@Override
			protected void fetchData() {
				ArgMap<BookArg> args = new ArgMap<BookArg>();
				args.put(BookArg.STATUS_ID, 1);
				bookService.getSummaryData(args, getCallback());
			}

			@Override
			protected void setColumns() {
				addCompositeWidgetColumn("Seller", new WidgetCellCreator<Data>() {
					@Override
					protected Widget createWidget(Data item) {
						return new Hyperlink(item.get("firstName") + " " + item.get("lastName"), PageUrl.user(item.getInt("userId")) + "&tab=6");
					}
				});

				addTotaledNumberColumn("Items", new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getInt("total");
					}
				});

				addTotaledCurrencyColumn("Total price", new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getDouble("totalPrice");
					}
				});
			}
		};

		table.setTitle(title);
		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH750PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
