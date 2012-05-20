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
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArticlePage implements Page {
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save();
		}
	});
	private VerticalPanel page;
	private FieldTable formTable = new FieldTable();
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

	private void initializePage() {
		String title = "Edit Article";
		TitleBar titleBar = new TitleBar(title, TitleBarStyle.SECTION);
		formTable.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.addStyleName("hugeText");
		titleInput.setVisibleLength(65);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("", titleInput, null);
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
			}
		});
		formTable.addField(titleField);

		final ControlledRichTextArea dataInput = new ControlledRichTextArea();
		FormField dataField = form.createFormField("", dataInput, null);
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
			}
		});
		formTable.addField(dataField);

		ButtonPanel buttons = new ButtonPanel();
		Button saveButton = new Button("Save");
		form.registerSubmitButton(saveButton);
		buttons.addCenterButton(saveButton);
		Button cancelButton = new Button("Cancel");
		cancelButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				History.back();
			}
		});
		buttons.addCenterButton(cancelButton);

		buttons.setWidth("800px");
		formTable.addField("", buttons);

		form.initialize();

		page.add(WidgetFactory.newSection(titleBar, formTable));

		Application.getLayout().setPage(title, page);
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
