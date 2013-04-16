package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable.BookColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class BookSearchPage implements Page {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
	private BookCellTable table = new BookCellTable(args);
	private PaddedPanel optionsPanel = new PaddedPanel(15);

	public BookSearchPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			args.put(BookArg.ONLINE_ONLY);
		}

		optionsPanel.getElement().getStyle().setBackgroundColor("#c5eabf");
		Label label = new Label("Filter for:");
		label.addStyleName("bold");
		optionsPanel.add(label);
		optionsPanel.addStyleName("heavyPadding");
		page.add(optionsPanel);

		String warning = "This is a sample of our online book sale. The online sale will open on 4/13 and additional inventory will be available for purchase. ";
		warning += "If you're interested in selling online with us, please register ";
		Hyperlink registerLink = new Hyperlink("here", PageUrl.event(471));
		warning += registerLink.toString() + ".";
		page.add(new HTML(warning));

		Hyperlink conditionLink = new Hyperlink("Click here", PageUrl.article(64));
		String text = conditionLink + " for a description of book conditions.";
		page.add(new HTML(text));

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
				category.addItem("All categories", 0);
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
				grade.addItem("All grades", 0);
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
				price.addItem("All prices");
				price.addItem("< $1", "0-0.99");
				price.addItem("$1 - $5", "1-5");
				price.addItem("$5 - $10", "5-10");
				price.addItem("$10 - $20", "10-20");
				price.addItem("> $20", "20.01-1000");

				optionsPanel.add(category);
				optionsPanel.add(grade);
				optionsPanel.add(price);
			}
		});

		final String title = "Books";

		table.setDisplayColumns(BookColumn.IMAGE, BookColumn.USER, BookColumn.TITLE, BookColumn.CATEGORY, BookColumn.GRADE_LEVEL, BookColumn.CONDITION,
				BookColumn.PRICE, BookColumn.CONTACT);
		table.setTitle(title);
		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1300PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}