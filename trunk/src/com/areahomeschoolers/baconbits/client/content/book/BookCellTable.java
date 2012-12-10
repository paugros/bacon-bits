package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable.BookColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class BookCellTable extends EntityCellTable<Book, BookArg, BookColumn> {
	public enum BookColumn implements EntityCellTableColumn<BookColumn> {
		USER("Seller"), TITLE("Title"), CATEGORY("Category"), GRADE_LEVEL("Grade level"), STATUS("Status"), PRICE("Price");

		private String title;

		BookColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private BookDialog dialog;
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
		filterBox.addItem("Unsold");
		filterBox.addItem("Sold");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					for (Book item : getFullList()) {
						setItemVisible(item, item.getStatusId() == 1, false, false, false);
					}
					break;
				case 1:
					for (Book item : getFullList()) {
						setItemVisible(item, item.getStatusId() == 2, false, false, false);
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

	public BookDialog getDialog() {
		return dialog;
	}

	public void setDialog(BookDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	protected void fetchData() {
		bookService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (BookColumn col : getDisplayColumns()) {
			switch (col) {
			case USER:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(Book item) {
						return new Hyperlink(item.getUserFirstName() + " " + item.getUserLastName(), PageUrl.user(item.getUserId()));
					}
				});
				break;
			case STATUS:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getStatus();
					}
				});
				break;
			case PRICE:
				addTotaledCurrencyColumn("Price", new ValueGetter<Number, Book>() {
					@Override
					public Number get(Book item) {
						return item.getPrice();
					}
				});
				break;
			case GRADE_LEVEL:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getGradeLevel();
					}
				});
				break;
			case CATEGORY:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getCategory();
					}
				});
				break;
			case TITLE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(final Book item) {
						if (Application.getCurrentUserId() == item.getUserId() || dialog == null) {
							if (dialog == null) {
								return new Label(item.getTitle());
							}
						}

						return new ClickLabel(item.getTitle(), new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								dialog.center(item);
							}
						});
					}
				});
				break;

			default:
				new AssertionError();
				break;
			}
		}
	}

}
