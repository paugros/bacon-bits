package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArticlePage implements Page {
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save();
		}
	});
	private VerticalPanel page;
	private FieldTable fieldTable = new FieldTable();
	private Article article = new Article();
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public ArticlePage(VerticalPanel page) {
		int articleId = Url.getIntegerParameter("articleId");
		if (!Application.isAuthenticated() && articleId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		if (articleId > 0) {
			articleService.getById(articleId, new Callback<Article>() {
				@Override
				protected void doOnSuccess(Article result) {
					if (result == null) {
						new ErrorPage(PageError.PAGE_NOT_FOUND);
						return;
					}
					article = result;
					initializePage();
				}
			});
		} else {
			initializePage();
		}
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleDisplay.addStyleName("hugeText");
		titleInput.addStyleName("hugeText");
		titleInput.setVisibleLength(65);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("", titleInput, titleDisplay);
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				article.setTitle(titleInput.getText());
			}
		});
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleInput.setText(article.getTitle());
				titleDisplay.setText(article.getTitle());
			}
		});
		fieldTable.addField(titleField);

		final ControlledRichTextArea dataInput = new ControlledRichTextArea();
		final HTML dataDisplay = new HTML();
		FormField dataField = form.createFormField("", dataInput, dataDisplay);
		dataField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				article.setArticle(dataInput.getTextArea().getHTML());
			}
		});
		dataField.setInitializer(new Command() {
			@Override
			public void execute() {
				dataInput.getTextArea().setHTML(article.getArticle());
				dataDisplay.setHTML(article.getArticle());
			}
		});
		fieldTable.addField(dataField);

		Button cancelButton = new Button("Cancel");
		cancelButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				History.back();
			}
		});

		form.getButtonPanel().addCenterButton(cancelButton);
	}

	private void createReadOnlyPage() {
		String title = article.isSaved() ? article.getTitle() : "New Article";
		page.add(new ArticleWidget(article));
		Application.getLayout().setPage(title, page);
	}

	private void createReadWritePage() {
		String title = article.isSaved() ? article.getTitle() : "New Article";

		createFieldTable();
		form.initialize();

		if (!article.isSaved()) {
			form.configureForAdd(fieldTable);
		} else {
			form.emancipate();
		}

		page.add(WidgetFactory.newSection(title, fieldTable));

		Application.getLayout().setPage(article.getTitle(), page);
	}

	private void initializePage() {
		if (!Application.isAuthenticated()) {
			createReadOnlyPage();
		} else {
			createReadWritePage();
		}
	}

	private void save() {
		articleService.save(article, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article a) {
				HistoryToken.set(PageUrl.home());
			}
		});
	}
}
