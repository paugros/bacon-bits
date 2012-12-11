package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BookDialog extends EntityEditDialog<Book> {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private BookPageData pageData;
	private boolean closeAfterSubmit = true;

	public BookDialog(final BookCellTable cellTable) {
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
		FieldTable ft = new FieldTable();
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);
		ft.setWidth("600px");

		final Label titleDisplay = new Label();
		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.setVisibleLength(50);
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
		for (Data d : pageData.getGradeLevels()) {
			ageInput.addItem(d.get("gradeLevel"), d.getId());
		}
		FormField ageField = form.createFormField("Grade level:", ageInput, ageDisplay);
		ageField.getFieldLabel().setWordWrap(false);
		ageField.setInitializer(new Command() {
			@Override
			public void execute() {
				ageDisplay.setText(entity.getGradeLevel());
				ageInput.setValue(entity.getGradeLevelId());
			}
		});
		ageField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setGradeLevelId(ageInput.getIntValue());
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

		// optional fields
		final Label isbnDisplay = new Label();
		final NumericTextBox isbnInput = new NumericTextBox();
		isbnInput.setMaxLength(13);
		isbnInput.setMinumumLength(13);
		Anchor isbnLink = new Anchor("ISBN:", "http://en.wikipedia.org/wiki/International_Standard_Book_Number");
		isbnLink.setTarget("_blank");
		FormField isbnField = form.createFormField(isbnLink, isbnInput, isbnDisplay);
		isbnField.setInitializer(new Command() {
			@Override
			public void execute() {
				isbnDisplay.setText(entity.getIsbn());
				isbnInput.setText(entity.getIsbn());
			}
		});
		isbnField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setIsbn(isbnInput.getText());
			}
		});
		ft.addField(isbnField);

		final Label statusDisplay = new Label();
		final DefaultListBox statusInput = new DefaultListBox();
		for (Data item : pageData.getStatuses()) {
			statusInput.addItem(item.get("status"), item.getId());
		}
		FormField statusField = form.createFormField("Status:", statusInput, statusDisplay);
		statusField.setInitializer(new Command() {
			@Override
			public void execute() {
				statusDisplay.setText(entity.getStatus());
				statusInput.setValue(entity.getStatusId());
			}
		});
		statusField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setStatusId(statusInput.getIntValue());
			}
		});
		ft.addField(statusField);

		final Label conditionDisplay = new Label();
		final DefaultListBox conditionInput = new DefaultListBox();
		conditionInput.addItem("", 0);
		for (Data item : pageData.getConditions()) {
			conditionInput.addItem(item.get("bookCondition"), item.getId());
		}
		FormField conditionField = form.createFormField("Condition:", conditionInput, conditionDisplay);
		conditionField.setInitializer(new Command() {
			@Override
			public void execute() {
				conditionDisplay.setText(entity.getCondition());
				conditionInput.setValue(entity.getConditionId());
			}
		});
		conditionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setConditionId(conditionInput.getIntValue());
			}
		});
		ft.addField(conditionField);

		final Label notesDisplay = new Label();
		final TextBox notesInput = new TextBox();
		notesInput.setVisibleLength(50);
		notesInput.setMaxLength(1000);
		FormField notesField = form.createFormField("Notes:", notesInput, notesDisplay);
		notesField.setInitializer(new Command() {
			@Override
			public void execute() {
				notesDisplay.setText(entity.getNotes());
				notesInput.setText(entity.getNotes());
			}
		});
		notesField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setNotes(notesInput.getText());
			}
		});
		ft.addField(notesField);

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
