package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.GroupPolicy;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
			save(formField);
		}
	});
	private VerticalPanel page;
	private FieldTable fieldTable = new FieldTable();
	private Article article = new Article();
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private boolean noTitle = Url.getBooleanParameter("noTitle");
	private Sidebar sidebar = new Sidebar();

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
		return Application.administratorOf(article);
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleDisplay.addStyleName("hugeText");
		titleInput.addStyleName("hugeText");
		titleInput.setVisibleLength(50);
		titleInput.setMaxLength(100);
		FormField titleField = form.createFormField("Title:", titleInput, titleDisplay);
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				article.setTitle(titleInput.getText().trim());
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

		if (allowEdit()) {
			final Label accessDisplay = new Label();
			final ItemVisibilityWidget accessInput = new ItemVisibilityWidget();
			accessInput.showOnlyCurrentOrganization();
			accessInput.removeItem(VisibilityLevel.PRIVATE);
			accessInput.removeItem(VisibilityLevel.MY_GROUPS);
			FormField accessField = form.createFormField("Visible to:", accessInput, accessDisplay);
			accessField.setInitializer(new Command() {
				@Override
				public void execute() {
					String text = article.getVisibilityLevel();
					if (article.getGroupId() != null && article.getGroupId() > 0) {
						text += " - " + article.getGroupName();
					}
					accessDisplay.setText(text);
					accessInput.setVisibilityLevelId(article.getVisibilityLevelId());
					accessInput.setGroupId(article.getGroupId());
				}
			});
			accessField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					article.setVisibilityLevelId(accessInput.getVisibilityLevelId());
					article.setGroupId(accessInput.getGroupId());
				}
			});
			fieldTable.addField(accessField);

			final Label endDateDisplay = new Label();
			final ValidatorDateBox endDateInput = new ValidatorDateBox();
			FormField endDateField = form.createFormField("Inactive date:", endDateInput, endDateDisplay);
			endDateField.setInitializer(new Command() {
				@Override
				public void execute() {
					endDateDisplay.setText(Common.getDefaultIfNull(Formatter.formatDate(article.getEndDate())));
					endDateInput.setValue(article.getEndDate());
				}
			});
			endDateField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					article.setEndDate(endDateInput.getValue());
				}
			});
			fieldTable.addField(endDateField);
		}

		if (article.isSaved() && (article.hasTags() || allowEdit())) {
			TagSection ts = new TagSection(TagMappingType.ARTICLE, article.getId());
			ts.setEditingEnabled(allowEdit());
			fieldTable.addField("Tags:", ts);
			ts.populate();
		}

		final ControlledRichTextArea dataInput = new ControlledRichTextArea();
		final HTML dataDisplay = new HTML();
		dataDisplay.getElement().getStyle().setPadding(10, Unit.PX);
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
		fieldTable.addSpanningWidget(dataField);

		if (article.isSaved() && (article.hasDocuments() || Application.administratorOf(article))) {
			DocumentSection ds = new DocumentSection(article, Application.administratorOf(article));
			ds.init();
			fieldTable.addField("Documents:", ds);
		}

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.back();
			}
		});

		form.getButtonPanel().insertCenterButton(cancelButton, 0);
	}

	private void createReadOnlyPage() {
		String title = article.isSaved() ? article.getTitle() : "New Article";
		page.add(new ArticleWidget(article));
		Application.getLayout().setPage(title, sidebar, page);
	}

	private void createReadWritePage() {
		String title = article.isSaved() ? article.getTitle() : "New Article";

		createFieldTable();
		form.initialize();

		if (!article.isSaved()) {
			String policy = Url.getParameter("gp");
			if (!Common.isNullOrBlank(policy)) {
				try {
					article.setGroupPolicy(GroupPolicy.valueOf(policy));
				} catch (Exception e) {
				}
			}
			form.configureForAdd(fieldTable);
		} else {
			form.emancipate();
		}

		page.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH1000PX));

		form.setEnabled(allowEdit());

		Application.getLayout().setPage(title, page);
	}

	private void createTextPage() {
		HTML h = new HTML(article.getArticle());
		h.setWidth("1000px");
		page.add(h);
		Application.getLayout().setPage(article.getTitle(), sidebar, page);
	}

	private void initializePage() {
		sidebar.add(MiniModule.LINKS, MiniModule.MY_EVENTS, MiniModule.NEW_EVENTS, MiniModule.UPCOMING_EVENTS, MiniModule.CITRUS);

		if (noTitle) {
			createTextPage();
		} else if (!Application.administratorOf(article)) {
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
					if (article.getGroupPolicy() != null) {
						Application.getCurrentOrg().setPolicyId(article.getGroupPolicy(), article.getId());
					}
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
