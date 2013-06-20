package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.content.event.BalanceBox;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.Pair;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomePage implements Page {
	private class EventModulePanel extends Composite {

		public EventModulePanel(String title, ArrayList<Event> events, String extraParams) {
			if (extraParams == null) {
				extraParams = "";
			}
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			vp.setSpacing(8);

			Label label = new Label(title);
			label.addStyleName("hugeText");
			vp.add(label);

			for (Event e : events) {
				VerticalPanel ep = new VerticalPanel();
				Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
				link.addStyleName("mediumText");

				ep.add(link);

				String subText = Formatter.formatDateTime(e.getStartDate());
				if (e.getCategoryId() == 6) {
					subText += " - ";
					if (e.getPrice() > 0) {
						subText += Formatter.formatCurrency(e.getPrice());
					} else {
						subText += "Free";
					}
				}
				Label date = new Label(subText);
				date.addStyleName("italic");
				ep.add(date);

				HTML h = new HTML();
				h.setHTML(e.getDescription().replaceAll("<br>", " "));
				String text = h.getText().trim();
				if (text.length() > 100) {
					text = text.substring(0, 101) + "...";
				}
				ep.add(new Label(text));

				vp.add(ep);
			}

			if (events.isEmpty()) {
				vp.add(new Label("None right now."));
			} else {
				String url = PageUrl.eventList() + extraParams;
				vp.add(new Hyperlink("See more events...", url));
			}

			initWidget(vp);
		}
	}

	private final BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private Grid grid = new Grid(1, 3);
	private VerticalPanel centerPanel = new VerticalPanel();
	private SimplePanel eventPanel = new SimplePanel();
	private HomePageData pageData;

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
						text += "<br><br><b>NOTE: You may need to log out and log back in before you can see the My Books menu option.</b>";
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
							if (result.getAuthorizationUrl() != null) {
								Window.Location.replace(result.getAuthorizationUrl());
							} else {
								HistoryToken.set(PageUrl.home() + "&ps=return");
							}
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
		centerPanel.setSpacing(10);
		grid.setWidget(0, 0, communityPanel);
		grid.setWidget(0, 1, centerPanel);
		grid.setWidget(0, 2, rightPanel);
		grid.getCellFormatter().setWidth(0, 0, "250px");
		grid.getCellFormatter().setWidth(0, 2, "250px");
		// grid.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor("#d8e6f7");
		// grid.getCellFormatter().getElement(0, 2).getStyle().setBackgroundColor("#ddf3da");
		grid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		grid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		grid.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		page.add(grid);

		eventService.getHomePageData(new Callback<HomePageData>() {
			@Override
			protected void doOnSuccess(HomePageData result) {
				pageData = result;

				// upcoming
				VerticalPanel uvp = new VerticalPanel();
				uvp.add(new EventModulePanel("Upcoming Events", pageData.getUpcomingEvents(), null));
				uvp.getElement().getStyle().setBackgroundColor("#ddf3da");
				uvp.addStyleName("roundedCorners");

				VerticalPanel vvp = new VerticalPanel();
				vvp.setSpacing(8);

				Image image = new Image(MainImageBundle.INSTANCE.faceBook());
				String text = "<p><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" target=\"_TOP\" ";
				text += "style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; color: #3B5998; text-decoration: none;\" ";
				text += "title=\"WeAre Home Educators\">WeAre Home Educators</a><br><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" title=\"WeAre Home Educators\">";
				text += image.toString() + "</a></p>";

				HTML fb = new HTML(text);
				vvp.add(fb);
				uvp.add(vvp);

				eventPanel.setWidget(uvp);

				// community
				VerticalPanel cvp = new VerticalPanel();
				cvp.add(new EventModulePanel("Community Events", pageData.getCommunityEvents(), "&showCommunity=true"));
				cvp.getElement().getStyle().setBackgroundColor("#d8e6f7");
				cvp.addStyleName("roundedCorners");

				String ftext = "<iframe src=\"http://wms.assoc-amazon.com/20070822/US/html/searchbox_20.html?t=httpwhediment-20\" width=\"120\" height=\"90\" frameborder=\"0\" scrolling=\"no\"></iframe>";
				SimplePanel sp = new SimplePanel(new HTML(ftext));
				sp.addStyleName("mediumPadding");
				cvp.add(sp);
				communityPanel.setWidget(cvp);

				// new
				if (!Common.isNullOrEmpty(pageData.getNewlyAddedEvents())) {
					VerticalPanel nvp = new VerticalPanel();
					nvp.getElement().getStyle().setBackgroundColor("#ffe8eb");
					nvp.addStyleName("roundedCorners");
					nvp.setSpacing(8);

					Label label = new Label("Newly Added Events");
					label.addStyleName("largeText");
					nvp.add(label);

					for (Event e : pageData.getNewlyAddedEvents()) {
						HTML h = new HTML();
						Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));

						String ntext = link.toString() + "&nbsp;-&nbsp;";
						ntext += "<i>" + Formatter.formatDateTime(e.getStartDate()) + "</i><br>";

						h.setHTML(e.getDescription().replaceAll("<br>", " "));
						String d = h.getText();
						if (d.length() > 100) {
							d = d.substring(0, 101) + "...";
						}
						ntext += d;

						h.setHTML(ntext);
						nvp.add(h);
					}

					if (pageData.getNewlyAddedEvents().size() == 5) {
						String url = PageUrl.eventList() + "&newlyAdded=true";
						nvp.add(new Hyperlink("See more...", url));
					}

					centerPanel.add(nvp);
				}

				if (Application.isAuthenticated()) {
					HorizontalPanel hp = new PaddedPanel(10);

					// my events
					if (!Common.isNullOrEmpty(pageData.getMyUpcomingEvents())) {
						VerticalPanel vp = new VerticalPanel();
						vp.setWidth("250px");
						vp.addStyleName("dottedBorder");
						vp.getElement().getStyle().setBackgroundColor("#EEEEEE");
						vp.setSpacing(8);

						Hyperlink titleLink = new Hyperlink(".:: My Upcoming Events ::.", PageUrl.user(Application.getCurrentUserId()) + "&tab=1");
						titleLink.addStyleName("largeText nowrap");
						vp.add(titleLink);

						for (Event e : pageData.getMyUpcomingEvents()) {
							VerticalPanel mhp = new VerticalPanel();

							Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
							link.addStyleName("mediumText");
							mhp.add(link);

							HTML date = new HTML(Formatter.formatDateTime(e.getStartDate()));
							date.setWordWrap(false);
							// date.getElement().getStyle().setColor("#555555");
							date.addStyleName("italic");
							mhp.add(date);

							vp.add(mhp);
						}

						hp.add(vp);
					}

					// links
					List<Pair<String, String>> links = new ArrayList<Pair<String, String>>();

					links.add(new Pair<String, String>("Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1"));
					if (Application.getCurrentUser().memberOfAny(16, 17)) {
						links.add(new Pair<String, String>("Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=5"));
					}
					links.add(new Pair<String, String>("Profile", PageUrl.user(Application.getCurrentUserId())));
					if (!Application.getCurrentUser().isChild()) {
						links.add(new Pair<String, String>("Shopping Cart", PageUrl.payment()));
					}
					if (!Application.getCurrentUser().isChild()) {
						links.add(new Pair<String, String>("Family", PageUrl.user(Application.getCurrentUserId()) + "&tab=4"));
					}
					links.add(new Pair<String, String>("Volunteer Positions", PageUrl.user(Application.getCurrentUserId()) + "&tab=2"));
					if (!Application.getCurrentUser().isChild()) {
						links.add(new Pair<String, String>("Payments", PageUrl.user(Application.getCurrentUserId()) + "&tab=6"));
					}
					links.add(new Pair<String, String>("Calendar", PageUrl.user(Application.getCurrentUserId()) + "&tab=7"));

					VerticalPanel lp = new VerticalPanel();
					Label linkLabel = new Label("Links");
					linkLabel.addStyleName("largeText");
					linkLabel.getElement().getStyle().setMarginBottom(4, Unit.PX);
					lp.add(linkLabel);

					for (Pair<String, String> item : links) {
						Hyperlink link = new Hyperlink(item.getLeft(), item.getRight());
						lp.add(link);
					}

					VerticalPanel rp = new VerticalPanel();
					rp.setSpacing(8);

					rp.add(lp);

					BalanceBox bb = new BalanceBox();
					bb.populate();
					rp.add(bb);
					rp.setCellVerticalAlignment(bb, HasVerticalAlignment.ALIGN_BOTTOM);

					hp.add(rp);

					centerPanel.add(hp);
				}
				// introduction
				centerPanel.add(new ArticleWidget(pageData.getIntro()));

				Application.getLayout().setPage("Home", page);
			}
		});

	}

}
