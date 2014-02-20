package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.EntitySuggestBox;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.MaxLengthTextArea;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class BookFieldTable extends FieldTable {
	private Book book;
	private Form form;
	private BookPageData pageData;
	private String lastIsbn = "";
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private BookEditDialog dialog;
	private FormField isbnField;

	public BookFieldTable(Form f, Book b, BookPageData pd) {
		this.book = b;
		this.form = f;
		this.pageData = pd;

		setWidth("100%");

		if (!book.isSaved()) {
			addField("", "Save typing by doing a book lookup using one of the two fields below.");
		}

		// optional fields
		final Label isbnDisplay = new Label();
		final TextBox isbnInput = new TextBox();
		isbnInput.setMaxLength(13);
		Anchor isbnLink = new Anchor("ISBN:", "http://en.wikipedia.org/wiki/International_Standard_Book_Number");
		isbnLink.setTarget("_blank");

		isbnField = form.createFormField(isbnLink, isbnInput, isbnDisplay);
		isbnField.setInitializer(new Command() {
			@Override
			public void execute() {
				isbnDisplay.setText(Common.getDefaultIfNull(book.getIsbn()));
				isbnInput.setText(book.getIsbn());
				if (!book.isSaved()) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							isbnInput.setFocus(true);
						}
					});
				}
			}
		});

		isbnField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				String isbn = isbnInput.getText();
				isbn = isbn.replaceAll("[^0-9xX]+", "");
				isbnInput.setText(isbn);
				book.setIsbn(isbn);
			}
		});

		if (!book.isSaved()) {
			isbnInput.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					lookupByIsbn();
				}
			});
		}
		addField(isbnField);

		if (!book.isSaved()) {
			final EntitySuggestBox titleSearchBox = new EntitySuggestBox("Book");
			titleSearchBox.setFontSize(11);
			titleSearchBox.setRequestLimit(12);
			Data options = new Data("googleLookup", true);
			titleSearchBox.setOptions(options);
			titleSearchBox.setSelectionHandler(new ParameterHandler<HtmlSuggestion>() {
				@Override
				public void execute(HtmlSuggestion sug) {
					isbnInput.setText(sug.getStringId());
					lookupByIsbn();
				}
			});
			PaddedPanel tp = new PaddedPanel();
			tp.add(titleSearchBox);
			tp.add(new Label("(whole words)"));
			addField("Title search:", tp);
			addField("", new Label());
		}

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

		final Label subTitleDisplay = new Label();
		final TextBox subTitleInput = new TextBox();
		subTitleInput.setVisibleLength(50);
		subTitleInput.setMaxLength(200);
		FormField subTitleField = form.createFormField("Subtitle:", subTitleInput, subTitleDisplay);
		subTitleField.setInitializer(new Command() {
			@Override
			public void execute() {
				subTitleDisplay.setText(Common.getDefaultIfNull(book.getSubTitle()));
				subTitleInput.setText(book.getSubTitle());
			}
		});
		subTitleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setSubTitle(subTitleInput.getText());
			}
		});
		addField(subTitleField);

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

		final HTML authorDisplay = new HTML();
		final MaxLengthTextArea authorInput = new MaxLengthTextArea(500);
		if (!book.isSaved()) {
			authorInput.setHeight("20px");
		}
		FormField authorField = form.createFormField("Author(s):", authorInput, authorDisplay);
		authorField.setInitializer(new Command() {
			@Override
			public void execute() {
				authorDisplay.setHTML(Formatter.formatNoteText(Common.getDefaultIfNull(book.getAuthor())));
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

		final HTML descriptionDisplay = new HTML();
		descriptionDisplay.addStyleName("bookDescription");
		final MaxLengthTextArea descriptionInput = new MaxLengthTextArea(10000);
		if (!book.isSaved()) {
			descriptionInput.setHeight("40px");
		}
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setHTML(Formatter.formatNoteText(Common.getDefaultIfNull(book.getDescription())));
				descriptionInput.setText(book.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setDescription(descriptionInput.getText());
			}
		});
		addField(descriptionField);

		final Label publisherDisplay = new Label();
		final TextBox publisherInput = new TextBox();
		publisherInput.setVisibleLength(50);
		publisherInput.setMaxLength(200);
		FormField publisherField = form.createFormField("Publisher:", publisherInput, publisherDisplay);
		publisherField.setInitializer(new Command() {
			@Override
			public void execute() {
				publisherDisplay.setText(Common.getDefaultIfNull(book.getPublisher()));
				publisherInput.setText(book.getPublisher());
			}
		});
		publisherField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				book.setPublisher(publisherInput.getText());
			}
		});
		addField(publisherField);

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
		imageInput.setVisibleLength(50);
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

	public void setBook(Book b) {
		this.book = b;
	}

	public void setDialog(BookEditDialog dialog) {
		this.dialog = dialog;
	}

	private void lookupByIsbn() {
		isbnField.updateDto();
		String isbn = book.getIsbn();
		if (lastIsbn.equals(isbn) || isbn.isEmpty()) {
			return;
		}

		lastIsbn = isbn;

		bookService.fetchGoogleData(book, new Callback<Book>() {
			@Override
			protected void doOnSuccess(Book result) {
				book = result;
				if (book.getTitle() == null) {
					AlertDialog.alert("Could not find book data for that ISBN.");
				}
				form.setDto(result);
				if (dialog != null) {
					dialog.setEntity(result);
				}
				form.initialize();
			}
		});
	}

}
