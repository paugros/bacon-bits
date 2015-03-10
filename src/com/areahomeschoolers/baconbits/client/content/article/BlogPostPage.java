package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BlogPostPage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel page = new VerticalPanel();
	private int postId = Url.getIntegerParameter("postId");
	private Article item;
	private ArgMap<ArticleArg> args;
	private static HandlerRegistration registration;
	private VerticalPanel blogPanel = new VerticalPanel();

	public BlogPostPage(VerticalPanel p) {
		page = p;
		page.getElement().getStyle().setPaddingLeft(20, Unit.PX);

		args = new ArgMap<ArticleArg>(ArticleArg.OWNING_ORG_ID, Application.getCurrentOrgId());
		args.put(ArticleArg.MOST_RECENT, 15);
		args.setStatus(Status.ACTIVE);
		args.put(ArticleArg.BLOG_ONLY);
		if (postId > 0) {
			args.put(ArticleArg.ARTICLE_ID, postId);
		}
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				if (postId > 0) {
					item = result.get(0);
				}

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

		initSinglePage();

		Sidebar sb = Sidebar.create(MiniModule.ADS);
		Application.getLayout().setPage("Blog", sb, page);
	}

	private void initSinglePage() {
		page.add(blogPanel);

		if (item == null) {
			item = new Article();
		}

		DefaultHyperlink back = new DefaultHyperlink("<< Blog", PageUrl.blog());
		blogPanel.add(back);

		blogPanel.add(new BlogItemWidget(item));

		if (postId > 0) {
			ArgMap<ArticleArg> args = new ArgMap<ArticleArg>(ArticleArg.ARTICLE_ID, postId);
			blogPanel.add(new BlogCommentSection(postId, args));
		}
	}
}