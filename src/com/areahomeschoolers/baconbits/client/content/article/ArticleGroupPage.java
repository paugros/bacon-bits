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
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.user.client.ui.VerticalPanel;

public class ArticleGroupPage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel page = new VerticalPanel();

	public ArticleGroupPage(VerticalPanel p) {
		page = p;
		String ids = Url.getParameter("articleIds");

		articleService.list(new ArgMap<ArticleArg>(ArticleArg.IDS, ids), new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				for (Article item : result) {
					page.add(new ArticleWidget(item));
				}

				Sidebar sb = Sidebar.create(MiniModule.ADS);
				Application.getLayout().setPage("Articles", sb, page);
			}
		});
	}
}
