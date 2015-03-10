package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule.AdDirection;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.MailListLink;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Data;

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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BlogPage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel page = new VerticalPanel();
	private List<Article> items;
	private VerticalPanel blogPanel = new VerticalPanel();
	private VerticalPanel sidebar = new VerticalPanel();
	private VerticalPanel topicsPanel = new VerticalPanel();
	private VerticalPanel pagePanel = new VerticalPanel();
	private ArgMap<ArticleArg> args;
	private static HandlerRegistration registration;
	private Button filterButton;
	private SimplePanel morePanel = new SimplePanel();
	private static final int PAGE_SIZE = 15;

	public BlogPage(VerticalPanel p) {
		page = p;
		page.getElement().getStyle().setPaddingLeft(20, Unit.PX);

		sidebar.setWidth("210px");
		args = new ArgMap<ArticleArg>(ArticleArg.BLOG_ONLY);
		args.put(ArticleArg.MOST_RECENT, PAGE_SIZE);
		args.setStatus(Status.ACTIVE);
		int tagId = Url.getIntegerParameter("tagId");
		if (tagId > 0) {
			args.put(ArticleArg.HAS_TAGS, tagId);
		}
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				items = result;

				initialize();
			}
		});
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

		topicsPanel.setSpacing(7);
		populateTopicsPanel();

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
		filterButton = new Button("Search");

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
		pagePanel.add(outerVp);

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

				populate();
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
			AddLink add = new AddLink("Add post", PageUrl.blogPost(0));
			pp.add(add);
			// pp.setCellWidth(add, "1%");
		}

		page.setWidth("auto");
		MailListLink signUp = new MailListLink("Sign Up For Updates");
		pp.add(signUp);
		pp.setCellHorizontalAlignment(signUp, HasHorizontalAlignment.ALIGN_RIGHT);
		pagePanel.add(pp);

		sidebar.add(topicsPanel);
		sidebar.add(new AdsMiniModule(AdDirection.VERTICAL));
		blogPanel.setWidth("100%");
		blogPanel.getElement().getStyle().setMarginTop(10, Unit.PX);

		HorizontalPanel hp = new PaddedPanel(10);
		hp.add(sidebar);
		pagePanel.add(blogPanel);
		hp.add(pagePanel);
		page.add(hp);

		pagePanel.add(morePanel);

		populate();

		Application.getLayout().setPage("Blog", page);
	}

	private void populate() {
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				if (args.getInt(ArticleArg.BEFORE_ID) == 0) {
					blogPanel.clear();
					items.clear();
				}
				items.addAll(result);
				filterButton.setEnabled(true);

				for (Article i : result) {
					blogPanel.add(new BlogItemWidget(i));
				}

				if (result.isEmpty() && blogPanel.getWidgetCount() == 0) {
					Label empty = new Label("No posts have been added yet");
					empty.setWidth("100%");
					empty.getElement().getStyle().setMarginBottom(10, Unit.PX);
					blogPanel.add(empty);
				}

				if (!result.isEmpty() && result.size() == PAGE_SIZE) {
					ClickLabel more = new ClickLabel("Load more >>", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							args.put(ArticleArg.BEFORE_ID, items.get(items.size() - 1).getId());
							populate();
						}
					});
					more.addStyleName("heavyPadding bold largeText");
					morePanel.setWidget(more);
				} else {
					morePanel.clear();
				}
			}
		});
	}

	private void populateTopicsPanel() {
		topicsPanel.clear();

		articleService.getTopics(new ArgMap<ArticleArg>(), new Callback<ArrayList<Data>>() {
			@Override
			protected void doOnSuccess(ArrayList<Data> result) {
				if (result.isEmpty()) {
					return;
				}

				Label title = new Label("Topics");
				title.addStyleName("largeText");
				topicsPanel.add(title);

				for (final Data d : result) {
					ClickLabel link = new ClickLabel(d.get("name") + " (" + d.get("total") + ")", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							args = new ArgMap<>(ArticleArg.MOST_RECENT, PAGE_SIZE);
							args.setStatus(Status.ACTIVE);
							args.put(ArticleArg.HAS_TAGS, d.getId());
							args.put(ArticleArg.BLOG_ONLY);
							HistoryToken.set(PageUrl.blog() + "&tagId=" + d.getId(), false);
							populate();
						}
					});
					link.getElement().getStyle().setMarginLeft(8, Unit.PX);

					topicsPanel.add(link);
				}
			}
		});
	}
}
