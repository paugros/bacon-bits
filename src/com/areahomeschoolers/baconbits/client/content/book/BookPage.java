package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule.AdDirection;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BookPage implements Page {
	private VerticalPanel page;
	private BookPageData pageData;
	private int bookId = Url.getIntegerParameter("bookId");
	private Book book;
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(final FormField formField) {
			save(formField);
		}
	});
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private BookFieldTable fieldTable;

	public BookPage(final VerticalPanel page) {
		this.page = page;

		bookService.getPageData(bookId, new Callback<BookPageData>() {
			@Override
			protected void doOnSuccess(BookPageData result) {
				if (result == null) {
					new ErrorPage(PageError.PAGE_NOT_FOUND);
					return;
				}
				pageData = result;

				book = result.getBook();
				if (Url.getBooleanParameter("details") && !Application.administratorOf(book)) {
					new ErrorPage(PageError.NOT_AUTHORIZED);
					return;
				}

				final String title = book.isSaved() ? book.getTitle() : "New Book";

				CookieCrumb cc = new CookieCrumb();
				cc.add(new DefaultHyperlink("Books By Type", PageUrl.tagGroup("BOOK")));
				cc.add(new DefaultHyperlink("Books", PageUrl.bookList()));
				if (Url.getBooleanParameter("details")) {
					cc.add(new DefaultHyperlink(title, PageUrl.book(book.getId())));
					cc.add("Edit details");
				} else {
					cc.add(title);
				}
				page.add(cc);

				if (book.isSaved() && !Url.getBooleanParameter("details")) {
					createViewPage();
				} else {
					createDetailsPage();
				}

				Application.getLayout().setPage(title, page);
			}
		});
	}

	private void createDetailsPage() {
		fieldTable = new BookFieldTable(form, book, pageData);
		form.initialize();

		HorizontalPanel pp = new HorizontalPanel();
		pp.setWidth("100%");
		if (!book.isSaved()) {
			form.configureForAdd(fieldTable);
		} else {
			form.emancipate();
		}

		boolean editable = Application.administratorOf(book);
		final EditableImage image = new EditableImage(DocumentLinkType.BOOK, book.getId());
		if (book.getImageId() != null) {
			image.setImage(new Image(ClientUtils.createDocumentUrl(book.getImageId(), book.getImageExtension())));
		} else {
			image.setImage(new Image(MainImageBundle.INSTANCE.defaultLarge()));
		}
		image.setEnabled(editable);
		image.populate();
		fieldTable.removeStyleName("sectionContent");
		pp.addStyleName("sectionContent");
		image.getElement().getStyle().setMargin(10, Unit.PX);
		pp.add(image);
		pp.setCellWidth(image, "1%");

		pp.add(fieldTable);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("1000px");
		vp.add(pp);

		page.add(vp);

		form.setEnabled(Application.administratorOf(book));
	}

	private void createViewPage() {
		HorizontalPanel pp = new HorizontalPanel();

		BookDetailsPanel bp = new BookDetailsPanel(book);
		pp.add(bp);

		pp.addStyleName("sectionContent");

		VerticalPanel outerPanel = new VerticalPanel();
		outerPanel.add(pp);
		outerPanel.add(new AdsMiniModule(AdDirection.HORIZONTAL));

		page.add(outerPanel);

	}

	private void save(final FormField field) {
		bookService.save(book, new Callback<Book>() {
			@Override
			protected void doOnSuccess(final Book b) {
				if (!Url.isParamValidId("bookId")) {
					fieldTable.getTagSection().saveAll(b.getId(), new Callback<Void>() {
						@Override
						protected void doOnSuccess(Void result) {
							HistoryToken.set(PageUrl.book(b.getId()));
						}
					});
				} else {
					book = b;
					form.setDto(b);
					fieldTable.setBook(b);
					field.setInputVisibility(false);
				}
			}
		});
	}

}
