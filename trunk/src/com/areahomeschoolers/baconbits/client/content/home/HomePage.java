package com.areahomeschoolers.baconbits.client.content.home;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.content.minimodules.ActiveUsersMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.CitrusMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.CommunityEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.LinksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.MyEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewBooksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewUsersMiniModule;
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
import com.areahomeschoolers.baconbits.client.widgets.PaddedVerticalPanel;
import com.areahomeschoolers.baconbits.client.widgets.RequestMembershipLink;
import com.areahomeschoolers.baconbits.client.widgets.cellview.GenericCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HomePage implements Page {

	// private class HomeContentPanel extends Composite {
	// private HorizontalPanel hp = new HorizontalPanel();
	// private VerticalPanel lp = new VerticalPanel();
	// private VerticalPanel rp = new VerticalPanel();
	//
	// public HomeContentPanel() {
	// hp.add(lp);
	// hp.add(rp);
	// initWidget(hp);
	// }
	//
	// public void add(Widget w) {
	// Style style = w.getElement().getStyle();
	//
	// if (lp.getOffsetHeight() > rp.getOffsetHeight()) {
	// rp.add(w);
	// } else {
	// lp.add(w);
	// style.setMarginRight(10, Unit.PX);
	// }
	//
	// style.setMarginBottom(10, Unit.PX);
	// style.setWidth(250, Unit.PX);
	// }
	// }

	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private Grid grid = new Grid(1, 3);
	private VerticalPanel leftPanel = new PaddedVerticalPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	private VerticalPanel rightPanel = new PaddedVerticalPanel();
	private HomePageData pageData;

	public HomePage(VerticalPanel p) {
		page = p;

		if ("return".equals(Url.getParameter("ps"))) {
			Application.refreshSecurityGroups(new Command() {
				@Override
				public void execute() {
					if (Application.memberOf(Constants.ONLINE_BOOK_SELLERS_GROUP_ID)) {
						String text = "Thank you for registering to sell books with us.<br><br>You can now begin loading your books into the system using the <b>Book Store -> My Books</b> menu option.";
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

				// citrus link
				leftPanel.add(new CitrusMiniModule());

				// book promo
				if (!Application.isAuthenticated()
						|| !Application.getCurrentUser().memberOfAny(Constants.ONLINE_BOOK_SELLERS_GROUP_ID, Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID)) {
					leftPanel.add(new SellBooksMiniModule());
				}

				// community
				leftPanel.add(new CommunityEventsMiniModule(pageData.getCommunityEvents()));

				// partner logos/links
				HTML logos = new HTML(pageData.getPartners().getArticle());
				leftPanel.add(logos);
				leftPanel.setCellHorizontalAlignment(logos, HasHorizontalAlignment.ALIGN_CENTER);

				// links
				rightPanel.add(new LinksMiniModule());

				// upcoming
				rightPanel.add(new UpcomingEventsMiniModule(pageData.getUpcomingEvents()));

				// new users
				if (Application.isAuthenticated()) {
					rightPanel.add(new NewUsersMiniModule());
				}

				// new books
				rightPanel.add(new NewBooksMiniModule());

				// active users
				if (Application.isAuthenticated()) {
					rightPanel.add(new ActiveUsersMiniModule());
				}

				// my events
				if (Application.isAuthenticated()) {
					if (!Common.isNullOrEmpty(pageData.getMyUpcomingEvents())) {
						rightPanel.add(new MyEventsMiniModule(pageData.getMyUpcomingEvents()));
					}
				}

				UserGroup org = Application.getCurrentOrg();

				if (Application.isAuthenticated() && Application.getCurrentUser().getGroups().get(org.getId()) == null) {
					centerPanel.add(new RequestMembershipLink(org));
				}

				// introduction
				centerPanel.add(new ArticleWidget(pageData.getIntro()));

				// news
				centerPanel.add(new NewsModule());

				// our groups
				if (Application.isCitrus()) {
					centerPanel.add(createGroupsTable());
				}

				Application.getLayout().setPage("Home", page);
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
						if (Constants.CG_DOMAIN.equals(item.get("orgDomain")) || "myhomeschoolgroups.com".equals(item.get("orgDomain"))) {
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
