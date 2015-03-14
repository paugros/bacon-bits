package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ArticleWidget extends Composite {

	public ArticleWidget(Article article) {
		boolean homePage = Url.getParameter("page").equals("Home");
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		Widget title;

		if (Application.administratorOf(article) && !homePage) {
			title = new DefaultHyperlink(article.getTitle(), PageUrl.article(article.getId()));
		} else {
			title = new Label(article.getTitle());
		}
		title.addStyleName("hugeText bold");
		vp.add(title);
		vp.add(new HTML(article.getArticle()));

		if (!homePage && article.isSaved() && (article.hasDocuments() || Application.administratorOf(article))) {
			DocumentSection ds = new DocumentSection(article, Application.administratorOf(article));
			ds.init();
			vp.add(ds);
		}

		initWidget(WidgetFactory.wrapForWidth(vp, ContentWidth.MAXWIDTH1000PX));
	}

}
