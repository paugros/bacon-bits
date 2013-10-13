package com.areahomeschoolers.baconbits.client.content.home;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.content.event.BalanceBox;
import com.areahomeschoolers.baconbits.client.content.minimodules.CitrusMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.CommunityEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.FindPeopleMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.LinksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.MyEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.SellBooksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.UpcomingEventsMiniModule;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.SidebarPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;

import com.google.gwt.dom.client.Style;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HomePage implements Page {

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

	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private Grid grid = new Grid(1, 3);
	private VerticalPanel leftPanel = new SidebarPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	private VerticalPanel rightPanel = new SidebarPanel();
	private HomePageData pageData;

	public HomePage(VerticalPanel p) {
		page = p;

		if ("return".equals(Url.getParameter("ps"))) {
			Application.refreshSecurityGroups(new Command() {
				@Override
				public void execute() {
					if (Application.memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
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

		centerPanel.setSpacing(10);
		grid.setWidget(0, 0, leftPanel);
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
				rightPanel.add(new UpcomingEventsMiniModule(pageData.getUpcomingEvents()));

				leftPanel.add(new CitrusMiniModule());
				// community
				leftPanel.add(new CommunityEventsMiniModule(pageData.getCommunityEvents()));

				HomeContentPanel hcp = new HomeContentPanel();
				centerPanel.add(hcp);
				Application.getLayout().setPage("Home", page);

				if (Application.isAuthenticated()) {
					hcp.add(new FindPeopleMiniModule());
				}

				// new
				if (!Common.isNullOrEmpty(pageData.getNewlyAddedEvents())) {
					hcp.add(new NewEventsMiniModule(pageData.getNewlyAddedEvents()));
				}

				if (!Application.memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
					hcp.add(new SellBooksMiniModule());
				}

				if (Application.isAuthenticated()) {
					// my events
					if (!Common.isNullOrEmpty(pageData.getMyUpcomingEvents())) {
						hcp.add(new MyEventsMiniModule(pageData.getMyUpcomingEvents()));
					}
				}

				// links
				VerticalPanel rp = new VerticalPanel();
				rp.setSpacing(8);

				rp.add(new LinksMiniModule());

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
