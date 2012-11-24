package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable.BookColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public final class BookCellTable extends EntityCellTable<Book, BookArg, BookColumn> {
	public enum BookColumn implements EntityCellTableColumn<BookColumn> {
		TITLE("Title"), CATEGORY("Category"), AGE_LEVEL("Age level"), PRICE("Price");

		private String title;

		BookColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);

	public BookCellTable(ArgMap<BookArg> args) {
		this();
		setArgMap(args);
	}

	private BookCellTable() {
		setDefaultSortColumn(BookColumn.TITLE, SortDirection.SORT_ASC);
		setDisplayColumns(BookColumn.values());
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl();
		filterBox.addItem("Active");
		filterBox.addItem("Inactive");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					for (Book item : getFullList()) {
						setItemVisible(item, item.isActive(), false, false, false);
					}
					break;
				case 1:
					for (Book item : getFullList()) {
						setItemVisible(item, !item.isActive(), false, false, false);
					}
					break;
				case 2:
					showAllItems();
					break;
				}

				refreshForCurrentState();
			}
		});

		filterBox.setSelectedIndex(0);

		addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				filterBox.fireEvent(new ChangeEvent() {
				});
			}
		});
	}

	@Override
	protected void fetchData() {
		bookService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (BookColumn col : getDisplayColumns()) {
			switch (col) {
			case PRICE:
				addCurrencyColumn(col, new ValueGetter<Double, Book>() {
					@Override
					public Double get(Book item) {
						return item.getPrice();
					}
				});
			case AGE_LEVEL:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getAgeLevel();
					}
				});
			case CATEGORY:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getCategory();
					}
				});
			case TITLE:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getTitle();
					}
				});

			default:
				new AssertionError();
				break;
			}
		}
	}

}
