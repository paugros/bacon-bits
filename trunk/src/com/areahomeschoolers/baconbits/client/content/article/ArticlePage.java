package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule.AdDirection;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.FormToggleHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.GroupPolicy;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	private TagSection tagSection;
	private FormField tagField;
	private String title;

	public ArticlePage(VerticalPanel p) {
		final int articleId = Url.getIntegerParameter("articleId");

		if (!Application.isAuthenticated() && articleId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = p;

		articleService.getById(articleId, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article result) {
				if (articleId > 0 && result == null) {
					new ErrorPage(PageError.PAGE_NOT_FOUND);
					return;
				}

				if (result != null) {
					article = result;
				}

				if (Url.getBooleanParameter("details") && !Application.administratorOf(article)) {
					new ErrorPage(PageError.NOT_AUTHORIZED);
					return;
				}

				title = article.isSaved() ? article.getTitle() : "New Resource";

				CookieCrumb cc = new CookieCrumb();
				cc.add(new DefaultHyperlink("Articles By Type", PageUrl.tagGroup("ARTICLE")));
				cc.add(new DefaultHyperlink("Articles", PageUrl.articleList()));
				if (Url.getBooleanParameter("details")) {
					cc.add(new DefaultHyperlink(title, PageUrl.article(article.getId())));
					cc.add("Edit details");
				} else {
					cc.add(title);
				}
				page.add(cc);

				if (article.isSaved() && !Url.getBooleanParameter("details")) {
					createViewPage();
				} else {
					createDetailsPage();
				}

				Application.getLayout().setPage(title, page);
			}
		});
	}

	private boolean allowEdit() {
		if (!article.isSaved() && Application.isCitrus()) {
			return true;
		}
		return Application.administratorOf(article);
	}

	private void createDetailsPage() {
		if (noTitle) {
			createTextPage();
		} else {
			createFieldTable();
			form.initialize();

			if (!article.isSaved()) {
				Application.setConfirmNavigation(true);
				String policy = Url.getParameter("gp");
				if (!Common.isNullOrBlank(policy)) {
					try {
						article.setGroupPolicy(GroupPolicy.valueOf(policy));
					} catch (Exception e) {
					}
				}
				form.configureForAdd(fieldTable);
			} else {
				Application.setConfirmNavigation(false);
				form.emancipate();
			}

			page.add(fieldTable);

			form.setEnabled(allowEdit());
		}
	}

	private void createFieldTable() {
		fieldTable.addStyleName(ContentWidth.MAXWIDTH900PX.toString());

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

			if (article.isSaved()) {
				fieldTable.addField("View count:", Integer.toString(article.getViewCount()));
			}
		}

		createTagSection();
		fieldTable.addField(tagField);

		final ControlledRichTextArea dataInput = new ControlledRichTextArea();
		final HTML dataDisplay = new HTML();
		dataDisplay.getElement().getStyle().setPadding(10, Unit.PX);
		if (!ClientUtils.isMobileBrowser()) {
			dataDisplay.setWidth("800px");
		}
		dataDisplay.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		final FormField dataField = form.createFormField("", dataInput, dataDisplay);
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
		dataField.addFormToggleHandler(new FormToggleHandler() {
			@Override
			public void onFormToggle(final boolean inputsAreVisible) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (dataField.getInputWidget().isVisible() == false) {
							Application.setConfirmNavigation(false);
						} else {
							Application.setConfirmNavigation(true);
						}
					}
				});
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
				ConfirmDialog.confirm("Are you sure you want to leave this page? Unsaved data will be lost.", new ConfirmHandler() {
					@Override
					public void onConfirm() {
						History.back();
						Application.setConfirmNavigation(false);
					}
				});
			}
		});

		form.getButtonPanel().insertCenterButton(cancelButton, 0);
	}

	private void createTagSection() {
		tagSection = new TagSection(TagType.ARTICLE, article.getId());
		tagSection.setEditingEnabled(allowEdit());
		tagSection.populate();

		tagField = form.createFormField("Tags:", tagSection);
		tagField.removeEditLabel();
	}

	private void createTextPage() {
		HTML h = new HTML(article.getArticle());
		h.setWidth("1000px");
		page.add(h);
		Application.getLayout().setPage(article.getTitle(), sidebar, page);
	}

	private void createViewPage() {
		HorizontalPanel pp = new HorizontalPanel();
		pp.setWidth("100%");
		pp.getElement().getStyle().setMarginLeft(10, Unit.PX);

		String titleText = "<div style=\"padding: 4px;\" class=hugeText>" + article.getTitle() + "</div>";
		if (article.getNewsItem()) {
			titleText += "<div style=\"padding: 4px;\">By " + article.getAddedByFirstName() + " " + article.getAddedByLastName() + "</div>";
		}
		pp.add(new HTML(titleText));

		if (allowEdit()) {
			DefaultHyperlink edit = new DefaultHyperlink("Edit details", PageUrl.article(article.getId()) + "&details=true");
			edit.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
			edit.getElement().getStyle().setMarginRight(15, Unit.PX);
			pp.add(edit);
			pp.setCellHorizontalAlignment(edit, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		VerticalPanel ovp = new VerticalPanel();
		ovp.addStyleName("sectionContent");

		ovp.add(pp);

		TagSection ts = new TagSection(TagType.ARTICLE, article.getId());
		ts.setEditingEnabled(false);
		ts.populate();
		ts.getElement().getStyle().setMarginLeft(10, Unit.PX);

		ovp.add(ts);

		if (!Common.isNullOrBlank(article.getArticle())) {
			String html = article.getArticle();

			HTML desc = new HTML(html);

			desc.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
			desc.getElement().getStyle().setMarginLeft(15, Unit.PX);
			desc.getElement().getStyle().setMarginRight(15, Unit.PX);
			desc.getElement().getStyle().setMarginBottom(15, Unit.PX);
			desc.getElement().getStyle().setPadding(10, Unit.PX);
			desc.getElement().getStyle().setBackgroundColor("#ffffff");
			desc.getElement().getStyle().setBorderColor("#cccccc");
			desc.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			desc.getElement().getStyle().setBorderWidth(1, Unit.PX);

			ovp.add(desc);
			ovp.addStyleName(ContentWidth.MAXWIDTH800PX.toString());
		}

		if (article.hasDocuments()) {
			DocumentSection ds = new DocumentSection(article, false);
			ds.init();
			ds.getElement().getStyle().setPadding(8, Unit.PX);
			ovp.add(ds);
		}

		VerticalPanel outerPanel = new VerticalPanel();
		outerPanel.add(ovp);
		outerPanel.add(new AdsMiniModule(AdDirection.HORIZONTAL));

		page.add(outerPanel);
	}

	private void save(final FormField field) {
		Application.setConfirmNavigation(false);

		articleService.save(article, new Callback<Article>() {
			@Override
			protected void doOnSuccess(final Article a) {
				if (!Url.isParamValidId("articleId")) {
					if (article.getGroupPolicy() != null) {
						Application.getCurrentOrg().setPolicyId(article.getGroupPolicy(), article.getId());
					}
					tagSection.saveAll(a.getId(), new Callback<Void>() {
						@Override
						protected void doOnSuccess(Void result) {
							HistoryToken.set(PageUrl.article(a.getId()));
						}
					});
				} else {
					article = a;
					form.setDto(a);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
