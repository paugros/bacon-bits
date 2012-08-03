package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public final class MainMenu extends MenuBar {
	public static void addLinkToMenu(MenuBar mb, String name, String url) {
		String item;

		if (isExternalUrl(url)) {
			item = "<a href=\"" + url + "\" target=\"_blank\" class=\"menuLink\">" + name + "</a>";
		} else {
			item = "<a href=\"" + Url.getGwtCodeServerAsQueryString() + "#" + url + "\" class=\"menuLink\">" + name + "</a>";
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
		addItem("Event Registration", getEventsMenu());
		addItem("WHE Classes/Activities", getClassesMenu());
		addItem("Educational Resources", getEducationMenu());
		addItem("Support", getSupportMenu());
		addItem("Resources", getResourcesMenu());
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

	private MenuBar getClassesMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Mixed Class Day", PageUrl.articleGroup("37,38"));
		addLinkToMenu(menu, "Parents' Support Meeting", PageUrl.articleGroup("33"));
		addLinkToMenu(menu, "PE Activities", PageUrl.articleGroup("36"));

		return menu;
	}

	private MenuBar getEducationMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Arts/Crafts", PageUrl.articleGroup("26,23"));
		// addLinkToMenu(menu, "Language Arts", PageUrl.articleGroup("26,23"));
		addLinkToMenu(menu, "Math", PageUrl.articleGroup("25"));
		addLinkToMenu(menu, "Preschool", PageUrl.articleGroup("5,23"));
		addLinkToMenu(menu, "Science", PageUrl.articleGroup("30"));
		addLinkToMenu(menu, "Seasonal/Holiday", PageUrl.articleGroup("18,19,20,21,22,29"));

		return menu;
	}

	private MenuBar getEventsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Events", PageUrl.eventList());
		if (Application.isAuthenticated()) {
			addLinkToMenu(menu, "My Event Registrations", PageUrl.eventParticipantList());
			addLinkToMenu(menu, "My Outstanding Event Balance", PageUrl.eventPayment());
		}
		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			addLinkToMenu(menu, "Add Event", PageUrl.event(0));
		}

		return menu;
	}

	private MenuBar getHomeMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Home", PageUrl.home());
		addLinkToMenu(menu, "Contact Us", PageUrl.articleGroup("48"));
		addLinkToMenu(menu, "FAQ", PageUrl.articleGroup("7"));

		return menu;
	}

	private MenuBar getResourcesMenu() {
		MenuBar menu = new MenuBar(true);
		// addLinkToMenu(menu, "Helpful Links", PageUrl.articleGroup("39"));
		addLinkToMenu(menu, "Homeschooling Books", PageUrl.articleGroup("43"));
		addLinkToMenu(menu, "Local Sports League Info", PageUrl.articleGroup("34"));

		return menu;
	}

	private MenuBar getSupportMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "Homeschooling Books", PageUrl.articleGroup("43"));
		addLinkToMenu(menu, "Homeschool Stories", PageUrl.articleGroup("15,16,14,13,12,11,10,9,8"));
		addLinkToMenu(menu, "Homeschooling Methods", PageUrl.articleGroup("40"));
		addLinkToMenu(menu, "Curriculum Providers", PageUrl.articleGroup("41"));
		addLinkToMenu(menu, "Managing Schedules", PageUrl.articleGroup("28,27"));
		addLinkToMenu(menu, "Reluctant Learners", PageUrl.articleGroup("24"));

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