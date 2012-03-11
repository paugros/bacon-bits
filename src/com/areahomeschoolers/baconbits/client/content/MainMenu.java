package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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

		addItem("Directories", getDirectoriesMenu());
		addItem("Corporate", getCorporateMenu());
		addItem("Help", getHelpMenu());
	}

	public void execute() {
		// TODO Auto-generated method stub

	}

	public void execute1() {
		Application.printPage();
	}

	private MenuBar getAdminMenu() {
		MenuBar entities = new MenuBar(true);

		MenuBar listings = new MenuBar(true);

		MenuBar quickAdd = new MenuBar(true);

		MenuBar admin = new MenuBar(true);
		admin.addItem("Entities", false, entities);
		admin.addItem("Listings", false, listings);
		admin.addItem("Quick-add", false, quickAdd);

		final boolean devMode = !GWT.isScript();
		admin.addItem(devMode ? "Production Mode" : "Dev Mode", new Command() {
			@Override
			public void execute() {
				String url = (devMode ? "https://dash.dscicorp.com/#" : "https://127.0.0.1:8888/RibEye.html?gwt.codesvr=127.0.0.1:9997#") + History.getToken();
				Window.open(url, "_blank", "");
			}
		});

		admin.addItem("Expire Session", new Command() {
			@Override
			public void execute() {
			}
		});

		admin.addItem("Reload Page", new Command() {
			@Override
			public void execute() {
			}
		});
		return admin;
	}

	private MenuBar getCorporateMenu() {
		MenuBar corporate = new MenuBar(true);
		addLinkToMenu(corporate, "DSCI Email", "https://exchange.dscicorp.com/exchange/");
		addLinkToMenu(corporate, "DSCI VPN", "https://vpn.dsci-net.com:8080/");

		return corporate;
	}

	private MenuBar getDirectoriesMenu() {
		MenuBar business = new MenuBar(true);
		return business;
	}

	private MenuBar getHelpMenu() {
		MenuBar menu = new MenuBar(true);

		menu.addItem("About Dash", new Command() {
			@Override
			public void execute() {
				DefaultDialog aboutDialog = new DefaultDialog();
				aboutDialog.setText("About Dash");

				VerticalPanel mainPanel = new VerticalPanel();
				mainPanel.setWidth("260px");
				mainPanel.setSpacing(10);

				Image logo = new Image();
				mainPanel.add(logo);

				Grid dataGrid = new Grid(2, 2);
				dataGrid.setText(0, 0, "Version:");

				DateTimeFormat formatter = Formatter.DEFAULT_DATE_TIME_FORMAT;
				dataGrid.setText(0, 1, Integer.toString(Application.getBuildNumber()) + " (" + formatter.format(Application.getBuildDate()) + ")");

				dataGrid.setText(1, 0, "Next release:");
				dataGrid.setText(1, 1, Formatter.formatDate(Application.getNextReleaseDate()));
				mainPanel.add(dataGrid);

				mainPanel.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
				mainPanel.setCellHorizontalAlignment(dataGrid, HasHorizontalAlignment.ALIGN_CENTER);

				HTML footer = new HTML("&copy; 2005-2012 DSCI Corporation.<br>All rights reserved.<br>Proprietary & Confidential.");
				footer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				footer.getElement().getStyle().setColor("#777777");
				mainPanel.add(footer);

				mainPanel.add(new ButtonPanel(aboutDialog));

				aboutDialog.setWidget(mainPanel);

				aboutDialog.center();
			}
		});

		MenuBar kbMenu = new MenuBar(true);

		menu.addItem("FAQs", false, kbMenu);

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