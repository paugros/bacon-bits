package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.content.event.BalanceBox;
import com.areahomeschoolers.baconbits.client.content.user.CreateUserDialog;
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
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
			label.addStyleName("homePageModuleTitle");
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

	private class HomeContentPanel extends Composite {
		private HorizontalPanel hp = new HorizontalPanel();
		private VerticalPanel lp = new VerticalPanel();
		private VerticalPanel rp = new VerticalPanel();

		public HomeContentPanel() {
			hp.add(lp);
			hp.add(rp);
			initWidget(hp);
		}

		public void add(Widget w) {
			Style style = w.getElement().getStyle();

			if (lp.getOffsetHeight() > rp.getOffsetHeight()) {
				rp.add(w);
			} else {
				lp.add(w);
				style.setMarginRight(10, Unit.PX);
			}

			style.setMarginBottom(10, Unit.PX);
			style.setWidth(250, Unit.PX);
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
		rightPanel.add(eventPanel);
		centerPanel.setSpacing(10);
		grid.setWidget(0, 0, communityPanel);
		grid.setWidget(0, 1, centerPanel);
		grid.setWidget(0, 2, rightPanel);
		grid.getCellFormatter().setWidth(0, 0, "250px");
		grid.getCellFormatter().setWidth(0, 2, "250px");
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
				uvp.add(new EventModulePanel("UPCOMING EVENTS", pageData.getUpcomingEvents(), null));
				uvp.addStyleName("homePageModule homePageEventPanel");

				VerticalPanel vvp = new VerticalPanel();
				vvp.setSpacing(8);

				// Image image = new Image(MainImageBundle.INSTANCE.faceBook());
				// String text = "<p><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" target=\"_TOP\" ";
				// text +=
				// "style=\"font-family: &quot;lucida grande&quot;,tahoma,verdana,arial,sans-serif; font-size: 11px; color: #3B5998; text-decoration: none;\" ";
				// text +=
				// "title=\"WeAre Home Educators\">WeAre Home Educators</a><br><a href=\"http://www.facebook.com/pages/WeAre-Home-Educators/111756708899702\" title=\"WeAre Home Educators\">";
				// text += image.toString() + "</a></p>";
				//
				// HTML fb = new HTML(text);
				// vvp.add(fb);
				uvp.add(vvp);

				eventPanel.setWidget(uvp);

				// community
				VerticalPanel cvp = new VerticalPanel();
				cvp.add(new EventModulePanel("COMMUNITY EVENTS", pageData.getCommunityEvents(), "&showCommunity=true"));
				cvp.addStyleName("homePageModule homePageEventPanel");

				// String ftext =
				// "<iframe src=\"http://wms.assoc-amazon.com/20070822/US/html/searchbox_20.html?t=httpwhediment-20\" width=\"120\" height=\"90\" frameborder=\"0\" scrolling=\"no\"></iframe>";
				// SimplePanel sp = new SimplePanel(new HTML(ftext));
				// sp.addStyleName("mediumPadding");
				// cvp.add(sp);

				communityPanel.setWidget(cvp);

				HomeContentPanel hcp = new HomeContentPanel();
				centerPanel.add(hcp);
				Application.getLayout().setPage("Home", page);

				if (Application.isAuthenticated()) {
					VerticalPanel vp = new VerticalPanel();
					vp.setWidth("100%");
					vp.setSpacing(8);
					Hyperlink h = new Hyperlink("Find People", PageUrl.userList());
					h.addStyleName("homePageModuleTitle");

					PaddedPanel pp = new PaddedPanel();
					pp.add(h);
					Label l = new Label("NEW!");
					l.addStyleName("errorText bold smallText");
					pp.add(l);

					vp.add(pp);
					Hyperlink priv = new Hyperlink("privacy preferences", PageUrl.user(Application.getCurrentUserId()) + "&tab=7");
					Hyperlink prof = new Hyperlink("profile page", PageUrl.user(Application.getCurrentUserId()));
					String t = "Click above to search our directory of homeschoolers. Find people in your area, with kids the same age as your own, who share your interests.<br><br>";
					t += "Visit your " + prof.toString() + " to add your interests.<br><br>";
					t += "Visit your " + priv.toString() + " to adjust what information you share with other people.";
					vp.add(new HTML(t));

					hcp.add(vp);
				}

				// new
				if (!Common.isNullOrEmpty(pageData.getNewlyAddedEvents())) {
					VerticalPanel nvp = new VerticalPanel();
					nvp.addStyleName("homePageModule");
					nvp.setSpacing(8);

					Label label = new Label("NEWLY ADDED EVENTS");
					label.addStyleName("homePageModuleTitle");
					nvp.add(label);

					for (Event e : pageData.getNewlyAddedEvents()) {
						VerticalPanel mhp = new VerticalPanel();

						Hyperlink link = new Hyperlink(e.getTitle(), PageUrl.event(e.getId()));
						link.addStyleName("mediumText");
						mhp.add(link);

						HTML date = new HTML(Formatter.formatDateTime(e.getStartDate()));
						date.setWordWrap(false);
						// date.getElement().getStyle().setColor("#555555");
						date.addStyleName("italic");
						mhp.add(date);

						nvp.add(mhp);
					}

					if (pageData.getNewlyAddedEvents().size() == 5) {
						String url = PageUrl.eventList() + "&newlyAdded=true";
						nvp.add(new Hyperlink("See more...", url));
					}

					hcp.add(nvp);
				}

				if (!Application.memberOf(16)) {
					VerticalPanel vp = new VerticalPanel();
					vp.setWidth("100%");
					vp.setSpacing(8);

					Label label = new Label("Sell Your Books");
					label.addStyleName("homePageModuleTitle");
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

					String sellText = "You can sell your used homeschool curriculum with us. Click the payment button ($5.00) to sign up as a book seller and begin listing your items.";
					vp.add(new Label(sellText));
					vp.add(payButton);

					hcp.add(vp);
				}

				if (Application.isAuthenticated()) {
					// HorizontalPanel hp = new PaddedPanel(10);

					// my events
					if (!Common.isNullOrEmpty(pageData.getMyUpcomingEvents())) {
						VerticalPanel vp = new VerticalPanel();
						vp.setWidth("250px");
						vp.addStyleName("homePageModule");
						vp.setSpacing(8);

						Label title = new Label("MY UPCOMING EVENTS");
						title.addStyleName("homePageModuleTitle");
						vp.add(title);

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

						vp.add(new Hyperlink("See more...", PageUrl.user(Application.getCurrentUserId()) + "&tab=1"));
						hcp.add(vp);
					}

				}

				// links
				VerticalPanel lp = new VerticalPanel();
				Label linkLabel = new Label("LINKS");
				linkLabel.addStyleName("homePageModuleTitle");
				linkLabel.getElement().getStyle().setMarginBottom(4, Unit.PX);
				lp.add(linkLabel);

				if (Application.isAuthenticated()) {
					lp.add(new Hyperlink("Find People", PageUrl.userList()));
				} else {
					ClickLabel cl = new ClickLabel("Create an Account", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							new CreateUserDialog().center(new User());
						}
					});
					cl.addStyleName("bold");
					lp.add(cl);
				}
				lp.add(new Hyperlink("Event Listing", PageUrl.eventList()));
				lp.add(new Hyperlink("Event Calendar", PageUrl.eventCalendar()));
				lp.add(new Hyperlink("Book Store", PageUrl.bookSearch()));

				if (Application.isAuthenticated()) {
					lp.add(new Hyperlink("My Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1"));
					if (Application.getCurrentUser().memberOfAny(16, 17)) {
						lp.add(new Hyperlink("My Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=4"));
					}
					lp.add(new Hyperlink("My Profile", PageUrl.user(Application.getCurrentUserId())));
					if (!Application.getCurrentUser().isChild()) {
						lp.add(new Hyperlink("My Shopping Cart", PageUrl.payment()));
					}
				}

				VerticalPanel rp = new VerticalPanel();
				rp.setSpacing(8);

				rp.add(lp);

				if (Application.isAuthenticated()) {
					BalanceBox bb = new BalanceBox();
					bb.populate();
					rp.add(bb);
					rp.setCellVerticalAlignment(bb, HasVerticalAlignment.ALIGN_BOTTOM);
				}

				hcp.add(rp);
				// introduction
				centerPanel.add(new ArticleWidget(pageData.getIntro()));

				if (Application.isCitrus()) {
					centerPanel.add(createGroupsTable());
				}
			}
		});

	}

	private Widget createGroupsTable() {
		GenericCellTable groupsTable = new GenericCellTable() {
			@Override
			protected void fetchData() {

			}

			@Override
			protected void setColumns() {
				addCompositeWidgetColumn("Group Name", new WidgetCellCreator<Data>() {
					@Override
					protected Widget createWidget(Data item) {
						String url = "http://www." + item.get("orgDomain");
						if (Constants.CG_DOMAIN.equals(item.get("orgDomain"))) {
							url = "http://" + item.get("orgSubDomain") + "." + item.get("orgDomain");
						}
						Anchor link = new Anchor(item.get("groupName"), url);
						return link;
					}
				});

				addTextColumn("Description", new ValueGetter<String, Data>() {
					@Override
					public String get(Data item) {
						return item.get("description");
					}
				});

				addNumberColumn("Members", new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getInt("memberCount");
					}
				}).setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}
		};

		groupsTable.populate(pageData.getGroups());
		groupsTable.disablePaging();
		groupsTable.setTitle("Our Groups");

		return WidgetFactory.newSection(groupsTable, ContentWidth.MAXWIDTH750PX);
	}
}
