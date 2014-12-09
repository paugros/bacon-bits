package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.LazyLoadDialog;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class BookEditDialog extends EntityEditDialog<Book> implements LazyLoadDialog {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private static BookPageData pageData;
	private boolean closeAfterSubmit = true;
	private BookFieldTable fieldTable;

	public BookEditDialog(final BookTable cellTable) {
		setAutoHide(false);
		setModal(false);

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				getButtonPanel().setEnabled(false);
				if (!entity.isSaved()) {
					entity.setStatusId(1);
				}
				bookService.save(entity, new Callback<Book>() {
					@Override
					protected void doOnSuccess(Book result) {
						fieldTable.getTagSection().saveAll(result.getId(), new Callback<Void>() {
							@Override
							protected void doOnSuccess(Void result) {
							}
						});
						setEntity(new Book());
						form.initialize();

						if (closeAfterSubmit) {
							hide();
						} else {
							closeAfterSubmit = true;
						}
						getButtonPanel().setEnabled(true);
						cellTable.populate();
					}
				});
			}
		});

		getButtonPanel().getCloseButton().setText("Cancel");
		getButtonPanel().addLeftButton(new Button("Help?", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mailto(Constants.SUPPORT_EMAIL, "Adding books");
			}
		}));
	}

	@Override
	public void loadData() {
		if (pageData != null) {
			showContent();
			return;
		}

		bookService.getPageData(0, new Callback<BookPageData>() {
			@Override
			protected void doOnSuccess(BookPageData result) {
				pageData = result;
				showContent();
			}
		});
	}

	@Override
	public void setEntity(Book b) {
		if (b.isSaved()) {
			setText("Edit Book");
		} else {
			setText("Add Book");
		}

		if (fieldTable != null) {
			fieldTable.setBook(b);
		}
		super.setEntity(b);
	}

	private native void mailto(String address, String subject) /*-{
		$wnd.location = "mailto:" + address + "?subject=" + subject;
	}-*/;

	@Override
	protected Widget createContent() {
		fieldTable = new BookFieldTable(form, entity, pageData);
		fieldTable.setDialog(this);
		fieldTable.setLabelColumnWidth(LabelColumnWidth.NARROW);
		fieldTable.setWidth("600px");

		form.getSubmitButton().setText("Save and Close");
		final Button save = new Button("Save and Add Another");
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeAfterSubmit = false;
				form.getSubmitButton().click();
			}
		});
		getButtonPanel().addRightButton(save);

		return fieldTable;
	}

}
