package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.content.user.AccessLevelListBox;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.GroupListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.WidgetCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ArticlePage implements Page {
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save(formField);
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

	private boolean allowEdit() {
		if (article.getGroupId() == null) {
			return Application.isSystemAdministrator();
		}
		return Application.administratorOf(article.getGroupId());
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleDisplay.addStyleName("hugeText");
		titleInput.addStyleName("hugeText");
		titleInput.setVisibleLength(65);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
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

		if (Application.isAuthenticated()) {
			final Label accessDisplay = new Label();
			final DefaultListBox accessInput = new AccessLevelListBox(article.getGroupId());
			FormField accessField = form.createFormField("Visible to:", accessInput, accessDisplay);
			accessField.setInitializer(new Command() {
				@Override
				public void execute() {
					accessDisplay.setText(article.getAccessLevel());
					accessInput.setValue(article.getAccessLevelId());
				}
			});
			accessField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					article.setAccessLevelId(accessInput.getIntValue());
				}
			});
			fieldTable.addField(accessField);

			final Label groupDisplay = new Label();
			WidgetCreator groupCreator = new WidgetCreator() {
				@Override
				public Widget createWidget() {
					return new GroupListBox(article.getGroupId());
				}
			};
			final FormField groupField = form.createFormField("Group:", groupCreator, groupDisplay);
			groupField.setInitializer(new Command() {
				@Override
				public void execute() {
					groupDisplay.setText(Common.getDefaultIfNull(article.getGroupName(), "None"));
					if (groupField.inputIsCreated()) {
						((GroupListBox) groupField.getInputWidget()).setValue(article.getGroupId());
					}
				}
			});
			groupField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					article.setGroupId(((GroupListBox) groupField.getInputWidget()).getIntValue());
				}
			});
			fieldTable.addField(groupField);
		}

		if (article.isSaved() && (article.hasTags() || Application.administratorOf(article.getGroupId()))) {
			TagSection ts = new TagSection(TagMappingType.ARTICLE, article.getId());
			ts.setEditingEnabled(Application.administratorOf(article.getGroupId()));
			fieldTable.addField("Tags:", ts);
			ts.populate();
		}

		final ControlledRichTextArea dataInput = new ControlledRichTextArea();
		final HTML dataDisplay = new HTML();
		dataDisplay.setWidth("800px");
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

		if (article.isSaved() && (article.hasDocuments() || Application.administratorOf(article.getGroupId()))) {
			DocumentSection ds = new DocumentSection(article, Application.administratorOf(article.getGroupId()));
			ds.populate();
			fieldTable.addField("Documents:", ds);
		}

		Button cancelButton = new Button("Cancel");
		cancelButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				History.back();
			}
		});

		form.getButtonPanel().insertCenterButton(cancelButton, 0);
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

		page.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH1100PX));

		form.setEnabled(allowEdit());

		Application.getLayout().setPage(title, page);
	}

	private void initializePage() {
		if (!Application.isAuthenticated()) {
			createReadOnlyPage();
		} else {
			createReadWritePage();
		}
	}

	private void save(final FormField field) {
		articleService.save(article, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article a) {
				if (!Url.isParamValidId("articleId")) {
					HistoryToken.set(PageUrl.article(a.getId()));
				} else {
					article = a;
					form.setDto(a);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
