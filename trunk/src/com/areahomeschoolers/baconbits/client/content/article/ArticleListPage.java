package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.ViewMode;
import com.areahomeschoolers.baconbits.client.content.article.ArticleTable.ArticleColumn;
import com.areahomeschoolers.baconbits.client.content.tag.SearchSection;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SortDirection;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ArticleListPage implements Page {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private ArgMap<ArticleArg> args = new ArgMap<ArticleArg>(Status.ACTIVE);
	private TilePanel fp = new TilePanel();
	private ArrayList<Article> articles;
	private SimplePanel sp = new SimplePanel();
	private ArticleTable table = new ArticleTable(args);
	private ViewMode viewMode = ViewMode.GRID;
	private VerticalPanel page;

	public ArticleListPage(final VerticalPanel page) {
		this.page = page;
		args.put(ArticleArg.ONLY_TAGGED);
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(ArticleArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}
		String title = "Articles";

		table.setDisplayColumns(ArticleColumn.IMAGE, ArticleColumn.TITLE, ArticleColumn.TAGS);
		table.setDefaultSortColumn(ArticleColumn.TITLE, SortDirection.SORT_ASC);
		table.addStyleName(ContentWidth.MAXWIDTH1000PX.toString());

		CookieCrumb cc = new CookieCrumb();
		cc.add(new DefaultHyperlink("Articles By Type", PageUrl.tagGroup("ARTICLE")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			String tag = URL.decode(Url.getParameter("tn"));
			cc.add(tag);
			title = "Articles about " + tag;
		} else {
			cc.add("Articles");
		}
		page.add(cc);

		if (Application.memberOf(33)) {
			AddLink link = new AddLink("Add Article", PageUrl.article(0));
			link.getElement().getStyle().setMarginLeft(10, Unit.PX);
			page.add(link);
			page.setCellWidth(link, "1%");
		}

		createSearchBox();

		sp.setWidget(fp);
		page.add(sp);
		Application.getLayout().setPage(title, page);
		populate();
	}

	private void applyFilter(String text) {
		if (text == null || text.isEmpty()) {
			if (viewMode == ViewMode.GRID) {
				fp.showAll();
			} else {
				table.showAllItems();
			}
			return;
		}

		text = text.toLowerCase();

		for (Article a : articles) {
			String articleText = new HTML(a.getArticle()).getText().toLowerCase();
			boolean visible = a.getTitle().toLowerCase().contains(text) || articleText.contains(text);
			if (viewMode == ViewMode.GRID) {
				fp.setVisible(a, visible);
			} else {
				table.setItemVisible(a, visible);
			}
		}
	}

	private void createSearchBox() {
		VerticalPanel searchBox = new VerticalPanel();
		searchBox.addStyleName("boxedBlurb");
		searchBox.setSpacing(8);

		PaddedPanel search = new PaddedPanel();
		search.add(new Label("Search:"));
		final TextBox searchInput = new TextBox();
		searchInput.setVisibleLength(35);
		search.add(searchInput);
		searchInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				applyFilter(searchInput.getText());
			}
		});

		searchInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					applyFilter(searchInput.getText());
				}
			}
		});

		VerticalPanel cp = new VerticalPanel();
		final ClickLabel view = new ClickLabel("List view");
		view.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (viewMode == ViewMode.GRID) {
					viewMode = ViewMode.LIST;
					view.setText("Grid view");

					sp.setWidget(table);
				} else {
					viewMode = ViewMode.GRID;
					view.setText("List view");

					sp.setWidget(fp);
				}

				populate(articles);
				applyFilter(searchInput.getText());
			}
		});

		ClickLabel reset = new ClickLabel("Reset search", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Application.reloadPage();
			}
		});

		cp.add(view);
		cp.add(reset);

		searchBox.add(search);
		searchBox.add(cp);
		searchBox.setCellHorizontalAlignment(cp, HasHorizontalAlignment.ALIGN_RIGHT);

		page.add(new SearchSection(TagMappingType.ARTICLE, searchBox));
	}

	private void populate() {
		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				articles = result;

				populate(articles);
			}
		});
	}

	private void populate(List<Article> articles) {
		if (viewMode == ViewMode.GRID) {
			fp.clear();

			for (Article a : articles) {
				fp.add(new ArticleTile(a), a.getId());
			}
		} else {
			table.populate(articles);
		}
	}
}
