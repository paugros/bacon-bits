package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Hyperlink;
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

				initializePage();
			}
		});
	}

	private void initializePage() {
		final String title = book.isSaved() ? book.getTitle() : "New Book";
		fieldTable = new BookFieldTable(form, book, pageData);
		form.initialize();

		PaddedPanel pp = new PaddedPanel();

		if (!book.isSaved()) {
			form.configureForAdd(fieldTable);
		} else {
			form.emancipate();
		}

		TitleBar tb = new TitleBar(title, TitleBarStyle.SECTION);
		tb.addLink(new ClickLabel("Contact seller", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				BookTable.showEmailDialog(book);
			}
		}));
		Hyperlink link = new Hyperlink("More from this seller", PageUrl.bookList() + "&sellerId=" + book.getUserId());
		tb.addLink(link);

		boolean editable = Application.administratorOf(book);
		final EditableImage image = new EditableImage(DocumentLinkType.BOOK, book.getId());
		image.setImage(new Image(ClientUtils.createDocumentUrl(book.getImageId(), book.getImageExtension())));
		image.setEnabled(editable);
		image.populate();
		image.addStyleName("profilePic");
		fieldTable.removeStyleName("sectionContent");
		VerticalPanel ivp = new VerticalPanel();
		pp.add(ivp);
		pp.setCellWidth(ivp, "220px");
		ivp.add(image);
		if (editable) {
			ClickLabel change = new ClickLabel("Change image...", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					image.click();
				}
			});
			change.getElement().getStyle().setMarginLeft(10, Unit.PX);
			change.addStyleName("smallText");
			ivp.add(change);
		}

		TagSection ts = new TagSection(TagMappingType.BOOK, book.getId(), pageData.getTags());
		ts.setEditingEnabled(editable);
		ts.populate();
		fieldTable.addField("Tags:", ts);

		pp.add(fieldTable);

		VerticalPanel vp = new VerticalPanel();
		vp.addStyleName("sectionContent");
		vp.setWidth("100%");
		vp.add(pp);

		page.add(WidgetFactory.newSection(tb, vp, ContentWidth.MAXWIDTH1000PX));

		form.setEnabled(Application.administratorOf(book));

		Sidebar sb = Sidebar.create(MiniModule.CITRUS, MiniModule.NEW_BOOKS, MiniModule.SELL_BOOKS);
		Application.getLayout().setPage(title, sb, page);
	}

	private void save(final FormField field) {
		bookService.save(book, new Callback<Book>() {
			@Override
			protected void doOnSuccess(Book result) {
				if (!Url.isParamValidId("bookId")) {
					HistoryToken.set(PageUrl.book(result.getId()));
				} else {
					book = result;
					form.setDto(result);
					fieldTable.setBook(result);
					field.setInputVisibility(false);
				}
			}
		});
	}

}
