package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.DateTimeBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.Spacer;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewsItemWidget extends Composite {
	private class ReadNewsItem extends Composite {
		private VerticalPanel vp = new VerticalPanel();

		public ReadNewsItem() {
			vp.setWidth(NEWS_ITEM_WIDTH + 6 + "px");
			vp.addStyleName("grayUnderline");

			Hyperlink title = new Hyperlink(item.getTitle(), PageUrl.news(item.getId()));
			title.getElement().getStyle().setFontSize(25, Unit.PX);
			title.getElement().getStyle().setColor("#333333");
			vp.add(title);

			PaddedPanel leftMugPanel = new PaddedPanel(5);
			Image userPhoto = new Image();
			int thumbNailPixelHeight = 70;
			int thumbNailPixelWidth = 70;

			int imageId = item.getImageDocumentId();
			if (imageId > 0) {
				userPhoto.setUrl(Document.toUrl(imageId));
			} else {
				userPhoto.setResource(MainImageBundle.INSTANCE.pixel());
			}
			userPhoto.setWidth(thumbNailPixelWidth - 10 + "px");
			userPhoto.setHeight(thumbNailPixelHeight - 10 + "px");
			leftMugPanel.add(userPhoto);

			VerticalPanel mugVP = new VerticalPanel();
			leftMugPanel.add(mugVP);

			if (canEdit()) {
				PaddedPanel pp = new PaddedPanel(3);
				Image edit = new Image(MainImageBundle.INSTANCE.edit());
				edit.addStyleName("pointer");
				ClickHandler cl = new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						panel.setWidget(new WriteNewsItem());
					}
				};
				edit.addClickHandler(cl);
				ClickLabel label = new ClickLabel("Edit post");
				label.addClickHandler(cl);
				pp.add(edit);
				pp.add(label);
				mugVP.add(pp);
			}

			HorizontalPanel hp = new HorizontalPanel();
			hp.setWidth(NEWS_ITEM_WIDTH + "px");
			HorizontalPanel userPanel = new PaddedPanel(3);
			userPanel.add(new Label("By " + item.getAddedByFirstName() + " " + item.getAddedByLastName()));
			hp.add(userPanel);

			PaddedPanel commentsPanel = new PaddedPanel();

			String s = "s";
			if (item.getCommentCount() == 1) {
				s = "";
			}
			Hyperlink commentLink = new Hyperlink(item.getCommentCount() + " comment" + s, PageUrl.news(item.getId()) + "&comment=1");
			commentLink.addStyleName("largeText");
			commentsPanel.add(commentLink);

			if (item.getLastCommentDate() != null) {
				Label lastCommentLabel = new Label("(Last: " + Formatter.formatDateTime(item.getLastCommentDate()) + ")");
				commentsPanel.add(lastCommentLabel);
				commentsPanel.setCellVerticalAlignment(lastCommentLabel, HasVerticalAlignment.ALIGN_MIDDLE);
			}

			hp.add(commentsPanel);
			hp.setCellHorizontalAlignment(commentsPanel, HasHorizontalAlignment.ALIGN_RIGHT);
			mugVP.add(hp);

			Label date = new Label(Formatter.formatDate(item.getAddedDate(), "MMMM d, yyyy h:mm a"));
			date.getElement().getStyle().setColor("#666666");
			mugVP.add(date);

			vp.add(leftMugPanel);

			HTML body = new HTML(item.getArticle());
			body.getElement().getStyle().setWidth(NEWS_ITEM_WIDTH + 1, Unit.PX);
			body.getElement().getStyle().setOverflowX(Overflow.AUTO);
			body.getElement().getStyle().setMarginBottom(50, Unit.PX);
			body.getElement().getStyle().setPadding(5, Unit.PX);
			vp.add(body);

			if (Application.isAuthenticated()) {
				vp.add(new Hyperlink("Add comment", PageUrl.news(item.getId()) + "&comment=1"));
				vp.add(new Spacer(1));
			}

			initWidget(vp);
		}
	}

	private class WriteNewsItem extends Composite {
		private Form form = new Form(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				saveItem();
			}
		});
		private final ArticleServiceAsync newsService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
		private VerticalPanel vp = new VerticalPanel();

		public WriteNewsItem() {
			final FieldTable ft = new FieldTable();
			ft.getFlexTable().removeStyleName("sectionContent");
			ft.getFlexTable().getColumnFormatter().setWidth(0, "140px");

			final RequiredTextBox titleInput = new RequiredTextBox();
			titleInput.setMaxLength(100);
			titleInput.setVisibleLength(68);
			titleInput.getElement().getStyle().setFontSize(25, Unit.PX);
			FormField titleField = form.createFormField("", titleInput, null);
			titleField.setInitializer(new Command() {
				@Override
				public void execute() {
					titleInput.setText(item.getTitle());
				}
			});
			titleField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					item.setTitle(titleInput.getText());
				}
			});

			Label l = new Label("Title");
			l.addStyleName("smallText fadedText");
			vp.add(l);
			vp.add(titleInput);

			final Label accessDisplay = new Label();
			final ItemVisibilityWidget accessInput = new ItemVisibilityWidget();
			accessInput.showOnlyCurrentOrganization();
			accessInput.removeItem(VisibilityLevel.PRIVATE);
			accessInput.removeItem(VisibilityLevel.MY_GROUPS);
			FormField accessField = form.createFormField("Visible to:", accessInput, accessDisplay);
			accessField.setInitializer(new Command() {
				@Override
				public void execute() {
					String text = item.getVisibilityLevel();
					if (item.getGroupId() != null && item.getGroupId() > 0) {
						text += " - " + item.getGroupName();
					}
					accessDisplay.setText(text);
					accessInput.setVisibilityLevelId(item.getVisibilityLevelId());
					accessInput.setGroupId(item.getGroupId());
				}
			});
			accessField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					item.setVisibilityLevelId(accessInput.getVisibilityLevelId());
					item.setGroupId(accessInput.getGroupId());
				}
			});
			ft.addField(accessField);

			final DateTimeBox effectiveInput = new DateTimeBox();
			FormField effectiveField = form.createFormField("Effective date:", effectiveInput, null);
			effectiveField.setInitializer(new Command() {
				@Override
				public void execute() {
					effectiveInput.setValue(item.getStartDate());
				}
			});
			effectiveField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					item.setStartDate(effectiveInput.getValue());
				}
			});
			ft.addField(effectiveField);

			final DateTimeBox expirationInput = new DateTimeBox();
			final FormField expirationField = form.createFormField("Expiration date:", expirationInput, null);
			expirationField.setInitializer(new Command() {
				@Override
				public void execute() {
					expirationInput.setValue(item.getEndDate());
				}
			});
			expirationField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					item.setEndDate(expirationInput.getValue());
				}
			});
			ft.addField(expirationField);

			vp.add(ft);

			final ControlledRichTextArea bodyInput = new ControlledRichTextArea();
			bodyInput.getTextArea().setWidth(NewsItemWidget.NEWS_ITEM_WIDTH + 10 + "px");
			FormField bodyField = form.createFormField("Body:", bodyInput, null);
			bodyField.setInitializer(new Command() {
				@Override
				public void execute() {
					bodyInput.getTextArea().setHTML(item.getArticle());
				}
			});
			bodyField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					item.setArticle(bodyInput.getTextArea().getHTML());
				}
			});

			vp.add(bodyInput);

			form.configureForAdd();
			Button cancel = new Button("Cancel");
			cancel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Application.setConfirmNavigation(false);
					if (item.isSaved()) {
						panel.setWidget(new ReadNewsItem());
					} else {
						HistoryToken.set(PageUrl.news(0));
					}
				}
			});
			form.getButtonPanel().addCenterButton(cancel);

			form.initialize();

			vp.add(form.getBottomPanel());

			initWidget(vp);

			Application.setConfirmNavigation(true);
		}

		private void saveItem() {
			form.getSubmitButton().setEnabled(false);
			item.setOwningOrgId(Application.getCurrentOrgId());
			item.setNewsItem(true);
			newsService.save(item, new Callback<Article>() {
				@Override
				protected void doOnSuccess(Article result) {
					Application.setConfirmNavigation(false);
					if (item.isSaved()) {
						item = result;
						panel.setWidget(new ReadNewsItem());
					} else {
						HistoryToken.set(PageUrl.news(result.getId()));
					}
				}
			});
		}
	}

	public static final int NEWS_ITEM_WIDTH = 750;
	private SimplePanel panel = new SimplePanel();
	private Article item;

	public NewsItemWidget(Article item) {
		this.item = item;

		if (item.isSaved()) {
			panel.setWidget(new ReadNewsItem());
		} else {
			panel.setWidget(new WriteNewsItem());
		}

		initWidget(panel);
	}

	private boolean canEdit() {
		if (!Application.isAuthenticated()) {
			return false;
		}
		return Application.getCurrentUser().getId() == item.getAddedById() || Application.administratorOf(Application.getCurrentOrg());
	}

}
