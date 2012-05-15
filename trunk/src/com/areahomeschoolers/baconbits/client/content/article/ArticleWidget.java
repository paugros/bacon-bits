package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArticleWidget extends Composite {

	public ArticleWidget(Article article) {
		VerticalPanel vp = new VerticalPanel();
		Hyperlink title = new Hyperlink(article.getTitle(), PageUrl.article(article.getId()));
		title.addStyleName("hugeText");
		vp.add(title);
		vp.add(new HTML(article.getArticle()));

		initWidget(vp);
	}

}
