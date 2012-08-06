package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.document.DocumentSection;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArticleWidget extends Composite {

	public ArticleWidget(Article article) {
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		Hyperlink title = new Hyperlink(article.getTitle(), PageUrl.article(article.getId()));
		title.addStyleName("hugeText bold");
		vp.add(title);
		vp.add(new HTML(article.getArticle()));

		if (article.isSaved() && (article.hasDocuments() || Application.administratorOf(article.getGroupId()))) {
			DocumentSection ds = new DocumentSection(article, Application.administratorOf(article.getGroupId()));
			ds.populate();
			vp.add(ds);
		}

		initWidget(WidgetFactory.wrapForWidth(vp, ContentWidth.MAXWIDTH1100PX));
	}

}
