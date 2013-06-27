package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public final class MainMenu extends MenuBar {
	public static void addLinkToMenu(MenuBar mb, String name, String url) {
		String item;

		if (isExternalUrl(url)) {
			item = "<a href=\"" + url + "\" target=\"_blank\" class=menuLink>" + name + "</a>";
		} else {
			item = "<a href=\"" + Url.getGwtCodeServerAsQueryString() + "#" + url + "\" class=menuLink>" + name + "</a>";
		}
		MenuItem mi = mb.addItem(item, true, new Command() {
			@Override
			public void execute() {
			}
		});

		mi.getElement().getStyle().setPadding(0, Unit.PX);
	}

	private static native boolean isExternalUrl(String url) /*-{
		var pattern = /(http|https|ftp|telnet):\/\//;
		return pattern.test(url);
	}-*/;

	private final User user = Application.getCurrentUser();

	public MainMenu() {
		setFocusOnHoverEnabled(false);
		addStyleName("MainMenu");
		addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						handleClose();
					}
				});
			}
		});

		addItem("About", getHomeMenu());
		addItem("Events", getEventsMenu());
		addItem("Classes", getClassesMenu());
		addItem("Book Store", getBooksMenu());
		addItem("Resources", getResourcesMenu());
		if (Application.isAuthenticated()) {
			addItem("People", getPeopleMenu());
		}
		if (Application.isAuthenticated()) {
			addItem("My Items", getMyItemsMenu());
		}
		if (Application.isSystemAdministrator()) {
			addItem("Admin", getAdminMenu());
		}

	}

	private MenuBar getAdminMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "Add Article", PageUrl.article(0));
		addLinkToMenu(menu, "Add User", PageUrl.user(0));
		addLinkToMenu(menu, "List Users", PageUrl.userList());
		addLinkToMenu(menu, "List Groups", PageUrl.userGroupList());

		menu.addItem("Expire Session", new Command() {
			@Override
			public void execute() {
				LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
				loginService.logout(new Callback<Void>(false) {
					@Override
					protected void doOnSuccess(Void result) {
						GWT.runAsync(new RunAsyncCallback() {
							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess() {
								AlertDialog.alert("Session Expiration", new Label("Success."));
							}
						});
					}
				});
			}
		});

		menu.addItem("Reload Page", new Command() {
			@Override
			public void execute() {
				Application.reloadPage();
			}
		});

		return menu;
	}

	private MenuBar getBooksMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Search Books", PageUrl.bookSearch());
		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			addLinkToMenu(menu, "Book Seller Summary", PageUrl.bookManagement());
		}

		addLinkToMenu(menu, "Book Seller Instructions", PageUrl.article(65));

		if (Application.administratorOf(17)) {
			addLinkToMenu(menu, "Create Book Receipt", PageUrl.bookReceipt());
		}

		return menu;
	}

	private MenuBar getClassesMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Co-op", PageUrl.articleGroup("37,38"));
		addLinkToMenu(menu, "PE Activities", PageUrl.articleGroup("36"));
		addLinkToMenu(menu, "The Youth Booth", PageUrl.articleGroup("63"));

		return menu;
	}

	private MenuBar getEducationMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Arts/Crafts", PageUrl.articleGroup("26,23"));
		addLinkToMenu(menu, "Math", PageUrl.articleGroup("25"));
		addLinkToMenu(menu, "Preschool", PageUrl.articleGroup("5,23"));
		addLinkToMenu(menu, "Science", PageUrl.articleGroup("30"));
		addLinkToMenu(menu, "Seasonal/Holiday", PageUrl.articleGroup("18,19,20,21,22,29"));

		return menu;
	}

	private MenuBar getEventsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Event Listing", PageUrl.eventList());
		addLinkToMenu(menu, "Calendar", PageUrl.eventCalendar());

		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			addLinkToMenu(menu, "Add Event", PageUrl.event(0));
			addLinkToMenu(menu, "Registration Management", PageUrl.registrationManagement());
		}

		addLinkToMenu(menu, "Policies", PageUrl.articleGroup("57,56,58"));

		return menu;
	}

	private MenuBar getHomeMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Home", PageUrl.home());
		addLinkToMenu(menu, "Contact Us", PageUrl.articleGroup("48"));
		addLinkToMenu(menu, "FAQ", PageUrl.articleGroup("7"));

		return menu;
	}

	private MenuBar getMyItemsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Profile", PageUrl.user(Application.getCurrentUserId()));
		addLinkToMenu(menu, "Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1");
		addLinkToMenu(menu, "Volunteer Positions", PageUrl.user(Application.getCurrentUserId()) + "&tab=2");
		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "Family", PageUrl.user(Application.getCurrentUserId()) + "&tab=4");
		}
		if (Application.getCurrentUser().memberOfAny(16, 17)) {
			addLinkToMenu(menu, "Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=5");
		}
		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "Payments", PageUrl.user(Application.getCurrentUserId()) + "&tab=6");
		}
		addLinkToMenu(menu, "Calendar", PageUrl.user(Application.getCurrentUserId()) + "&tab=7");
		addLinkToMenu(menu, "Privacy", PageUrl.user(Application.getCurrentUserId()) + "&tab=8");

		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "Shopping Cart", PageUrl.payment());
		}

		menu.addSeparator();

		menu.addItem("Change password", new ScheduledCommand() {
			@Override
			public void execute() {
				GWT.runAsync(new RunAsyncCallback() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess() {
						new ResetPasswordDialog(true).center();
					}
				});
			}
		});

		menu.addItem("Log out", new ScheduledCommand() {
			@Override
			public void execute() {
				LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
				loginService.logout(new Callback<Void>(false) {
					@Override
					protected void doOnSuccess(Void result) {
						Window.Location.reload();
					}
				});
			}
		});

		return menu;
	}

	private MenuBar getPeopleMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Find People", PageUrl.userList());
		menu.addSeparator();
		addLinkToMenu(menu, "My Profile", PageUrl.user(Application.getCurrentUserId()));
		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "My Family", PageUrl.user(Application.getCurrentUserId()) + "&tab=4");
		}
		addLinkToMenu(menu, "Privacy Settings", PageUrl.user(Application.getCurrentUserId()) + "&tab=8");
		return menu;
	}

	private MenuBar getResourcesMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Homeschooling Books", PageUrl.articleGroup("43"));
		addLinkToMenu(menu, "Local Sports League Info", PageUrl.articleGroup("34"));

		menu.addSeparator();

		addLinkToMenu(menu, "Homeschooling Books", PageUrl.articleGroup("43"));
		addLinkToMenu(menu, "Homeschool Stories", PageUrl.articleGroup("15,16,14,13,12,11,10,9,8"));
		addLinkToMenu(menu, "Homeschooling Methods", PageUrl.articleGroup("40"));
		addLinkToMenu(menu, "Curriculum Providers", PageUrl.articleGroup("41"));
		addLinkToMenu(menu, "Parents' Support Meeting", PageUrl.articleGroup("33"));

		return menu;
	}

	private void handleClose() {
		MenuItem item = getSelectedItem();
		if (item == null) {
			return;
		}

		MenuBar subMenu = item.getSubMenu();
		if (subMenu == null) {
			return;
		}

		if (!subMenu.isAttached()) {
			selectItem(null);
		}
	}
}