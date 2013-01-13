package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.LinkPanel;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.client.widgets.StatusPanel;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Accessor class (available through Application) to major page layout components.
 */
public final class Layout {

	class MainLayoutDock extends DockLayoutPanel {
		public MainLayoutDock(Unit unit) {
			super(unit);
			addStyleName("MainLayoutDock");
		}

		@Override
		public Double getWidgetSize(Widget widget) {
			LayoutData ld = (LayoutData) widget.getLayoutData();
			if (ld == null) {
				return 0.0;
			}
			return ld.size;
		}
	}

	private static int HEADER_HEIGHT = 52;
	private static int MENU_HEIGHT = 22;
	private final MainLayoutDock dock = new MainLayoutDock(Unit.PX);
	private final SearchBox searchBox;
	private final SimplePanel mobileBodyPanel = new SimplePanel();
	private final MainMenu menu;
	private final ScrollPanel bodyPanel = new ScrollPanel();
	private final AbsolutePanel ap = new AbsolutePanel();
	// private final User currentUser = Application.getCurrentUser();
	private boolean headerIsVisible = true;
	// Holds a reference to the latest panel queued to be displayed on the page when setPage is called
	private VerticalPanel currentPagePanel;
	private final HorizontalPanel headerPanel = new HorizontalPanel();
	private final HorizontalPanel menuPanel = new HorizontalPanel();
	private boolean isMobileBrowser = false;

	public Layout() {
		isMobileBrowser = ClientUtils.isMobileBrowser();

		headerPanel.setStyleName("headerPanel");
		headerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (!isMobileBrowser) {
			RootLayoutPanel.get().setStyleName("overflowHidden");
		}

		// logo
		Image logo = new Image(MainImageBundle.INSTANCE.logo());

		HTML logoDiv = new HTML("<a href=\"#" + PageUrl.home() + "\">" + logo + "</a>");
		logoDiv.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if ("Home".equals(HistoryToken.getElement("page"))) {
					Application.reloadPage();
				} else {
					HistoryToken.set(PageUrl.home());
				}
			}
		});
		headerPanel.add(logoDiv);

		SimplePanel spacer = new SimplePanel();
		headerPanel.add(spacer);
		headerPanel.setCellWidth(spacer, "100%");

		searchBox = new SearchBox();
		headerPanel.add(searchBox);

		LinkPanel sessionPanel = new LinkPanel();
		headerPanel.add(sessionPanel);
		sessionPanel.addStyleName("sessionPanel");

		ClickLabel logInOrOut = new ClickLabel();
		if (Application.isAuthenticated()) {
			Hyperlink name = new Hyperlink("Hello, " + Application.getCurrentUser().getFirstName(), PageUrl.user(Application.getCurrentUserId()));
			name.addStyleName("nowrap");
			sessionPanel.add(name);

			logInOrOut.setText("Log out");
			logInOrOut.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
					loginService.logout(new Callback<Void>(false) {
						@Override
						protected void doOnSuccess(Void result) {
							Window.Location.reload();
						}
					});
				}
			});
		} else {
			logInOrOut.setText("Log in");
			logInOrOut.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

					if (!GWT.isProdMode()) {
						// loginService.loginAndGetApplicationData("kristin@wearehomeeducators.com", "Redball1", new AsyncCallback<ApplicationData>() {
						loginService.loginAndGetApplicationData("paul.augros@gmail.com", "L33nfiatna", new AsyncCallback<ApplicationData>() {
							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(ApplicationData result) {
								Window.Location.reload();
							}
						});
						return;
					}

					final LoginDialog ld = new LoginDialog(loginService);
					ld.setLoginHandler(new LoginHandler() {
						@Override
						public void onLogin(ApplicationData ap) {
							Window.Location.reload();
						}
					});
					ld.center();
				}
			});
		}
		logInOrOut.setWordWrap(false);
		sessionPanel.add(logInOrOut);

		if (Application.isAuthenticated()) {
			ClickLabel resetLabel = new ClickLabel("Change password", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
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
			resetLabel.setWordWrap(false);
			sessionPanel.add(resetLabel);
		}

		if (!isMobileBrowser) {
			ap.setHeight("100%");
			ap.getElement().getStyle().setOverflow(Overflow.VISIBLE);
			bodyPanel.setStyleName("bodyPanel");
			bodyPanel.add(ap);
		}

		menu = new MainMenu();
		menuPanel.setWidth("100%");
		menuPanel.add(menu);
		menuPanel.setCellWidth(menu, "100%");

		if (isMobileBrowser) {
			mobileBodyPanel.addStyleName("bodyPanel");
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			vp.add(headerPanel);
			vp.add(menuPanel);
			mobileBodyPanel.setWidth("100%");
			vp.add(mobileBodyPanel);
			RootPanel.get().add(vp);
		} else {
			dock.addStyleName("Dock");
			dock.addNorth(headerPanel, HEADER_HEIGHT);
			dock.addNorth(menuPanel, MENU_HEIGHT);
			dock.add(bodyPanel);
			RootLayoutPanel.get().add(dock);
		}

		// status panel
		StatusPanel sp = new StatusPanel();
		Callback.setStatusPanel(sp);
		RootPanel.get().add(sp);
	}

	/**
	 * @return Returns a new {@link VerticalPanel} queued to be displayed as the current page once it is shown by a call to
	 *         <code>Application.getLayout().setPage(panel)</code>, unless preempted by another call to getNewPagePanel
	 */
	public VerticalPanel getNewPagePanel() {
		currentPagePanel = WidgetFactory.createPagePanel();
		return currentPagePanel;
	}

	public VerticalPanel getPagePanel() {
		return currentPagePanel;
	}

	public SearchBox getSearchBox() {
		return searchBox;
	}

	public void setHeaderVisible(boolean visible) {
		if (isMobileBrowser) {
			headerPanel.setVisible(visible);
			return;
		}

		if (visible == headerIsVisible) {
			return;
		}
		if (!visible) {
			dock.setWidgetSize(headerPanel, 0);
			if (menu != null) {
				dock.setWidgetSize(menuPanel, 0);
			}
		} else {
			dock.setWidgetSize(headerPanel, HEADER_HEIGHT);
			if (menu != null) {
				dock.setWidgetSize(menuPanel, MENU_HEIGHT);
			}
		}
		dock.forceLayout();

		headerIsVisible = visible;
	}

	public void setPage(String title, VerticalPanel page) {
		if (currentPagePanel == page) {
			addPageToBodyPanel(title, page);
		}
	}

	private void addPageToBodyPanel(String title, VerticalPanel page) {
		HTML footer = new HTML("&copy; 2005-2012 WHE. All rights reserved. Proprietary & Confidential.");
		footer.setStylePrimaryName("footer");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(page);
		vp.add(footer);

		Application.setTitle(title);
		if (isMobileBrowser) {
			mobileBodyPanel.clear();
			mobileBodyPanel.setWidget(vp);
			Window.scrollTo(0, 0);
		} else {
			ap.clear();
			ap.add(vp);
			bodyPanel.scrollToTop();
		}

	}

}
