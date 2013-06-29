package com.areahomeschoolers.baconbits.client.content;

import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
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
		// prevents auto-selection bug
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

		addDynamicItems(Application.getApplicationData().getDynamicMenuItems(), this);
		addItem("Events", getEventsMenu());
		addItem("Book Store", getBooksMenu());
		if (Application.isAuthenticated() && Application.isSystemAdministrator()) {
			addItem("People", getPeopleMenu());
		}
		if (Application.isAuthenticated()) {
			addItem("My Items", getMyItemsMenu());
		}
		if (Application.isSystemAdministrator()) {
			addItem("Admin", getAdminMenu());
		}

	}

	@Override
	public MenuItem addItem(String text, final MenuBar popup) {
		popup.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				popup.getParent().getParent().getElement().getStyle().setMarginTop(11, Unit.PX);
			}
		});
		return super.addItem(text, popup);
	}

	private void addDynamicItems(List<MainMenuItem> items, MenuBar parent) {
		for (MainMenuItem item : items) {
			if (item.hasChildren()) {
				final MenuBar menu = new MenuBar(true);
				addDynamicItems(item.getItems(), menu);
				parent.addItem(item.getName(), menu);
			} else {
				String url = "";
				if (item.getArticleIds() != null) {
					url = PageUrl.articleGroup(item.getArticleIds());
				} else {
					url = item.getUrl();
				}
				addLinkToMenu(parent, item.getName(), url);
			}
		}
	}

	private MenuBar getAdminMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "Add Article", PageUrl.article(0));
		addLinkToMenu(menu, "Add User", PageUrl.user(0));
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