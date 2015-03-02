package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.book.BookTable.BookColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.VerticalPanel;

public final class BookManagementPage implements Page {
	private ArgMap<BookArg> args = new ArgMap<BookArg>();

	public BookManagementPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Books";

		args.put(BookArg.STATUS_ID, 1);
		final BookTable table = new BookTable(args);
		table.setDisplayColumns(BookColumn.TITLE, BookColumn.USER, BookColumn.GRADE_LEVEL, BookColumn.STATUS, BookColumn.PRICE, BookColumn.VIEWS);
		table.setTitle(title);
		table.getTitleBar().addLink(new DefaultHyperlink("Add", PageUrl.book(0)));
		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		table.addStatusFilterBox();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
