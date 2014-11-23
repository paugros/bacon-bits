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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewsPage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel page = new VerticalPanel();
	private int newsId = Url.getIntegerParameter("newsId");
	private List<Article> items;
	private Article item;
	private VerticalPanel newsPanel = new VerticalPanel();
	private ArgMap<ArticleArg> args;
	private static HandlerRegistration registration;

	public NewsPage(VerticalPanel p) {
		page = p;
		page.getElement().getStyle().setPaddingLeft(20, Unit.PX);

		args = new ArgMap<ArticleArg>(ArticleArg.OWNING_ORG_ID, Application.getCurrentOrgId());
		args.put(ArticleArg.MOST_RECENT, 15);
		args.setStatus(Status.ACTIVE);
		args.put(ArticleArg.NEWS_ONLY);
		if (newsId > 0) {
			args.put(ArticleArg.ARTICLE_ID, newsId);
		}
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				items = result;
				if (newsId > 0) {
					item = result.get(0);
				}

				initialize();
			}
		});
	}

	private void addNewsItems(List<Article> list) {
		for (Article i : list) {
			newsPanel.add(new NewsItemWidget(i));
		}
	}

	private void initialize() {
		Application.getLayout().getBodyPanel().getElement().getStyle().setBackgroundColor("#eeeeee");
		if (registration == null) {
			registration = History.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (event.getValue().contains("page=News")) {
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

		newsPanel.getElement().getStyle().setBackgroundColor("#ffffff");
		newsPanel.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		newsPanel.getElement().getStyle().setPaddingRight(10, Unit.PX);
		newsPanel.getElement().getStyle().setPaddingTop(10, Unit.PX);
		newsPanel.getElement().getStyle().setBorderColor("#dddddd");
		newsPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		newsPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

		if (newsId < 0) {
			initListPage();
		} else {
			initSinglePage();
		}

		Sidebar sb = Sidebar.create(MiniModule.CITRUS, MiniModule.LINKS, MiniModule.ADS);
		Application.getLayout().setPage("News Bulletin", sb, page);
	}

	private void initListPage() {
		// header
		VerticalPanel outerVp = new VerticalPanel();
		Label header = new Label("News Bulletin");
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
						newsPanel.clear();
						items.clear();
						items.addAll(result);
						addNewsItems(result);
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
		if (Application.administratorOfCurrentOrg() || Application.memberOf(33)) {
			PaddedPanel pp = new PaddedPanel(3);
			Image edit = new Image(MainImageBundle.INSTANCE.plus());
			edit.addStyleName("pointer");
			ClickHandler cl = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					page.clear();
					newsPanel.clear();
					item = new Article();
					HistoryToken.append("newsId=0", false);
					initSinglePage();
				}
			};
			edit.addClickHandler(cl);
			ClickLabel label = new ClickLabel("Add post");
			label.addClickHandler(cl);
			pp.add(edit);
			pp.add(label);

			page.add(pp);
		}

		page.add(newsPanel);

		addNewsItems(items);

		ClickLabel more = new ClickLabel("Load more news >>", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				args.put(ArticleArg.BEFORE_ID, items.get(items.size() - 1).getId());
				articleService.list(args, new Callback<ArrayList<Article>>() {
					@Override
					protected void doOnSuccess(ArrayList<Article> result) {
						items.addAll(result);
						addNewsItems(result);
					}
				});
			}
		});
		more.addStyleName("heavyPadding bold hugeText");
		page.add(more);
	}

	private void initSinglePage() {
		page.add(newsPanel);

		if (item == null) {
			item = new Article();
		}

		Hyperlink back = new Hyperlink("<< News list", PageUrl.news(0));
		newsPanel.add(back);

		newsPanel.add(new NewsItemWidget(item));

		if (newsId > 0) {
			ArgMap<ArticleArg> args = new ArgMap<ArticleArg>(ArticleArg.ARTICLE_ID, newsId);
			newsPanel.add(new NewsCommentSection(newsId, args));
		}
	}
}
