package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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
	private final BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private Grid grid = new Grid(1, 3);
	private SimplePanel articlePanel = new SimplePanel();
	private SimplePanel eventPanel = new SimplePanel();

	private SimplePanel communityPanel = new SimplePanel();
	private boolean paying = false;

	public HomePage(VerticalPanel p) {
		page = p;

		if ("return".equals(Url.getParameter("ps"))) {
			Application.refreshSecurityGroups(new Command() {
				@Override
				public void execute() {
					if (Application.memberOf(16)) {
						String text = "Thank you for registering to sell books with us.<br><br>You can now begin loading your books into the system using the <b>Book Store -> My Books</b> meu option.";
						HTML label = new HTML(text);
						label.setWidth("300px");
						AlertDialog dialog = new AlertDialog("Thanks!", label);
						dialog.getButton().addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								HistoryToken.set(PageUrl.home(), false);
								Window.Location.reload();
							}
						});

						dialog.center();
					}
				}
			});
		}

		VerticalPanel rightPanel = new VerticalPanel();
		rightPanel.setSpacing(0);
		if (!Application.memberOf(16)) {
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			vp.setSpacing(8);

			Label label = new Label("Sell Your Books");
			label.addStyleName("hugeText");
			vp.add(label);
			Image payButton = new Image(MainImageBundle.INSTANCE.paypalButton());
			payButton.getElement().getStyle().setCursor(Cursor.POINTER);
			payButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!Application.isAuthenticated()) {
						LoginDialog.showLogin();
						return;
					}

					if (paying) {
						return;
					}

					paying = true;

					bookService.signUpToSell(new Callback<PaypalData>() {
						@Override
						protected void doOnFailure(Throwable caught) {
							super.doOnFailure(caught);
							paying = false;
						}

						@Override
						protected void doOnSuccess(PaypalData result) {
							Window.Location.replace(result.getAuthorizationUrl());
						}
					});
				}
			});

			String text = "You can sell your used homeschool curriculum with us. Click the payment button ($5.00) to sign up as a book seller and begin listing your items.";
			vp.add(new Label(text));
			vp.add(payButton);

			rightPanel.add(vp);
		}

		rightPanel.add(eventPanel);
		grid.setWidget(0, 0, communityPanel);
		grid.setWidget(0, 1, articlePanel);
		grid.setWidget(0, 2, rightPanel);
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

				String text = "<iframe src=\"http://wms.assoc-amazon.com/20070822/US/html/searchbox_20.html?t=httpwhediment-20\" width=\"120\" height=\"90\" frameborder=\"0\" scrolling=\"no\"></iframe>";
				SimplePanel sp = new SimplePanel(new HTML(text));
				sp.addStyleName("mediumPadding");
				vp.add(sp);
				communityPanel.setWidget(vp);
			}
		});

	}
}
