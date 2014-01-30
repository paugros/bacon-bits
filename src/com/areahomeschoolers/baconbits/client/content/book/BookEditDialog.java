package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class BookEditDialog extends EntityEditDialog<Book> {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private BookPageData pageData;
	private boolean closeAfterSubmit = true;

	public BookEditDialog(final BookTable cellTable) {
		setAutoHide(false);
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
						if (closeAfterSubmit) {
							hide();
						} else {
							closeAfterSubmit = true;
							setEntity(new Book());
							form.initialize();
						}
						getButtonPanel().setEnabled(true);
						cellTable.populate();
					}
				});
			}
		});

		getButtonPanel().getCloseButton().setText("Cancel");
		bookService.getPageData(0, new Callback<BookPageData>() {
			@Override
			protected void doOnSuccess(BookPageData result) {
				pageData = result;
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

		super.setEntity(b);
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new BookFieldTable(form, entity, pageData);
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);
		ft.setWidth("600px");

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

		return ft;
	}

}
