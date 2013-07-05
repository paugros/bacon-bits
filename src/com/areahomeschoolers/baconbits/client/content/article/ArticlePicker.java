package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.content.article.ArticleTable.ArticleColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellPicker;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellSelector;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

public class ArticlePicker extends EntityCellPicker<Article, ArticleArg, ArticleColumn> {
	private ArticleSelector selector;

	public ArticlePicker(ArgMap<ArticleArg> args) {
		selector = new ArticleSelector(args);
		setEntitySelector(selector);
	}

	@Override
	public EntityCellSelector<Article, ArticleArg, ArticleColumn> getSelector() {
		return selector;
	}

}
