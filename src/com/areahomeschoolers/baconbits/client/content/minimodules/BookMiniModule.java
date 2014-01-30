package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BookMiniModule extends Composite {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private VerticalPanel vp = new VerticalPanel();

	public BookMiniModule() {
		initWidget(vp);
	}

	public BookMiniModule(String title, ArrayList<Book> books) {
		this();
		populate(title, books);
	}

	protected void populate(final String title, ArgMap<BookArg> args) {
		bookService.list(args, new Callback<ArrayList<Book>>() {
			@Override
			protected void doOnSuccess(ArrayList<Book> result) {
				if (!result.isEmpty()) {
					populate(title, result);
				} else {
					setVisible(false);
					removeFromParent();
				}
			}
		});
	}

	protected void populate(String title, ArrayList<Book> books) {
		if (Common.isNullOrEmpty(books)) {
			removeFromParent();
			return;
		}

		vp.addStyleName("module");
		vp.setSpacing(8);

		Label label = new Label(title);
		label.addStyleName("moduleTitle");
		vp.add(label);

		for (Book b : books) {
			VerticalPanel mhp = new VerticalPanel();

			Hyperlink link = new Hyperlink(b.getTitle() + " - " + Formatter.formatCurrency(b.getPrice()), PageUrl.book(b.getId()));
			link.addStyleName("mediumText");
			mhp.add(link);

			// HTML date = new HTML(Formatter.formatDateTime(e.getStartDate()));
			// date.setWordWrap(false);
			// date.addStyleName("italic");
			// mhp.add(date);

			vp.add(mhp);
		}

	}

}
