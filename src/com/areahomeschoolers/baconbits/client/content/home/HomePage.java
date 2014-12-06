package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.ResourceTile;
import com.areahomeschoolers.baconbits.client.content.resource.Tile;
import com.areahomeschoolers.baconbits.client.content.resource.TileConfig;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	// private VerticalPanel leftPanel = new PaddedVerticalPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	// private VerticalPanel rightPanel = new PaddedVerticalPanel();
	private HomePageData pageData;

	public HomePage(VerticalPanel p) {
		page = p;

		if ("return".equals(Url.getParameter("ps"))) {
			Application.refreshSecurityGroups(new Command() {
				@Override
				public void execute() {
					if (signedUpForBooks()) {
						String text = "Thank you for registering to sell books with us.<br><br>You can now begin loading your books into the system using the <b>My Items -> Books</b> menu option.";
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

		// if (Url.getBooleanParameter("bookSaleSignup") && !signedUpForBooks()) {
		// new SellBooksMiniModule().showDialog();
		// }

		if (Url.getIntegerParameter("aagrp") > 0 && Application.isAuthenticated() && !Application.memberOf(Url.getIntegerParameter("aagrp"))) {
			final UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
			userService.listGroups(new ArgMap<UserGroupArg>(UserGroupArg.ID, Url.getIntegerParameter("aagrp")), new Callback<ArrayList<UserGroup>>(false) {
				@Override
				protected void doOnSuccess(ArrayList<UserGroup> result) {
					if (result.isEmpty()) {
						return;
					}

					UserGroup g = result.get(0);
					g.setUserApproved(true);
					g.setGroupApproved(true);

					userService.updateUserGroupRelation(Application.getCurrentUser(), g, true, new Callback<Void>(false) {
						@Override
						protected void doOnSuccess(Void result) {
						}
					});
				}
			});
		}

		centerPanel.setSpacing(10);
		// grid.setWidget(0, 0, leftPanel);
		grid.setWidget(0, 0, centerPanel);
		// grid.setWidget(0, 1, rightPanel);
		// grid.getCellFormatter().setWidth(0, 0, "250px");
		// grid.getCellFormatter().setWidth(0, 2, "250px");
		grid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		// grid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		// grid.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		page.add(grid);

		eventService.getHomePageData(new Callback<HomePageData>() {
			@Override
			protected void doOnSuccess(HomePageData result) {
				pageData = result;

				// citrus link
				// leftPanel.add(new CitrusMiniModule());

				// book promo
				// if (!Application.isAuthenticated() || !signedUpForBooks()) {
				// leftPanel.add(new SellBooksMiniModule());
				// }

				// community
				// leftPanel.add(new CommunityEventsMiniModule(pageData.getCommunityEvents()));

				// partner logos/links
				// HTML logos = new HTML(pageData.getPartners().getArticle());
				// leftPanel.add(logos);
				// leftPanel.setCellHorizontalAlignment(logos, HasHorizontalAlignment.ALIGN_CENTER);

				// ad
				// rightPanel.add(new AdsMiniModule());

				// links
				// rightPanel.add(new LinksMiniModule());

				// upcoming
				// rightPanel.add(new UpcomingEventsMiniModule(pageData.getUpcomingEvents()));

				// new users
				// if (Application.isAuthenticated()) {
				// rightPanel.add(new NewUsersMiniModule());
				// }

				// new books
				// rightPanel.add(new NewBooksMiniModule());

				// active users
				// if (Application.isAuthenticated()) {
				// rightPanel.add(new ActiveUsersMiniModule());
				// }

				// my events
				// if (Application.isAuthenticated()) {
				// if (!Common.isNullOrEmpty(pageData.getMyUpcomingEvents())) {
				// rightPanel.add(new MyEventsMiniModule(pageData.getMyUpcomingEvents()));
				// }
				// }

				// UserGroup org = Application.getCurrentOrg();

				Grid g = new Grid(3, 2);
				g.setCellSpacing(10);

				TileConfig rc = new TileConfig().setTagType(TagMappingType.RESOURCE).setCount(pageData.getResourceCount());
				rc.setImage(new Image(MainImageBundle.INSTANCE.resourceTile()));
				g.setWidget(0, 0, new Tile(rc));

				TileConfig ec = new TileConfig().setTagType(TagMappingType.EVENT).setCount(pageData.getEventCount());
				ec.setImage(new Image(MainImageBundle.INSTANCE.eventTile()));
				g.setWidget(0, 1, new Tile(ec));

				TileConfig uc = new TileConfig().setTagType(TagMappingType.USER).setCount(pageData.getUserCount());
				uc.setImage(new Image(MainImageBundle.INSTANCE.userTile()));
				g.setWidget(1, 0, new Tile(uc));

				TileConfig bbc = new TileConfig().setTagType(TagMappingType.ARTICLE).setText("Blog");
				bbc.setImage(new Image(MainImageBundle.INSTANCE.blogTile())).setUrl(PageUrl.blog(0));
				g.setWidget(1, 1, new Tile(bbc));

				TileConfig bc = new TileConfig().setTagType(TagMappingType.BOOK).setCount(pageData.getBookCount());
				bc.setImage(new Image(MainImageBundle.INSTANCE.bookTile()));
				g.setWidget(2, 0, new Tile(bc));

				TileConfig ac = new TileConfig().setTagType(TagMappingType.ARTICLE).setCount(pageData.getArticleCount());
				ac.setImage(new Image(MainImageBundle.INSTANCE.articleTile()));
				g.setWidget(2, 1, new Tile(ac));

				VerticalPanel adPanel = new VerticalPanel();
				adPanel.getElement().getStyle().setMarginTop(6, Unit.PX);
				for (Resource ad : pageData.getAds()) {
					ResourceTile tile = new ResourceTile(ad);

					tile.getElement().getStyle().setMarginBottom(10, Unit.PX);
					adPanel.add(tile);
				}

				HorizontalPanel hp = new HorizontalPanel();
				hp.add(g);
				hp.add(adPanel);

				centerPanel.add(hp);

				// if (Application.isAuthenticated() && Application.getCurrentUser().getGroups().get(org.getId()) == null) {
				// centerPanel.add(new RequestMembershipLink(org));
				// }

				// introduction
				// centerPanel.add(new ArticleWidget(pageData.getIntro()));

				// raffle
				// centerPanel.add(createRaffleWidget());

				// news
				// centerPanel.add(new NewsModule());

				// our groups
				// if (Application.isCitrus()) {
				// centerPanel.add(createGroupsTable());
				// }

				Application.getLayout().setPage("Home", page);
			}
		});

	}

	// private Widget createGroupsTable() {
	// GenericCellTable groupsTable = new GenericCellTable() {
	// @Override
	// protected void fetchData() {
	//
	// }
	//
	// @Override
	// protected void setColumns() {
	// addCompositeWidgetColumn("Group Name", new WidgetCellCreator<Data>() {
	// @Override
	// protected Widget createWidget(Data item) {
	// String url = "http://www." + item.get("orgDomain");
	// if (Constants.CG_DOMAIN.equals(item.get("orgDomain")) || "myhomeschoolgroups.com".equals(item.get("orgDomain"))) {
	// url = "http://" + item.get("orgSubDomain") + "." + item.get("orgDomain");
	// }
	// Anchor link = new Anchor(item.get("groupName"), url);
	// return link;
	// }
	// });
	//
	// addTextColumn("Description", new ValueGetter<String, Data>() {
	// @Override
	// public String get(Data item) {
	// return item.get("description");
	// }
	// });
	//
	// addNumberColumn("Members", new ValueGetter<Number, Data>() {
	// @Override
	// public Number get(Data item) {
	// return item.getInt("memberCount");
	// }
	// }).setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	// }
	// };
	//
	// groupsTable.populate(pageData.getGroups());
	// groupsTable.disablePaging();
	// groupsTable.setTitle("Our Groups");
	//
	// return WidgetFactory.newSection(groupsTable, ContentWidth.MAXWIDTH750PX);
	// }

	// private Widget createRaffleWidget() {
	// // rafflecopter
	// String html =
	// "<div id=raffle><a class=\"rafl\" href=\"http://www.rafflecopter.com/rafl/display/797bf8a71/\" id=\"rc-797bf8a71\" rel=\"nofollow\">a Rafflecopter giveaway</a></div>";
	//
	// HTML raffle = new HTML(html);
	// raffle.addAttachHandler(new Handler() {
	// @Override
	// public void onAttachOrDetach(AttachEvent event) {
	// if (event.isAttached()) {
	// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	// @Override
	// public void execute() {
	// Element head = Document.get().getElementById("raffle");
	// ScriptElement sce = Document.get().createScriptElement();
	// sce.setType("text/javascript");
	// sce.setSrc("//widget.rafflecopter.com/load.js");
	// head.appendChild(sce);
	// }
	// });
	// }
	// }
	// });
	//
	// return raffle;
	// }

	private boolean signedUpForBooks() {
		if (!Application.isAuthenticated()) {
			return false;
		}

		return Application.getCurrentUser().memberOfAny(Constants.ONLINE_BOOK_SELLERS_GROUP_ID, Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID);
	}
}
