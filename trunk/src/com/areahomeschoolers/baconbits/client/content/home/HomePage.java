package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomePage implements Page {
	private class EventModulePanel extends Composite {
		public EventModulePanel(String title, ArrayList<Event> events) {
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			vp.setSpacing(8);

			Label label = new Label(title);
			label.addStyleName("hugeText");
			vp.add(label);
			boolean community = false;

			for (Event e : events) {
				VerticalPanel ep = new VerticalPanel();
				Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
				link.addStyleName("mediumText");

				ep.add(link);

				String subText = Formatter.formatDateTime(e.getStartDate());
				if (e.getCategoryId() == 6) {
					community = true;
					subText += " - " + Formatter.formatCurrency(e.getPrice());
				}
				Label date = new Label(subText);
				date.addStyleName("italic");
				ep.add(date);

				HTML h = new HTML();
				h.setHTML(e.getDescription().replaceAll("<br>", " "));
				String text = h.getText();
				if (text.length() > 100) {
					text = text.substring(0, 101) + "...";
				}
				ep.add(new Label(text));

				vp.add(ep);
			}

			if (events.isEmpty()) {
				vp.add(new Label("None right now."));
			} else {
				String url = PageUrl.eventList();
				if (community) {
					url += "&showCommunity=true";
				}
				vp.add(new Hyperlink("See more events...", url));
			}

			initWidget(vp);
		}
	}

	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private Grid grid = new Grid(1, 3);
	private SimplePanel articlePanel = new SimplePanel();
	private SimplePanel eventPanel = new SimplePanel();

	private SimplePanel communityPanel = new SimplePanel();

	public HomePage(VerticalPanel p) {
		page = p;
		grid.setWidget(0, 0, communityPanel);
		grid.setWidget(0, 1, articlePanel);
		grid.setWidget(0, 2, eventPanel);
		grid.getCellFormatter().setWidth(0, 0, "250px");
		grid.getCellFormatter().setWidth(0, 2, "250px");
		grid.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor("#d8e6f7");
		grid.getCellFormatter().getElement(0, 2).getStyle().setBackgroundColor("#ddf3da");
		grid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		grid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		grid.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		page.add(grid);

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
				vp.add(new EventModulePanel("Upcoming Events", result));

				VerticalPanel vvp = new VerticalPanel();
				vvp.setSpacing(8);
				// Hyperlink link = new Hyperlink("See more events...", PageUrl.eventList());
				// vvp.add(link);

				Image image = new Image(MainImageBundle.INSTANCE.faceBook());
				String text = "<p><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" target=\"_TOP\" ";
				text += "style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; color: #3B5998; text-decoration: none;\" ";
				text += "title=\"WeAre Home Educators\">WeAre Home Educators</a><br><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" title=\"WeAre Home Educators\">";
				text += image.toString() + "</a></p>";

				HTML fb = new HTML(text);
				vvp.add(fb);
				vp.add(vvp);

				eventPanel.setWidget(vp);
			}
		});

		args.put(EventArg.SHOW_COMMUNITY);

		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				VerticalPanel vp = new VerticalPanel();
				vp.add(new EventModulePanel("Community Events", result));

				communityPanel.setWidget(vp);
			}
		});

	}
}
