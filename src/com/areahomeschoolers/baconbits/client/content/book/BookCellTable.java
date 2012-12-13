package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable.BookColumn;
import com.areahomeschoolers.baconbits.client.content.document.FileUploadDialog;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class BookCellTable extends EntityCellTable<Book, BookArg, BookColumn> {
	public enum BookColumn implements EntityCellTableColumn<BookColumn> {
		IMAGE(""), USER("Seller"), TITLE("Title"), CATEGORY("Category"), GRADE_LEVEL("Grade level"), STATUS("Status"), CONDITION("Condition"), TOTALED_PRICE(
				"Price"), PRICE("Price"), CONTACT("Contact seller");

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
					getArgMap().put(BookArg.STATUS_ID, 1);
					break;
				case 1:
					getArgMap().put(BookArg.STATUS_ID, 2);
					break;
				case 2:
					getArgMap().remove(BookArg.STATUS_ID);
					break;
				}

				populate();
			}
		});

		int statusId = getArgMap().getInt(BookArg.STATUS_ID);

		if (statusId == 1) {
			filterBox.setSelectedIndex(0);
		} else if (statusId == 2) {
			filterBox.setSelectedIndex(1);
		} else {
			filterBox.setSelectedIndex(2);
		}
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
			case CONTACT:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Book>() {
					@Override
					protected Widget createWidget(Book item) {
						ClickLabel cl = new ClickLabel("Contact", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {

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
						final Image image = new Image("/baconbits/service/file?id=" + item.getSmallImageId());

						if (Application.getCurrentUserId() != item.getUserId()) {
							return image;
						}

						image.addStyleName("pointer");

						image.addMouseDownHandler(new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								final FileUploadDialog uploadDialog = new FileUploadDialog(DocumentLinkType.BOOK, item.getId(), false,
										new UploadCompleteHandler() {
											@Override
											public void onUploadComplete(int documentId) {
												populate();
												// image.setUrl("/baconbits/service/file?id=" + documentId);
											}
										});

								uploadDialog.getForm().addFormValidatorCommand(new ValidatorCommand() {
									@Override
									public void validate(Validator validator) {
										String fileName = uploadDialog.getFileName();
										if (Common.isNullOrBlank(fileName)) {
											validator.setError(true);
										}

										if (!Document.hasImageExtension(fileName)) {
											validator.setError(true);
											validator.setErrorMessage("Invalid image file.");
										}
									}
								});

								uploadDialog.center();
							}
						});

						return image;
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
							return new Label(item.getTitle());
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

		addCompositeWidgetColumn("", new WidgetCellCreator<Book>() {
			@Override
			protected Widget createWidget(final Book item) {
				if (Application.getCurrentUserId() != item.getUserId()) {
					return new Label("");
				}

				return new ClickLabel("X", new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						ConfirmDialog.confirm("Confirm deletion of: " + item.getTitle(), new ConfirmHandler() {
							@Override
							public void onConfirm() {
								bookService.delete(item.getId(), new Callback<Void>() {
									@Override
									protected void doOnSuccess(Void result) {
										populate();
									}
								});
							}
						});
					}
				});
			}
		});
	}

}
