package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.MiniModuleSidebar;
import com.areahomeschoolers.baconbits.client.content.MiniModuleSidebar.MiniModule;
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

				MiniModuleSidebar sb = MiniModuleSidebar.create(MiniModule.LINKS, MiniModule.MY_EVENTS, MiniModule.NEW_EVENTS, MiniModule.UPCOMING_EVENTS,
						MiniModule.CITRUS);
				Application.getLayout().setPage("Articles", sb, page);
			}
		});
	}
}
