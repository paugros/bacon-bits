package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.book.BookTable.BookColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GeocoderTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class BookSearchPage implements Page {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
	private BookTable table = new BookTable(args);
	private VerticalPanel optionsPanel = new VerticalPanel();
	private PaddedPanel top = new PaddedPanel(15);
	private PaddedPanel bottom = new PaddedPanel(15);
	private int sellerId = Url.getIntegerParameter("sellerId");

	public BookSearchPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			args.put(BookArg.ONLINE_ONLY);
		}

		if (sellerId > 0) {
			args.put(BookArg.USER_ID, sellerId);
		}

		optionsPanel.addStyleName("boxedBlurb");
		optionsPanel.setSpacing(8);
		Label label = new Label("Show");
		top.add(label);

		Hyperlink conditionLink = new Hyperlink("Click here", PageUrl.article(64));
		String text = conditionLink + " for a description of book conditions.";
		page.add(new HTML(text));

		if (sellerId > 0) {
			Hyperlink all = new Hyperlink(">>> Books below are for a single seller. Click here to see all books.", PageUrl.bookSearch());
			all.addStyleName("largeText bold mediumPadding");
			page.add(all);
		}

		bookService.getPageData(0, new Callback<BookPageData>() {
			@Override
			protected void doOnSuccess(BookPageData result) {
				final DefaultListBox category = new DefaultListBox();
				category.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						args.put(BookArg.CATEGORY_ID, category.getIntValue());
						table.populate();
					}
				});
				category.addItem("all categories", 0);
				for (Data item : result.getCategories()) {
					category.addItem(item.get("category"), item.getId());
				}

				final DefaultListBox grade = new DefaultListBox();
				grade.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						args.put(BookArg.GRADE_LEVEL_ID, grade.getIntValue());
						table.populate();
					}
				});
				grade.addItem("all grades", 0);
				for (Data item : result.getGradeLevels()) {
					grade.addItem(item.get("gradeLevel"), item.getId());
				}

				final DefaultListBox price = new DefaultListBox();
				price.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						if (price.getSelectedIndex() == 0) {
							args.remove(BookArg.PRICE_BETWEEN);
						} else {
							args.put(BookArg.PRICE_BETWEEN, price.getValue());
						}
						table.populate();
					}
				});
				price.addItem("all prices");
				price.addItem("< $1", "0-0.99");
				price.addItem("$1 - $5", "1-5");
				price.addItem("$5 - $10", "5-10");
				price.addItem("$10 - $20", "10-20");
				price.addItem("> $20", "20.01-1000");

				top.add(category);
				top.add(grade);
				top.add(price);

				for (int i = 0; i < top.getWidgetCount(); i++) {
					top.setCellVerticalAlignment(top.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
				}

				// within miles
				final DefaultListBox milesInput = new DefaultListBox();
				final GeocoderTextBox locationInput = new GeocoderTextBox();
				milesInput.addItem("5", 5);
				milesInput.addItem("10", 10);
				milesInput.addItem("25", 25);
				milesInput.addItem("50", 50);
				milesInput.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						args.put(BookArg.WITHIN_MILES, milesInput.getIntValue());
						if (!locationInput.getText().isEmpty()) {
							table.populate();
						}
					}
				});

				locationInput.setClearCommand(new Command() {
					@Override
					public void execute() {
						args.remove(BookArg.WITHIN_LAT);
						args.remove(BookArg.WITHIN_LNG);
						table.populate();
					}
				});

				locationInput.setChangeCommand(new Command() {
					@Override
					public void execute() {
						args.put(BookArg.WITHIN_LAT, Double.toString(locationInput.getLat()));
						args.put(BookArg.WITHIN_LNG, Double.toString(locationInput.getLng()));
						args.put(BookArg.WITHIN_MILES, milesInput.getIntValue());
						table.populate();
					}
				});

				bottom.add(new Label("within"));
				bottom.add(milesInput);
				bottom.add(new Label("miles of"));
				bottom.add(locationInput);
				bottom.add(new Label("with text"));
				bottom.add(table.getTitleBar().extractSearchControl());

				for (int i = 0; i < bottom.getWidgetCount(); i++) {
					bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
				}

				optionsPanel.add(top);
				optionsPanel.add(bottom);
				page.insert(optionsPanel, 0);
			}
		});

		final String title = "Books";

		table.setDisplayColumns(BookColumn.IMAGE, BookColumn.USER, BookColumn.TITLE, BookColumn.CATEGORY, BookColumn.GRADE_LEVEL, BookColumn.PRICE,
				BookColumn.CONTACT);
		table.setTitle(title);
		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		ContentWidth width = Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS) ? ContentWidth.MAXWIDTH1200PX : ContentWidth.MAXWIDTH1000PX;
		page.add(WidgetFactory.newSection(table, width));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Sidebar sb = Sidebar.create(MiniModule.CITRUS, MiniModule.NEW_BOOKS, MiniModule.SELL_BOOKS);
				if (Application.isAuthenticated()) {
					sb.add(MiniModule.ACTIVE_USERS);
				}
				Application.getLayout().setPage(title, sb, page);
			}
		});

		table.populate();
	}
}
