package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BlogPage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel page = new VerticalPanel();
	private int blogId = Url.getIntegerParameter("postId");
	private List<Article> items;
	private Article item;
	private VerticalPanel blogPanel = new VerticalPanel();
	private ArgMap<ArticleArg> args;
	private static HandlerRegistration registration;

	public BlogPage(VerticalPanel p) {
		page = p;
		page.getElement().getStyle().setPaddingLeft(20, Unit.PX);

		args = new ArgMap<ArticleArg>(ArticleArg.OWNING_ORG_ID, Application.getCurrentOrgId());
		args.put(ArticleArg.MOST_RECENT, 15);
		args.setStatus(Status.ACTIVE);
		args.put(ArticleArg.BLOG_ONLY);
		if (blogId > 0) {
			args.put(ArticleArg.ARTICLE_ID, blogId);
		}
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				items = result;
				if (blogId > 0) {
					item = result.get(0);
				}

				initialize();
			}
		});
	}

	private void addBlogItems(List<Article> list) {
		for (Article i : list) {
			blogPanel.add(new BlogItemWidget(i));
		}
	}

	private void initialize() {
		Application.getLayout().getBodyPanel().getElement().getStyle().setBackgroundColor("#eeeeee");
		if (registration == null) {
			registration = History.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (event.getValue().contains("page=Blog")) {
						return;
					}

					Application.getLayout().getBodyPanel().getElement().getStyle().setBackgroundColor("#ffffff");
					if (registration != null) {
						registration.removeHandler();
						registration = null;
					}
				}
			});
		}

		blogPanel.getElement().getStyle().setBackgroundColor("#ffffff");
		blogPanel.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		blogPanel.getElement().getStyle().setPaddingRight(10, Unit.PX);
		blogPanel.getElement().getStyle().setPaddingTop(10, Unit.PX);
		blogPanel.getElement().getStyle().setBorderColor("#dddddd");
		blogPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		blogPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

		if (blogId < 0) {
			initListPage();
		} else {
			initSinglePage();
		}

		Sidebar sb = Sidebar.create(MiniModule.ADS);
		Application.getLayout().setPage("Blog", sb, page);
	}

	private void initListPage() {
		// header
		VerticalPanel outerVp = new VerticalPanel();
		Label header = new Label("Blog");
		header.getElement().getStyle().setFontSize(24, Unit.PX);
		header.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		header.addStyleName("bold");

		// filter
		PaddedPanel filterHorizontalPanel = new PaddedPanel();
		filterHorizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		SimplePanel outerGrayPanel = new SimplePanel();
		outerGrayPanel.addStyleName("mediumPadding");
		Label heading = new Label("Find posts");
		heading.addStyleName("bold");
		filterHorizontalPanel.add(heading);
		filterHorizontalPanel.add(new Label("containing text"));
		final TextBox searchBox = new TextBox();

		VerticalPanel cbPanel = new VerticalPanel();
		cbPanel.add(searchBox);

		filterHorizontalPanel.add(cbPanel);
		filterHorizontalPanel.add(new Label("posted after"));
		final ValidatorDateBox afterDateBox = new ValidatorDateBox();
		afterDateBox.setRequired(false);
		final ValidatorDateBox beforeDateBox = new ValidatorDateBox();
		beforeDateBox.setRequired(false);
		filterHorizontalPanel.add(afterDateBox);
		filterHorizontalPanel.add(new Label("and/or before"));
		filterHorizontalPanel.add(beforeDateBox);
		final Button filterButton = new Button("Search");

		filterHorizontalPanel.add(filterButton);

		VerticalPanel filterVerticalPanel = new VerticalPanel();
		final CheckBox i = new CheckBox("Include inactive");
		HorizontalPanel checkBoxPanel = new PaddedPanel();
		checkBoxPanel.getElement().getStyle().setMarginLeft(25, Unit.PX);
		checkBoxPanel.add(i);

		filterVerticalPanel.add(filterHorizontalPanel);
		if (Application.administratorOfCurrentOrg() || Application.memberOf(33)) {
			filterVerticalPanel.add(checkBoxPanel);
		}
		outerGrayPanel.setWidget(filterVerticalPanel);
		outerVp.add(header);
		outerVp.add(outerGrayPanel);
		page.add(outerVp);

		filterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				args.put(ArticleArg.SEARCH, searchBox.getText());
				args.put(ArticleArg.AFTER_DATE, afterDateBox.getValue());
				args.put(ArticleArg.BEFORE_DATE, beforeDateBox.getValue());
				args.put(ArticleArg.BEFORE_ID, 0);
				if (i.getValue()) {
					args.setStatus(Status.ALL);
				} else {
					args.setStatus(Status.ACTIVE);
				}

				filterButton.setEnabled(false);

				articleService.list(args, new Callback<ArrayList<Article>>() {
					@Override
					protected void doOnSuccess(ArrayList<Article> result) {
						blogPanel.clear();
						items.clear();
						items.addAll(result);
						addBlogItems(result);
						filterButton.setEnabled(true);
					}
				});
			}
		});

		searchBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				if (keyCode == KeyCodes.KEY_ENTER) {
					filterButton.click();
				}
			}
		});

		filterHorizontalPanel.add(new ClickLabel("Clear", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchBox.setText("");
				afterDateBox.setValue(null);
				beforeDateBox.setValue(null);
				i.setValue(false);
				filterButton.click();
			}
		}));

		// Add post
		PaddedPanel pp = new PaddedPanel(3);
		pp.setWidth("100%");
		if (Application.administratorOfCurrentOrg() || Application.memberOf(33)) {
			Image edit = new Image(MainImageBundle.INSTANCE.plus());
			edit.addStyleName("pointer");
			ClickHandler cl = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					page.clear();
					blogPanel.clear();
					item = new Article();
					HistoryToken.append("newsId=0", false);
					initSinglePage();
				}
			};
			edit.addClickHandler(cl);
			ClickLabel label = new ClickLabel("Add post");
			label.addClickHandler(cl);
			label.setWordWrap(false);
			pp.add(edit);
			pp.setCellWidth(edit, "1%");
			pp.add(label);
			pp.setCellWidth(label, "1%");
		}

		page.setWidth("auto");
		Anchor signUp = new Anchor("Sign Up For Updates", Constants.CONSTANT_CONTACT_URL);
		signUp.setTarget("_blank");
		pp.add(signUp);
		pp.setCellHorizontalAlignment(signUp, HasHorizontalAlignment.ALIGN_RIGHT);
		page.add(pp);

		page.add(blogPanel);

		addBlogItems(items);

		ClickLabel more = new ClickLabel("Load more news >>", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				args.put(ArticleArg.BEFORE_ID, items.get(items.size() - 1).getId());
				articleService.list(args, new Callback<ArrayList<Article>>() {
					@Override
					protected void doOnSuccess(ArrayList<Article> result) {
						items.addAll(result);
						addBlogItems(result);
					}
				});
			}
		});
		more.addStyleName("heavyPadding bold hugeText");
		page.add(more);
	}

	private void initSinglePage() {
		page.add(blogPanel);

		if (item == null) {
			item = new Article();
		}

		DefaultHyperlink back = new DefaultHyperlink("<< Blog", PageUrl.blog(0));
		blogPanel.add(back);

		blogPanel.add(new BlogItemWidget(item));

		if (blogId > 0) {
			ArgMap<ArticleArg> args = new ArgMap<ArticleArg>(ArticleArg.ARTICLE_ID, blogId);
			blogPanel.add(new BlogCommentSection(blogId, args));
		}
	}
}