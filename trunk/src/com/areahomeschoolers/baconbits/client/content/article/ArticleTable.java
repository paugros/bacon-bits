package com.areahomeschoolers.baconbits.client.content.article;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleTable.ArticleColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class ArticleTable extends EntityCellTable<Article, ArticleArg, ArticleColumn> {
	public enum ArticleColumn implements EntityCellTableColumn<ArticleColumn> {
		TITLE("Title"), ADDED_DATE("Added"), ADDED_BY("Added By"), END_DATE("Inactive Date");

		private String title;

		ArticleColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public ArticleTable(ArgMap<ArticleArg> args) {
		this();
		setArgMap(args);
	}

	private ArticleTable() {
		setTitle("Articles");

		setDefaultSortColumn(ArticleColumn.ADDED_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(ArticleColumn.values());

		disablePaging();
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl();
		filterBox.addItem("Active");
		filterBox.addItem("Inactive");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					for (Article article : getFullList()) {
						setItemVisible(article, article.isActive(), false, false, false);
					}
					refreshForCurrentState();
					break;
				case 1:
					for (Article article : getFullList()) {
						setItemVisible(article, !article.isActive(), false, false, false);
					}
					refreshForCurrentState();
					break;
				case 2:
					showAllItems();
					break;
				}
			}
		});
		filterBox.setSelectedIndex(0);

		addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				filterBox.fireEvent(new ChangeEvent() {
				});
			}
		});
	}

	@Override
	protected void fetchData() {
		articleService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (ArticleColumn col : getDisplayColumns()) {
			switch (col) {
			case ADDED_BY:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Article>() {
					@Override
					protected Widget createWidget(Article item) {
						return new DefaultHyperlink(item.getAddedByFirstName() + " " + item.getAddedByLastName(), PageUrl.user(item.getAddedById()));
					}
				});
				break;
			case ADDED_DATE:
				addDateColumn(col, new ValueGetter<Date, Article>() {
					@Override
					public Date get(Article item) {
						return item.getAddedDate();
					}
				});
				break;
			case TITLE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Article>() {
					@Override
					protected Widget createWidget(Article item) {
						return new DefaultHyperlink(item.getTitle(), PageUrl.article(item.getId()));
					}
				});
				break;
			case END_DATE:
				addDateColumn(col, new ValueGetter<Date, Article>() {
					@Override
					public Date get(Article item) {
						return item.getEndDate();
					}
				});
				break;
			default:
				new AssertionError();
				break;
			}
		}
	}

}
