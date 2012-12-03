package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BookDialog extends EntityEditDialog<Book> {

	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private BookPageData pageData;

	public BookDialog(final BookCellTable cellTable) {
		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				entity.setStatusId(1);
				bookService.save(entity, new Callback<Book>() {
					@Override
					protected void doOnSuccess(Book result) {
						hide();
						cellTable.addItem(result);
					}
				});
			}
		});

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
		FieldTable ft = new FieldTable();

		final Label titleDisplay = new Label();
		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.setMaxLength(200);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleDisplay.setText(entity.getTitle());
				titleInput.setText(entity.getTitle());
			}
		});
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setTitle(titleInput.getText());
			}
		});
		ft.addField(titleField);

		final Label categoryDisplay = new Label();
		final RequiredListBox categoryInput = new RequiredListBox();
		for (Data d : pageData.getCategories()) {
			categoryInput.addItem(d.get("category"), d.getId());
		}
		FormField categoryField = form.createFormField("Category:", categoryInput, categoryDisplay);
		categoryField.setInitializer(new Command() {
			@Override
			public void execute() {
				categoryDisplay.setText(entity.getCategory());
				categoryInput.setValue(entity.getCategoryId());
			}
		});
		categoryField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setCategoryId(categoryInput.getIntValue());
			}
		});
		ft.addField(categoryField);

		final Label ageDisplay = new Label();
		final RequiredListBox ageInput = new RequiredListBox();
		for (Data d : pageData.getAgeLevels()) {
			ageInput.addItem(d.get("ageLevel"), d.getId());
		}
		FormField ageField = form.createFormField("Age level:", ageInput, ageDisplay);
		ageField.setInitializer(new Command() {
			@Override
			public void execute() {
				ageDisplay.setText(entity.getAgeLevel());
				ageInput.setValue(entity.getAgeLevelId());
			}
		});
		ageField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setAgeLevelId(ageInput.getIntValue());
			}
		});
		ft.addField(ageField);

		final Label priceDisplay = new Label();
		final NumericTextBox priceInput = new NumericTextBox(2);
		priceInput.setRequired(true);
		FormField priceField = form.createFormField("Price:", priceInput, priceDisplay);
		priceField.setInitializer(new Command() {
			@Override
			public void execute() {
				priceDisplay.setText(Formatter.formatCurrency(entity.getPrice()));
				priceInput.setValue(entity.getPrice());
			}
		});
		priceField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setPrice(priceInput.getDouble());
			}
		});
		ft.addField(priceField);

		return ft;
	}

}
