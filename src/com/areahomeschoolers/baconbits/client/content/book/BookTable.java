package com.areahomeschoolers.baconbits.client.content.book;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookTable.BookColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EmailDialog;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public final class BookTable extends EntityCellTable<Book, BookArg, BookColumn> {
	public enum BookColumn implements EntityCellTableColumn<BookColumn> {
		IMAGE(""), USER("Seller"), TITLE("Title"), TAGS("Tags"), GRADE_LEVEL("Grade Level"), STATUS("Status"), CONDITION("Condition"), TOTALED_PRICE("Price"), PRICE(
				"Price"), VIEWS("Views"), ADDED_DATE("Added"), DELETE(""), DELETE_PURCHASE("");

		private String title;

		BookColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	public static void showEmailDialog(Book item) {
		EmailDialog e = new EmailDialog();
		e.addTo(item.getUserEmail());
		e.addBcc("admin@wearehomeeducators.com");
		e.setSubject("Buyer for book: " + item.getTitle());
		String m = "Hello, someone is interested in buying a book that you have listed for sale. Details appear below.<br><br>";
		if (Application.isAuthenticated()) {
			m += "Buyer: " + Application.getCurrentUser().getFullName() + "<br>";
		}
		m += "Title: " + item.getTitle() + "<br>";
		m += "Price: " + Formatter.formatCurrency(item.getPrice()) + "<br><br>";
		m += "Message from buyer: ";
		e.setHiddenAboveText(m);
		e.setText("Email Seller");
		e.center();
	}

	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private double totalPrice;

	private Command onDelete;

	public BookTable(ArgMap<BookArg> args) {
		this();
		setArgMap(args);
	}

	private BookTable() {
		addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				totalPrice = 0;
				for (Book item : getFullList()) {
					totalPrice += item.getPrice();
				}
			}
		});
		setDefaultSortColumn(BookColumn.TITLE, SortDirection.SORT_ASC);
		setDisplayColumns(BookColumn.TITLE, BookColumn.PRICE, BookColumn.GRADE_LEVEL, BookColumn.STATUS, BookColumn.CONDITION, BookColumn.VIEWS);
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl(false);
		filterBox.addItem("Listed - available");
		filterBox.addItem("Unlisted - sold");
		filterBox.addItem("Unlisted - deleted");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					getArgMap().put(BookArg.STATUS_ID, 1);
					break;
				case 1:
					getArgMap().put(BookArg.STATUS_ID, 2);
					break;
				case 2:
					getArgMap().put(BookArg.STATUS_ID, 3);
					break;
				case 3:
					getArgMap().remove(BookArg.STATUS_ID);
					break;
				}

				populate();
			}
		});

		int statusId = getArgMap().getInt(BookArg.STATUS_ID);
		if (statusId > 0) {
			filterBox.setSelectedIndex(statusId - 1);
		} else {
			filterBox.setSelectedIndex(3);
		}
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setOnDelete(Command onDelete) {
		this.onDelete = onDelete;
	}

	@Override
	protected void fetchData() {
		bookService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (BookColumn col : getDisplayColumns()) {
			switch (col) {
			case TAGS:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getTags();
					}
				});
				break;
			case IMAGE:
				addWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(Book item) {
						Image i = new Image(MainImageBundle.INSTANCE.defaultSmall());
						if (item.getSmallImageId() != null) {
							i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
						}
						return i;
					}
				});
				break;
			case VIEWS:
				addNumberColumn(col, new ValueGetter<Number, Book>() {
					@Override
					public Number get(Book item) {
						return item.getViewCount();
					}
				});
				break;
			case ADDED_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, Book>() {
					@Override
					public Date get(Book item) {
						return item.getAddedDate();
					}
				});

				break;
			case CONDITION:
				addTextColumn(col, new ValueGetter<String, Book>() {
					@Override
					public String get(Book item) {
						return item.getCondition();
					}
				});
				break;
			case USER:
				if (Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS)) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
						@Override
						protected Widget createWidget(Book item) {
							return new DefaultHyperlink(item.getUserFirstName() + " " + item.getUserLastName(), PageUrl.user(item.getUserId()));
						}
					});
				}
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
				addCurrencyColumn(col, new ValueGetter<Double, Book>() {
					@Override
					public Double get(Book item) {
						return item.getPrice();
					}
				});
				break;
			case TOTALED_PRICE:
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
			case TITLE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(final Book item) {
						if (Application.getCurrentUserId() != item.getUserId()) {
							return new ClickLabel(item.getTitle(), new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									new BookDetailsDialog(item).center();
								}
							});
						}

						return new DefaultHyperlink(item.getTitle(), PageUrl.book(item.getId()));
					}
				});
				break;

			case DELETE:
				if (Application.isSystemAdministrator()) {
					addCompositeWidgetColumn("", new WidgetCellCreator<Book>() {
						@Override
						protected Widget createWidget(final Book item) {
							return new ClickLabel("X", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									ConfirmDialog.confirm("Confirm deletion of: " + item.getTitle(), new ConfirmHandler() {
										@Override
										public void onConfirm() {
											bookService.delete(item, new Callback<Void>() {
												@Override
												protected void doOnSuccess(Void result) {
													populate();
													if (onDelete != null) {
														onDelete.execute();
													}
												}
											});
										}
									});
								}
							});
						}
					});
				}
				break;

			case DELETE_PURCHASE:
				addCompositeWidgetColumn("", new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(final Book item) {
						return new ClickLabel("X", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								removeItem(item);
								totalPrice -= item.getPrice();
								if (onDelete != null) {
									onDelete.execute();
								}
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
