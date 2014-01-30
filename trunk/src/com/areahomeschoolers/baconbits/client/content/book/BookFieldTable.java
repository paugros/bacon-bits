package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class BookFieldTable extends FieldTable {
	private Book book;
	private Form form;
	private BookPageData pageData;

	public BookFieldTable(Form f, Book b, BookPageData pd) {
		this.book = b;
		this.form = f;
		this.pageData = pd;

		setWidth("100%");

		final Label titleDisplay = new Label();
		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.setVisibleLength(50);
		titleInput.setMaxLength(200);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleDisplay.setText(book.getTitle());
				titleInput.setText(book.getTitle());
			}
		});
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setTitle(titleInput.getText());
			}
		});
		addField(titleField);

		final Label authorDisplay = new Label();
		final TextBox authorInput = new TextBox();
		authorInput.setMaxLength(100);
		FormField authorField = form.createFormField("Author:", authorInput, authorDisplay);
		authorField.setInitializer(new Command() {
			@Override
			public void execute() {
				authorDisplay.setText(Common.getDefaultIfNull(book.getAuthor()));
				authorInput.setText(book.getAuthor());
			}
		});
		authorField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setAuthor(authorInput.getText());
			}
		});
		addField(authorField);

		final Label categoryDisplay = new Label();
		final RequiredListBox categoryInput = new RequiredListBox();
		for (Data d : pageData.getCategories()) {
			categoryInput.addItem(d.get("category"), d.getId());
		}
		FormField categoryField = form.createFormField("Category:", categoryInput, categoryDisplay);
		categoryField.setInitializer(new Command() {
			@Override
			public void execute() {
				categoryDisplay.setText(book.getCategory());
				categoryInput.setValue(book.getCategoryId());
			}
		});
		categoryField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setCategoryId(categoryInput.getIntValue());
			}
		});
		addField(categoryField);

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
				ageDisplay.setText(book.getGradeLevel());
				ageInput.setValue(book.getGradeLevelId());
			}
		});
		ageField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setGradeLevelId(ageInput.getIntValue());
			}
		});
		addField(ageField);

		final Label priceDisplay = new Label();
		final NumericTextBox priceInput = new NumericTextBox(2);
		priceInput.setRequired(true);
		FormField priceField = form.createFormField("Price:", priceInput, priceDisplay);
		priceField.setInitializer(new Command() {
			@Override
			public void execute() {
				priceDisplay.setText(Formatter.formatCurrency(book.getPrice()));
				priceInput.setValue(book.getPrice());
			}
		});
		priceField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setPrice(priceInput.getDouble());
			}
		});
		addField(priceField);

		// optional fields
		final Label isbnDisplay = new Label();
		final NumericTextBox isbnInput = new NumericTextBox();
		isbnInput.setMaxLength(13);
		isbnInput.setMinumumLength(9);
		Anchor isbnLink = new Anchor("ISBN:", "http://en.wikipedia.org/wiki/International_Standard_Book_Number");
		isbnLink.setTarget("_blank");
		FormField isbnField = form.createFormField(isbnLink, isbnInput, isbnDisplay);
		isbnField.setInitializer(new Command() {
			@Override
			public void execute() {
				isbnDisplay.setText(Common.getDefaultIfNull(book.getIsbn()));
				isbnInput.setText(book.getIsbn());
			}
		});
		isbnField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setIsbn(isbnInput.getText());
			}
		});
		addField(isbnField);

		final Label statusDisplay = new Label();
		final DefaultListBox statusInput = new DefaultListBox();
		for (Data item : pageData.getStatuses()) {
			statusInput.addItem(item.get("status"), item.getId());
		}
		FormField statusField = form.createFormField("Status:", statusInput, statusDisplay);
		statusField.setInitializer(new Command() {
			@Override
			public void execute() {
				statusDisplay.setText(book.getStatus());
				statusInput.setValue(book.getStatusId());
			}
		});
		statusField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setStatusId(statusInput.getIntValue());
			}
		});
		addField(statusField);

		final Label conditionDisplay = new Label();
		final DefaultListBox conditionInput = new DefaultListBox();
		Anchor conditionLink = new Anchor("Condition:", Url.getBaseUrl() + "#" + PageUrl.article(64));
		conditionLink.setTarget("_blank");
		conditionInput.addItem("", 0);
		for (Data item : pageData.getConditions()) {
			conditionInput.addItem(item.get("bookCondition"), item.getId());
		}
		FormField conditionField = form.createFormField(conditionLink, conditionInput, conditionDisplay);
		conditionField.setInitializer(new Command() {
			@Override
			public void execute() {
				conditionDisplay.setText(Common.getDefaultIfNull(book.getCondition()));
				conditionInput.setValue(book.getConditionId());
			}
		});
		conditionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setConditionId(conditionInput.getIntValue());
			}
		});
		addField(conditionField);

		final Label imageDisplay = new Label();
		final TextBox imageInput = new TextBox();
		imageInput.setMaxLength(256);
		imageInput.setVisibleLength(20);
		FormField imageField = form.createFormField("Image URL:", imageInput, imageDisplay);
		imageField.setInitializer(new Command() {
			@Override
			public void execute() {
				imageDisplay.setText(Common.getDefaultIfNull(book.getImageUrl()));
				imageInput.setText(book.getImageUrl());
			}
		});
		imageField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setImageUrl(imageInput.getText());
			}
		});
		addField(imageField);

		final Label notesDisplay = new Label();
		final TextBox notesInput = new TextBox();
		notesInput.setVisibleLength(50);
		notesInput.setMaxLength(1000);
		FormField notesField = form.createFormField("Notes:", notesInput, notesDisplay);
		notesField.setInitializer(new Command() {
			@Override
			public void execute() {
				notesDisplay.setText(Common.getDefaultIfNull(book.getNotes()));
				notesInput.setText(book.getNotes());
			}
		});
		notesField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setNotes(notesInput.getText());
			}
		});
		addField(notesField);

	}

}
