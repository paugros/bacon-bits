package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.content.article.ArticleTable.ArticleColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellSelector;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

public class ArticleSelector extends EntityCellSelector<Article, ArticleArg, ArticleColumn> {
	private ArticleTable table;

	public ArticleSelector() {
		setModal(false);
		setText("Select an Article");
	}

	public ArticleSelector(ArgMap<ArticleArg> args) {
		this();
		table = new ArticleTable(args);
		setEntityCellTable(table);
	}

	@Override
	public EntityCellTable<Article, ArticleArg, ArticleColumn> getCellTable() {
		return table;
	}

}
