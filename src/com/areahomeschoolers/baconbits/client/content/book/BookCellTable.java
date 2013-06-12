package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable.BookColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.EmailDialog;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class BookCellTable extends EntityCellTable<Book, BookArg, BookColumn> {
	public enum BookColumn implements EntityCellTableColumn<BookColumn> {
		IMAGE("Image"), USER("Seller"), TITLE("Title"), CATEGORY("Category"), GRADE_LEVEL("Grade level"), STATUS("Status"), CONDITION("Condition"), TOTALED_PRICE(
				"Price"), PRICE("Price"), CONTACT("Contact seller"), DELETE(""), DELETE_PURCHASE("");

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
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private Article tc;
	private boolean agreed = false;
	private double totalPrice;
	private Command onDelete;

	public BookCellTable(ArgMap<BookArg> args) {
		this();
		setArgMap(args);
	}

	private BookCellTable() {
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
		setDisplayColumns(BookColumn.values());

		articleService.getById(66, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article result) {
				tc = result;
			}
		});
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

	public BookDialog getDialog() {
		return dialog;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setDialog(BookDialog dialog) {
		this.dialog = dialog;
	}

	public void setOnDelete(Command onDelete) {
		this.onDelete = onDelete;
	}

	private void showEmailDialog(Book item) {
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

	@Override
	protected void fetchData() {
		bookService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (BookColumn col : getDisplayColumns()) {
			switch (col) {
			case CONTACT:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(final Book item) {
						ClickLabel cl = new ClickLabel("Contact", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								if (!agreed && tc != null) {
									HTML h = new HTML(tc.getArticle());
									h.setWidth("400px");
									AlertDialog ad = new AlertDialog(tc.getTitle(), h);
									ad.getButton().addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											showEmailDialog(item);
										}
									});
									ad.center();
									agreed = true;
								} else {
									showEmailDialog(item);
								}
							}
						});

						return cl;
					}
				});
				break;
			case IMAGE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(final Book item) {
						boolean editable = Application.getCurrentUserId() == item.getUserId();
						EditableImage image = new EditableImage(DocumentLinkType.BOOK, item.getId(), item.getSmallImageId(), editable);
						image.setUploadCompleteHandler(new UploadCompleteHandler() {
							@Override
							public void onUploadComplete(int documentId) {
								populate();
							}
						});
						return image.getImage();
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
				if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
						@Override
						protected Widget createWidget(Book item) {
							return new Hyperlink(item.getUserFirstName() + " " + item.getUserLastName(), PageUrl.user(item.getUserId()));
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
						if (Application.getCurrentUserId() != item.getUserId() || dialog == null) {
							return new ClickLabel(item.getTitle(), new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
									new BookDetailsDialog(item).center();
								}
							});
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

			case DELETE:
				if (!(Application.isSystemAdministrator() || Application.getCurrentUser().isSwitched())) {
					addCompositeWidgetColumn("", new WidgetCellCreator<Book>() {
						@Override
						protected Widget createWidget(final Book item) {
							return new ClickLabel("X", new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
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
						return new ClickLabel("X", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
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
