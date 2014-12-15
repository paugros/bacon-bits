package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.AdTile;
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
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HomePage implements Page {
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private VerticalPanel page = new VerticalPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
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
		centerPanel.setWidth("100%");
		page.add(centerPanel);

		eventService.getHomePageData(new Callback<HomePageData>() {
			@Override
			protected void doOnSuccess(HomePageData result) {
				pageData = result;

				Grid g = new Grid(3, 3);
				g.setCellSpacing(10);

				TileConfig rc = new TileConfig().setTagType(TagMappingType.RESOURCE).setCount(pageData.getResourceCount());
				rc.setImage(new Image(MainImageBundle.INSTANCE.resourceTile()));
				g.setWidget(0, 0, new Tile(rc));

				TileConfig ec = new TileConfig().setTagType(TagMappingType.EVENT).setCount(pageData.getEventCount());
				ec.setImage(new Image(MainImageBundle.INSTANCE.eventTile()));
				g.setWidget(0, 1, new Tile(ec));

				g.setWidget(0, 2, new AdTile(pageData.getAds().get(0)));

				TileConfig uc = new TileConfig().setTagType(TagMappingType.USER).setCount(pageData.getUserCount());
				uc.setImage(new Image(MainImageBundle.INSTANCE.userTile()));
				g.setWidget(1, 0, new Tile(uc));

				TileConfig bbc = new TileConfig().setTagType(TagMappingType.ARTICLE).setText("Blog");
				bbc.setImage(new Image(MainImageBundle.INSTANCE.blogTile())).setUrl(PageUrl.blog(0));
				g.setWidget(1, 1, new Tile(bbc));

				g.setWidget(1, 2, new AdTile(pageData.getAds().get(1)));

				TileConfig bc = new TileConfig().setTagType(TagMappingType.BOOK).setCount(pageData.getBookCount());
				bc.setImage(new Image(MainImageBundle.INSTANCE.bookTile()));
				g.setWidget(2, 0, new Tile(bc));

				TileConfig ac = new TileConfig().setTagType(TagMappingType.ARTICLE).setCount(pageData.getArticleCount());
				ac.setImage(new Image(MainImageBundle.INSTANCE.articleTile()));
				g.setWidget(2, 1, new Tile(ac));

				g.setWidget(2, 2, new AdTile(pageData.getAds().get(2)));

				centerPanel.add(g);
				// centerPanel.setCellHorizontalAlignment(g, HasHorizontalAlignment.ALIGN_CENTER);

				Application.getLayout().setPage("Home", page);
			}
		});

	}

	@SuppressWarnings("unused")
	private Widget createRaffleWidget() {
		// rafflecopter
		String html = "<div id=raffle><a class=\"rafl\" href=\"http://www.rafflecopter.com/rafl/display/797bf8a71/\" id=\"rc-797bf8a71\" rel=\"nofollow\">a Rafflecopter giveaway</a></div>";

		HTML raffle = new HTML(html);
		raffle.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							Element head = Document.get().getElementById("raffle");
							ScriptElement sce = Document.get().createScriptElement();
							sce.setType("text/javascript");
							sce.setSrc("//widget.rafflecopter.com/load.js");
							head.appendChild(sce);
						}
					});
				}
			}
		});

		return raffle;
	}

	private boolean signedUpForBooks() {
		if (!Application.isAuthenticated()) {
			return false;
		}

		return Application.getCurrentUser().memberOfAny(Constants.ONLINE_BOOK_SELLERS_GROUP_ID, Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID);
	}
}
