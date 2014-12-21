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
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ArticleListPage implements Page {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private ArgMap<ArticleArg> args = new ArgMap<ArticleArg>(Status.ACTIVE);
	private TilePanel fp = new TilePanel();
	private ArrayList<Article> articles;

	public ArticleListPage(final VerticalPanel page) {
		args.put(ArticleArg.ONLY_TAGGED);
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(ArticleArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}
		final String title = "Articles";

		CookieCrumb cc = new CookieCrumb();
		cc.add(new Hyperlink("Articles By Type", PageUrl.tagGroup("ARTICLE")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			cc.add(URL.decode(Url.getParameter("tn")));
		} else {
			cc.add("Articles");
		}
		page.add(cc);

		if (Application.memberOf(33)) {
			AddLink link = new AddLink("Add Article", PageUrl.article(0));
			link.getElement().getStyle().setMarginLeft(10, Unit.PX);
			page.add(link);
		}

		PaddedPanel searchBox = new PaddedPanel();
		searchBox.addStyleName("boxedBlurb");
		searchBox.setSpacing(8);
		searchBox.add(new Label("Search:"));
		final TextBox searchInput = new TextBox();
		searchInput.setVisibleLength(35);
		searchBox.add(searchInput);
		searchInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				search(searchInput.getText());
			}
		});

		searchInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					search(searchInput.getText());
				}
			}
		});

		page.add(searchBox);

		page.add(fp);
		Application.getLayout().setPage(title, page);
		populate();
	}

	private void populate() {
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				articles = result;
				fp.clear();

				for (Article a : result) {
					fp.add(new ArticleTile(a), a.getId());
				}
			}
		});
	}

	private void search(String text) {
		if (text == null || text.isEmpty()) {
			fp.showAll();
			return;
		}

		text = text.toLowerCase();

		for (Article a : articles) {
			String articleText = new HTML(a.getArticle()).getText().toLowerCase();
			fp.setVisible(a, a.getTitle().toLowerCase().contains(text) || articleText.contains(text));
		}
	}
}
