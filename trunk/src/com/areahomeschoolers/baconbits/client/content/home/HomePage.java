package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.article.ArticleWidget;
import com.areahomeschoolers.baconbits.client.content.minimodules.SellBooksMiniModule;
import com.areahomeschoolers.baconbits.client.content.resource.AdTile;
import com.areahomeschoolers.baconbits.client.content.resource.Tile;
import com.areahomeschoolers.baconbits.client.content.resource.TileConfig;
import com.areahomeschoolers.baconbits.client.content.user.CreateUserDialog;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ImageSwitcher;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.RequestMembershipLink;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
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
			handlePaymentReturn();
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

		if (Application.isCitrus()) {
			VerticalPanel groupOffer = new VerticalPanel();
			groupOffer.setSpacing(10);
			groupOffer.addStyleName(ContentWidth.MAXWIDTH800PX.toString());
			String grpUrl = Constants.URL_SEPARATOR + PageUrl.userGroup(0);
			String grpTxt = "<div class=moduleTitle>Host Your Homeschool Group</div>";
			grpTxt += "Creating a free hosted homeschool group site takes less than a minute. ";
			grpTxt += "Your site will have its own member directory, forum, event registration system (including payment reciept), ";
			grpTxt += "curriculum marketplace, and access to hundreds of local homeschooling resources and events.";

			if (Application.isAuthenticated()) {
				grpTxt += "<div style=\"padding-top: 10px;\"><a href=\"" + grpUrl + "\" style=\"font-size: 16px;\">Try it out!</a></div>";
				groupOffer.add(new HTML(grpTxt));
			} else {

				ClickLabel login = new ClickLabel("sign in", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						LoginDialog.showLogin();
					}
				});

				ClickLabel createUser = new ClickLabel("create an account", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						CreateUserDialog dialog = new CreateUserDialog();
						dialog.center(new User());
					}
				});

				HorizontalPanel actionPanel = new HorizontalPanel();
				actionPanel.add(new HTML("Just&nbsp;"));
				actionPanel.add(login);
				actionPanel.add(new HTML("&nbsp;or&nbsp;"));
				actionPanel.add(createUser);
				actionPanel.add(new HTML(", then add your group."));
				groupOffer.add(new HTML(grpTxt));
				groupOffer.add(actionPanel);
			}

			page.add(groupOffer);

			page.getElement().getStyle().setZIndex(0);
			page.getElement().getStyle().setPosition(Position.RELATIVE);
			// begin slider test ground
			ImageSwitcher s = new ImageSwitcher();
			page.add(s);
		}

		Application.getLayout().setPage("Homeschooling Resources and Events, in Your Area", page);
		// end slider test ground

		page.add(centerPanel);

		eventService.getHomePageData(new Callback<HomePageData>() {
			@Override
			protected void doOnSuccess(HomePageData result) {
				pageData = result;

				if (!Application.isCitrus()) {
					UserGroup org = Application.getCurrentOrg();
					if (Application.isAuthenticated() && Application.getCurrentUser().getGroups().get(org.getId()) == null) {
						centerPanel.add(new RequestMembershipLink(org));
					}
					centerPanel.add(new ArticleWidget(pageData.getIntro()));
				}

				if (Application.hasLocation() && Application.isCitrus()) {
					String txt = "NOTE: The resource, event and community numbers below are only those ";
					if (Application.getCurrentLocation().length() == 2) {
						txt += "in " + Application.getCurrentLocation() + ". ";
					} else {
						txt += "within " + Constants.DEFAULT_SEARCH_RADIUS + " ";
						txt += "miles of " + Application.getCurrentLocation() + ". ";
					}
					txt += "You can change this setting on any ";
					txt += "<a href=\"" + Url.getBaseUrl() + PageUrl.tagGroup(TagType.RESOURCE.toString()) + "\">search page</a>.";
					HTML note = new HTML(txt);
					note.addStyleName(ContentWidth.MAXWIDTH700PX.toString());
					note.addStyleName("mediumPadding");
					note.getElement().getStyle().setFontSize(16, Unit.PX);
					centerPanel.add(note);
				}

				Grid g = new Grid(3, 3);
				g.setCellSpacing(45);

				TileConfig rc = new TileConfig().setTagType(TagType.RESOURCE).setCount(pageData.getResourceCount());
				rc.setImage(new Image(MainImageBundle.INSTANCE.resourceTile()));
				g.setWidget(0, 0, new Tile(rc));

				TileConfig ec = new TileConfig().setTagType(TagType.EVENT).setCount(pageData.getEventCount());
				ec.setImage(new Image(MainImageBundle.INSTANCE.eventTile()));
				g.setWidget(0, 1, new Tile(ec));

				g.setWidget(0, 2, getAdWidget(0));

				TileConfig uc = new TileConfig().setTagType(TagType.USER).setCount(pageData.getUserCount());
				uc.setImage(new Image(MainImageBundle.INSTANCE.userTile()));
				g.setWidget(1, 0, new Tile(uc));

				TileConfig bbc = new TileConfig().setTagType(TagType.ARTICLE).setText("Blog");
				bbc.setImage(new Image(MainImageBundle.INSTANCE.blogTile())).setUrl(PageUrl.blog());
				g.setWidget(1, 1, new Tile(bbc));

				g.setWidget(1, 2, getAdWidget(1));

				TileConfig bc = new TileConfig().setTagType(TagType.BOOK).setCount(pageData.getBookCount());
				bc.setImage(new Image(MainImageBundle.INSTANCE.bookTile()));
				g.setWidget(2, 0, new Tile(bc));

				TileConfig ac = new TileConfig().setTagType(TagType.ARTICLE).setCount(pageData.getArticleCount());
				ac.setImage(new Image(MainImageBundle.INSTANCE.articleTile()));
				g.setWidget(2, 1, new Tile(ac));

				g.setWidget(2, 2, getAdWidget(2));

				if (Application.isAuthenticated() && !signedUpForBooks()) {
					centerPanel.add(new SellBooksMiniModule());
				}

				centerPanel.add(g);
				// centerPanel.setCellHorizontalAlignment(g, HasHorizontalAlignment.ALIGN_CENTER);

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

	private Widget getAdWidget(int index) {
		if (pageData.getAds().size() > index) {
			return new AdTile(pageData.getAds().get(index));
		}

		return new Label();
	}

	private void handlePaymentReturn() {
		String paymentType = Url.getParameter("pt");

		if ("book".equals(paymentType)) {
			Application.refreshSecurityGroups(new Command() {
				@Override
				public void execute() {
					if (signedUpForBooks()) {
						String text = "Thank you for registering to sell books with us.<br><br>";
						text += "You can now begin loading your books into the system using the <b>My Books</b> menu option under your name in the upper right.";
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
		} else if ("event".equals(paymentType)) {
			String txt = "Thank you for registering for events through us. Check in regularly for new events and resources in your area!";
			Label text = new Label(txt);
			text.setWidth("350px");
			AlertDialog ad = new AlertDialog("Thanks!", text);
			ad.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					HistoryToken.set(PageUrl.home(), false);
				}
			});
			ad.center();
		}
	}

	private boolean signedUpForBooks() {
		if (!Application.isAuthenticated()) {
			return false;
		}

		return Application.getCurrentUser().memberOfAny(Constants.ONLINE_BOOK_SELLERS_GROUP_ID, Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID);
	}
}
