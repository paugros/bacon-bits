package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.User;

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
import com.google.gwt.user.client.ui.MenuItemSeparator;
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

	MenuBar reports = new MenuBar(true);

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
		addItem("Events", getEventsMenu());
		addItem("Blog", getBlogMenu());
		addItem("All About Books!", getBooksMenu());
		addItem("Co-op Classes", getCoopMenu());
		addItem("Admin", getAdminMenu());
	}

	private MenuBar getAdminMenu() {
		MenuBar menu = new MenuBar(true);
		menu.addItem("Log in", new Command() {
			@Override
			public void execute() {
				LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
				final LoginDialog ld = new LoginDialog(loginService);
				ld.setLoginHandler(new LoginHandler() {
					@Override
					public void onLogin(ApplicationData ap) {
						ld.hide();
					}
				});
				ld.center();
			}
		});

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
		addLinkToMenu(menu, "Add Article", PageUrl.article(0));

		return menu;
	}

	private MenuBar getBlogMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Our Homeschool Corner", "");
		addLinkToMenu(menu, "Latest Newsletter", "");
		addLinkToMenu(menu, "Add Article", PageUrl.article(0));

		return menu;
	}

	private MenuBar getBooksMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Book Club", "");
		addLinkToMenu(menu, "Scholastic Newsletter", "");
		addLinkToMenu(menu, "Book Sale", "");

		return menu;
	}

	private MenuBar getCoopMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "Overview", "");
		addLinkToMenu(menu, "LEGO Class", "");
		addLinkToMenu(menu, "Spanish", "");
		addLinkToMenu(menu, "Young Inventors' Program", "");
		addLinkToMenu(menu, "Drama", "");
		addLinkToMenu(menu, "Apologia Physics Lab", "");

		return menu;
	}

	private MenuBar getEventsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Event Calendar", "");
		addLinkToMenu(menu, "Event Registration", "");

		menu.addSeparator(new MenuItemSeparator());
		addLinkToMenu(menu, "Mixed Class Day", "");
		addLinkToMenu(menu, "Mom's Night Out", "");
		addLinkToMenu(menu, "Parents' Support Meeting and Chess Club", "");
		addLinkToMenu(menu, "Physical Education Activities", "");
		addLinkToMenu(menu, "Local Sports Info", "");

		return menu;
	}

	private MenuBar getHomeMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "FAQs", "");
		addLinkToMenu(menu, "Directions", "");
		addLinkToMenu(menu, "Contact Us", "");

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