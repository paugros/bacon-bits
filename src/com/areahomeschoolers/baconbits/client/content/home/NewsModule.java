package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewsModule extends Composite {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private ArgMap<ArticleArg> args;
	private List<Article> items;
	private VerticalPanel vp = new VerticalPanel();

	public NewsModule() {
		initWidget(vp);
		args = new ArgMap<ArticleArg>(ArticleArg.OWNING_ORG_ID, Application.getCurrentOrgId());
		args.put(ArticleArg.MOST_RECENT, 5);
		args.setStatus(Status.ACTIVE);
		args.put(ArticleArg.NEWS_ONLY);

		articleService.list(args, new Callback<ArrayList<Article>>() {
			@Override
			protected void doOnSuccess(ArrayList<Article> result) {
				items = result;

				initialize();
			}
		});
	}

	private void initialize() {
		if (items.isEmpty()) {
			removeFromParent();
			return;
		}

		Label heading = new Label("Recent News");
		heading.addStyleName("largeText");
		vp.add(heading);
		vp.setWidth("100%");
		vp.setSpacing(2);

		for (Article item : items) {
			PaddedPanel pp = new PaddedPanel();
			Label date = new Label(Formatter.formatDate(item.getAddedDate(), "M/d"));
			date.setStylePrimaryName("grayText");
			date.setWordWrap(false);
			pp.add(date);
			pp.setCellVerticalAlignment(date, HasVerticalAlignment.ALIGN_MIDDLE);

			Hyperlink title = new InlineHyperlink(item.getTitle(), PageUrl.news(item.getId()));
			title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			title.getElement().getStyle().setFontSize(14, Unit.PX);
			title.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);

			String text = new HTML(item.getArticle()).getText();
			text = text.replaceAll("\\.", ". ");

			Hyperlink body = new InlineHyperlink("- " + text, PageUrl.news(item.getId()));
			body.getElement().getStyle().setHeight(2.83, Unit.EX);
			body.getElement().getStyle().setColor("#777777");

			body.setStyleName("newsModuleBody");

			pp.add(title);
			pp.add(body);
			pp.setCellHorizontalAlignment(title, HasHorizontalAlignment.ALIGN_LEFT);
			pp.setCellHorizontalAlignment(body, HasHorizontalAlignment.ALIGN_LEFT);
			pp.setCellVerticalAlignment(title, HasVerticalAlignment.ALIGN_MIDDLE);

			vp.add(pp);
		}

		Hyperlink more = new Hyperlink(":: More news ::", PageUrl.news(0));
		vp.add(more);
	}

}
