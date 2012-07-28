package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomePage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private HorizontalPanel hp = new HorizontalPanel();
	private SimplePanel articlePanel = new SimplePanel();
	private SimplePanel eventPanel = new SimplePanel();

	public HomePage(VerticalPanel p) {
		page = p;
		hp.add(articlePanel);
		hp.add(eventPanel);
		eventPanel.getElement().getStyle().setBackgroundColor("#dddddd");
		hp.setCellWidth(eventPanel, "250px");
		page.add(hp);

		articleService.getById(6, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article result) {
				articlePanel.setWidget(new ArticleWidget(result));

				Application.getLayout().setPage("Home", page);
			}
		});

		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		args.put(EventArg.UPCOMING_NUMBER, 5);

		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				vp.setSpacing(8);

				Label title = new Label("Upcoming Events");
				title.addStyleName("hugeText");
				vp.add(title);

				for (Event e : result) {
					VerticalPanel ep = new VerticalPanel();
					Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
					link.addStyleName("mediumText");

					ep.add(link);

					Label date = new Label(Formatter.formatDateTime(e.getStartDate()));
					date.addStyleName("italic");
					ep.add(date);

					HTML h = new HTML();
					h.setHTML(e.getDescription());
					String text = h.getText();
					text = text.replaceAll("(\n)+", "\n");
					if (text.length() > 100) {
						text = text.substring(0, 101) + "...";
					}
					ep.add(new HTML(Formatter.formatNoteText(text)));

					vp.add(ep);
				}

				HTML fb = new HTML();
				fb.setHTML("<p><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" mce_href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" target=\"_TOP\" style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; font-variant: normal; font-style: normal; font-weight: normal; color: #3B5998; text-decoration: none;\" mce_style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; font-variant: normal; font-style: normal; font-weight: normal; color: #3B5998; text-decoration: none;\" title=\"WeAre Home Educators\">WeAre Home Educators</a><br><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" mce_href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" target=\"_TOP\" title=\"WeAre Home Educators\"><img src=\"http://badge.facebook.com/badge/111756708899702.553.1216136535.png\" mce_src=\"http://badge.facebook.com/badge/111756708899702.553.1216136535.png\" width=\"120\" height=\"179\" style=\"border: 0px;\" mce_style=\"border: 0px;\"></a><br><a href=\"http://www.facebook.com/business/dashboard/\" mce_href=\"http://www.facebook.com/business/dashboard/\" target=\"_TOP\" style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; font-variant: normal; font-style: normal; font-weight: normal; color: #3B5998; text-decoration: none;\" mce_style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; font-variant: normal; font-style: normal; font-weight: normal; color: #3B5998; text-decoration: none;\" title=\"Make your own badge!\"></a></p>");
				vp.add(fb);

				eventPanel.setWidget(vp);
			}
		});

	}
}
