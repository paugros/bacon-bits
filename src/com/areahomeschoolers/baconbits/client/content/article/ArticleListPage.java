package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ArticleListPage implements Page {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private ArgMap<ArticleArg> args = new ArgMap<ArticleArg>();
	private TilePanel fp = new TilePanel();

	public ArticleListPage(final VerticalPanel page) {
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(ArticleArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}
		final String title = "Articles";

		Hyperlink home = new Hyperlink("Home", PageUrl.home());
		Hyperlink cat = new Hyperlink("Articles By Type", PageUrl.tagGroup("ARTICLE"));
		String ccText = home.toString() + "&nbsp;>&nbsp;" + cat.toString();
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			ccText += "&nbsp;>&nbsp;" + URL.decode(Url.getParameter("tn"));
		} else {
			ccText += "&nbsp;>&nbsp;Articles";
		}
		HTML cc = new HTML(ccText);
		cc.addStyleName("largeText");
		page.add(cc);

		if (Application.memberOf(33)) {
			AddLink link = new AddLink("Add Resource", PageUrl.resource(0));
			link.getElement().getStyle().setMarginLeft(10, Unit.PX);
			page.add(link);
		}

		page.add(fp);
		Application.getLayout().setPage(title, page);
		populate();
	}

	private void populate() {
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				fp.clear();

				for (Article a : result) {
					fp.add(new ArticleTile(a), a.getId());
				}
			}
		});
	}
}
